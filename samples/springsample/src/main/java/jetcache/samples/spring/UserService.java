/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import com.alicp.jetcache.anno.*;

import java.util.List;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public interface UserService {
    @Cached(name = "loadUser", expire = 10)
    User loadUser(long userId);

    @FirstPageCached(name = "fetchUsers", entityLoader = "loadUser",expire = 20, keyConvertor = KeyConvertor.FASTJSON)
    List<User> fetchUsers(@PageId int pageId, int pageSize);


    @FirstPageCached(name = "fetchUsers0", entityLoader = "loadUser", expire = 20, keyConvertor = KeyConvertor.FASTJSON)
    List<User> fetchUsers0(@PageParameter Page page);


}
