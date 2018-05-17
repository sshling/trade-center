
package cn.shaolingweb.rml.tradecenter.service.auth;

import cn.shaolingweb.rml.tradecenter.domain.auth.User;
import cn.shaolingweb.rml.tradecenter.domain.page.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {

    /**
     * 返回错误消息
     */
    String insertUser(User user);

    int updateUser(User user);

    int deleteUser(String userIds);

    int deleteUsers(List<String> userIds);

    List<User> listUser(Pagination pg);

    int countUser(Pagination pg);

    User selectById(String id);

    Boolean isSuperAdmin(String pin);

    List<User> queryUserByClusterId(@Param("clusterId") Integer clusterId);

    List<User> getUsersByErps(List<String> erps);

    User getUserByErp(String erp);

    boolean isExist(String pin);


}
