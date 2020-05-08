package com.alicp.jetcache.redis;

import com.alicp.jetcache.*;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * jetcache-parent
 *
 * @author atpexgo.wu
 * @date 2020-05-08 15:01
 */
public class SortedSetRedisCache<K, V> extends RedisCache<K, V> {

    private RedisCacheConfig<K, V> config;

    Function<Object, byte[]> valueEncoder;
    Function<byte[], Object> valueDecoder;

    public SortedSetRedisCache(RedisCacheConfig<K, V> config) {
        super(config);
        this.config = config;
        this.valueEncoder = config.getValueEncoder();
        this.valueDecoder = config.getValueDecoder();
    }

    private String doGetLua = "local result = {}\n" +
            "result[0] = redis.call('get', KEYS[2])\n" +
            "result[1] = redis.call('zrange',KEYS[1], 0, -1)\n" +
            "return result";

    private String doPutLua = "redis.call('set', KEYS[2], ARGV[1])\n" +
            "redis.call('expire', KEYS[2], tonumber(ARGV[2]))\n" +
            "redis.call('delete', KEYS[1])\n" +
            "for i, v in pairs(ARGV) do\n" +
            "    if i > 4 then\n" +
            "        redis.call('zadd', KEYS[1], tonumber(ARGV[3]), v)\n" +
            "    end\n" +
            "end\n" +
            "redis.call('expire', KEYS[1], tonumber(ARGV[2]))\n" +
            "return 'ok'";

    @Override
    protected CacheGetResult<V> do_GET(K key) {
        try (Jedis jedis = getReadPool().getResource()) {
            byte[] newKey = buildKey(key);
            Object[] result = (Object[])jedis.evalsha(jedis.scriptLoad(doGetLua).getBytes(), Arrays.asList(newKey, (new String(newKey) + "$$$Holder").getBytes()), new ArrayList<>());

            byte[] holderBytes = (byte[])result[0];
            if(holderBytes != null){
                CacheValueHolder<V> holder = (CacheValueHolder<V>) valueDecoder.apply(holderBytes);
                if (System.currentTimeMillis() >= holder.getExpireTime()) {
                    return CacheGetResult.EXPIRED_WITHOUT_MSG;
                }
                Object[] values = (Object[]) result[1];
                List<Object> valueList = new ArrayList<>();
                for (Object value : values) {
                    byte[] valueBytes = (byte[]) value;
                    valueList.add(valueDecoder.apply(valueBytes));
                }
                CacheValueHolder<V> valueHolder = new CacheValueHolder(valueList, Integer.MAX_VALUE * 1000L);
                return new CacheGetResult(CacheResultCode.SUCCESS, null, valueHolder);
            }else {
                return CacheGetResult.NOT_EXISTS_WITHOUT_MSG;
            }
        } catch (Exception ex) {
            logError("GET", key, ex);
            return new CacheGetResult(ex);
        }
    }

    @Override
    protected MultiGetResult<K, V> do_GET_ALL(Set<? extends K> keys) {
        return super.do_GET_ALL(keys);
    }

    @Override
    protected CacheResult do_PUT(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        try (Jedis jedis = config.getJedisPool().getResource()) {
            CacheValueHolder<V> holder = new CacheValueHolder("wow", timeUnit.toMillis(expireAfterWrite));
            byte[] newKey = buildKey(key);
            // todo score
            double score = 0;
            Object[] valueObjectArray;
            if(value instanceof Collection){
                valueObjectArray = ((Collection) value).toArray();
            }else {
                valueObjectArray = (Object[]) value;
            }
            List<byte[]> argBytes = new ArrayList<>();
            argBytes.add(valueEncoder.apply(holder));
            argBytes.add(String.valueOf(timeUnit.toMillis(expireAfterWrite)).getBytes());
            argBytes.add(String.valueOf(score).getBytes());
            for (Object o : valueObjectArray) {
                argBytes.add(valueEncoder.apply(o));
            }
            Object result = jedis.evalsha(jedis.scriptLoad(doPutLua).getBytes(), Arrays.asList(newKey, (new String(newKey) + "$$$Holder").getBytes()),
                    argBytes);
            if ("OK".equals(String.valueOf(result))) {
                return CacheResult.SUCCESS_WITHOUT_MSG;
            } else {
                return new CacheResult(CacheResultCode.FAIL, "");
            }
        } catch (Exception ex) {
            logError("PUT", key, ex);
            return new CacheResult(ex);
        }
    }

    @Override
    protected CacheResult do_PUT_ALL(Map<? extends K, ? extends V> map, long expireAfterWrite, TimeUnit timeUnit) {
        return super.do_PUT_ALL(map, expireAfterWrite, timeUnit);
    }

    @Override
    protected CacheResult do_REMOVE(K key) {
        return super.do_REMOVE(key);
    }

    @Override
    protected CacheResult do_REMOVE_ALL(Set<? extends K> keys) {
        return super.do_REMOVE_ALL(keys);
    }

    @Override
    protected CacheResult do_PUT_IF_ABSENT(K key, V value, long expireAfterWrite, TimeUnit timeUnit) {
        return super.do_PUT_IF_ABSENT(key, value, expireAfterWrite, timeUnit);
    }
}
