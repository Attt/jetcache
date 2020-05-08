/**
 * Created on 2018/8/11.
 */
package jetcache.samples.spring;

import com.alicp.jetcache.anno.ReferKey;

import java.io.Serializable;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class User implements Serializable {
    private static final long serialVersionUID = -5157877924507849953L;

    @ReferKey
    private long userId;
    private String userName;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
