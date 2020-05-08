/**
 * Created on  13-09-20 22:01
 */
package com.alicp.jetcache.anno.method;

import com.alicp.jetcache.CacheConfigException;
import com.alicp.jetcache.RefreshPolicy;
import com.alicp.jetcache.anno.*;
import com.alicp.jetcache.anno.support.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class CacheConfigUtil {
    private static CachedAnnoConfig parseCached(Method m) {
        Cached cached = m.getAnnotation(Cached.class);
        if (cached == null) {
            return null;
        }
        CachedAnnoConfig cc = new CachedAnnoConfig();
        cc.setArea(cached.area());
        cc.setName(cached.name());
        cc.setCacheType(cached.cacheType());
        cc.setEnabled(cached.enabled());
        cc.setTimeUnit(cached.timeUnit());
        cc.setExpire(cached.expire());
        cc.setLocalExpire(cached.localExpire());
        cc.setLocalLimit(cached.localLimit());
        cc.setCacheNullValue(cached.cacheNullValue());
        cc.setCondition(cached.condition());
        cc.setPostCondition(cached.postCondition());
        cc.setSerialPolicy(cached.serialPolicy());
        cc.setKeyConvertor(cached.keyConvertor());
        cc.setKey(cached.key());
        cc.setDefineMethod(m);

        CacheRefresh cacheRefresh = m.getAnnotation(CacheRefresh.class);
        if (cacheRefresh != null) {
            RefreshPolicy policy = parseRefreshPolicy(cacheRefresh);
            cc.setRefreshPolicy(policy);
        }

        CachePenetrationProtect protectAnno = m.getAnnotation(CachePenetrationProtect.class);
        if (protectAnno != null) {
            PenetrationProtectConfig protectConfig = parsePenetrationProtectConfig(protectAnno);
            cc.setPenetrationProtectConfig(protectConfig);
        }

        return cc;
    }

    private static FirstPageCachedAnnoConfig parseFirstPageCached(Method m) {
        FirstPageCached firstPageCached = m.getAnnotation(FirstPageCached.class);
        if (firstPageCached == null) {
            return null;
        }
        FirstPageCachedAnnoConfig fpcc = new FirstPageCachedAnnoConfig();
        fpcc.setArea(firstPageCached.area());
        fpcc.setName(firstPageCached.name());
        fpcc.setCacheType(firstPageCached.cacheType());
        fpcc.setEnabled(firstPageCached.enabled());
        fpcc.setTimeUnit(firstPageCached.timeUnit());
        fpcc.setExpire(firstPageCached.expire());
        fpcc.setLocalExpire(firstPageCached.localExpire());
        fpcc.setLocalLimit(firstPageCached.localLimit());
        fpcc.setCacheNullValue(firstPageCached.cacheNullValue());
        fpcc.setCondition(firstPageCached.condition());
        fpcc.setPostCondition(firstPageCached.postCondition());
        fpcc.setSerialPolicy(firstPageCached.serialPolicy());
        fpcc.setKeyConvertor(firstPageCached.keyConvertor());
        fpcc.setKey(firstPageCached.key());
        fpcc.setFetchPageNumber(ClassUtil.fetchPageNumberByAnnotation());
        // todo
        fpcc.setEntityLoader(firstPageCached.entityLoader());
        fpcc.setDefineMethod(m);

        CacheRefresh cacheRefresh = m.getAnnotation(CacheRefresh.class);
        if (cacheRefresh != null) {
            RefreshPolicy policy = parseRefreshPolicy(cacheRefresh);
            fpcc.setRefreshPolicy(policy);
        }

        CachePenetrationProtect protectAnno = m.getAnnotation(CachePenetrationProtect.class);
        if (protectAnno != null) {
            PenetrationProtectConfig protectConfig = parsePenetrationProtectConfig(protectAnno);
            fpcc.setPenetrationProtectConfig(protectConfig);
        }

        return fpcc;
    }

    public static PenetrationProtectConfig parsePenetrationProtectConfig(CachePenetrationProtect protectAnno) {
        PenetrationProtectConfig protectConfig = new PenetrationProtectConfig();
        protectConfig.setPenetrationProtect(protectAnno.value());
        if (!CacheConsts.isUndefined(protectAnno.timeout())) {
            long timeout = protectAnno.timeUnit().toMillis(protectAnno.timeout());
            protectConfig.setPenetrationProtectTimeout(Duration.ofMillis(timeout));
        }
        return protectConfig;
    }

    public static RefreshPolicy parseRefreshPolicy(CacheRefresh cacheRefresh) {
        RefreshPolicy policy = new RefreshPolicy();
        TimeUnit t = cacheRefresh.timeUnit();
        policy.setRefreshMillis(t.toMillis(cacheRefresh.refresh()));
        if (!CacheConsts.isUndefined(cacheRefresh.stopRefreshAfterLastAccess())) {
            policy.setStopRefreshAfterLastAccessMillis(t.toMillis(cacheRefresh.stopRefreshAfterLastAccess()));
        }
        if (!CacheConsts.isUndefined(cacheRefresh.refreshLockTimeout())) {
            policy.setRefreshLockTimeoutMillis(t.toMillis(cacheRefresh.refreshLockTimeout()));
        }
        return policy;
    }

    public static List<CacheInvalidateAnnoConfig> parseCacheInvalidates(Method m) {
        List<CacheInvalidateAnnoConfig> annoList = null;
        CacheInvalidate ci = m.getAnnotation(CacheInvalidate.class);
        if (ci != null) {
            annoList = new ArrayList<>(1);
            annoList.add(createCacheInvalidateAnnoConfig(ci, m));
        } else {
            CacheInvalidateContainer cic = m.getAnnotation(CacheInvalidateContainer.class);
            if (cic != null) {
                CacheInvalidate[] cacheInvalidates = cic.value();
                annoList = new ArrayList<>(cacheInvalidates.length);
                for (CacheInvalidate cacheInvalidate : cacheInvalidates) {
                    annoList.add(createCacheInvalidateAnnoConfig(cacheInvalidate, m));
                }
            }
        }
        return annoList;
    }

    private static CacheInvalidateAnnoConfig createCacheInvalidateAnnoConfig(CacheInvalidate anno, Method m) {
        CacheInvalidateAnnoConfig cc = new CacheInvalidateAnnoConfig();
        cc.setArea(anno.area());
        cc.setName(anno.name());
        if (cc.getName() == null || cc.getName().trim().equals("")) {
            throw new CacheConfigException("name is required for @CacheInvalidate: " + m.getClass().getName() + "." + m.getName());
        }
        cc.setKey(anno.key());
        cc.setCondition(anno.condition());
        cc.setMulti(anno.multi());
        cc.setDefineMethod(m);
        return cc;
    }

    private static CacheUpdateAnnoConfig parseCacheUpdate(Method m) {
        CacheUpdate anno = m.getAnnotation(CacheUpdate.class);
        if (anno == null) {
            return null;
        }
        CacheUpdateAnnoConfig cc = new CacheUpdateAnnoConfig();
        cc.setArea(anno.area());
        cc.setName(anno.name());
        if (cc.getName() == null || cc.getName().trim().equals("")) {
            throw new CacheConfigException("name is required for @CacheUpdate: " + m.getClass().getName() + "." + m.getName());
        }
        cc.setKey(anno.key());
        cc.setValue(anno.value());
        if (cc.getValue() == null || cc.getValue().trim().equals("")) {
            throw new CacheConfigException("value is required for @CacheUpdate: " + m.getClass().getName() + "." + m.getName());
        }
        cc.setCondition(anno.condition());
        cc.setMulti(anno.multi());
        cc.setDefineMethod(m);
        return cc;
    }


    private static boolean parseEnableCache(Method m) {
        EnableCache anno = m.getAnnotation(EnableCache.class);
        return anno != null;
    }

    public static boolean parse(CacheInvokeConfig cac, Method method) {
        boolean hasAnnotation = false;
        CachedAnnoConfig cachedConfig = parseCached(method);
        FirstPageCachedAnnoConfig firstPageCachedAnnoConfig = parseFirstPageCached(method);
        if (firstPageCachedAnnoConfig != null) {
            cac.setCachedAnnoConfig(firstPageCachedAnnoConfig);
            hasAnnotation = true;
        }

        if (cachedConfig != null) {
            cac.setCachedAnnoConfig(cachedConfig);
            hasAnnotation = true;
        }

        boolean enable = parseEnableCache(method);
        if (enable) {
            cac.setEnableCacheContext(true);
            hasAnnotation = true;
        }
        List<CacheInvalidateAnnoConfig> invalidateAnnoConfigs = parseCacheInvalidates(method);
        if (invalidateAnnoConfigs != null) {
            cac.setInvalidateAnnoConfigs(invalidateAnnoConfigs);
            hasAnnotation = true;
        }
        CacheUpdateAnnoConfig updateAnnoConfig = parseCacheUpdate(method);
        if (updateAnnoConfig != null) {
            cac.setUpdateAnnoConfig(updateAnnoConfig);
            hasAnnotation = true;
        }

        if (cachedConfig != null && (invalidateAnnoConfigs != null || updateAnnoConfig != null)) {
            throw new CacheConfigException("@Cached can't coexists with @CacheInvalidate or @CacheUpdate: " + method);
        }

        return hasAnnotation;
    }
}
