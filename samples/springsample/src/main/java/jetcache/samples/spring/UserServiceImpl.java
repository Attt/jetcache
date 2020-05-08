/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
@Repository
public class UserServiceImpl implements UserService {

    @Override
    public User loadUser(long userId) {
        System.out.println("load user: " + userId);
        User user = new User();
        user.setUserId(userId);
        user.setUserName("user" + userId);
        return user;
    }

    @Override
    public List<User> fetchUsers(int pageId, int pageSize) {
        List<User> userList = new ArrayList<>();
        for(int i = (pageId-1)*pageSize;i<pageId * pageSize;i++){
            User user = new User();
            user.setUserName("user" + i);
            user.setUserId(i);
            userList.add(user);
        }
        return userList;
    }

    @Override
    public List<User> fetchUsers0(Page page) {
        return fetchUsers(page.getPageNumber().intValue(), page.getPageSize().intValue());
    }
}
