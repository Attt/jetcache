package jetcache.samples.spring;

import java.util.List;

/**
 * jetcache-parent
 *
 * @author atpexgo.wu
 * @date 2020-05-07 18:28
 */
public class UserPage {

    private List<User> userList;

    private int total;

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
