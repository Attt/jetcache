/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CreateCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
@Component
public class MyServiceImpl implements MyService {
    @CreateCache(name = "myServiceCache", expire = 60)
    private Cache<String, String> cache;

    @Autowired
    private UserService userService;

    @Override
    public void createCacheDemo() {
        cache.put("myKey", "myValue");
        String myValue = cache.get("myKey");
        System.out.println("get 'myKey' from cache:" + myValue);
    }

    @Override
    public void cachedDemo() {
//        userService.loadUser(1);
//        userService.loadUser(1);
        System.out.println(userService.fetchUsers(1, 20));
        System.out.println(userService.fetchUsers(2, 20));
        System.out.println(userService.fetchUsers(1, 20));

        Page page = new Page();
        page.setPageSize(20L);
        page.setPageNumber(1L);
        System.out.println(userService.fetchUsers0(page));
        page.setPageNumber(2L);
        System.out.println(userService.fetchUsers0(page));
        page.setPageNumber(1L);
        System.out.println(userService.fetchUsers0(page));
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
