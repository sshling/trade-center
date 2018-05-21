package cn.shaolingweb.rml.tradecenter.service.auth;


import cn.shaolingweb.rml.tradecenter.domain.auth.Privilege;
import cn.shaolingweb.rml.tradecenter.domain.auth.Role;
import cn.shaolingweb.rml.tradecenter.domain.auth.UserRole;
import cn.shaolingweb.rml.tradecenter.domain.page.Pagination;

import java.util.Collection;
import java.util.List;

public interface RoleService {
    Collection<Role> find(String pin, Integer type);

    Collection<Privilege> findPrivilegesByRoleId(Integer roleId);

    Collection<Privilege> findPrivilegesByRoleId(Integer roleId, Pagination pg);

    int countPrivilegesByRoleId(String roleId, Pagination pg);

    List<String> findResourcePriviCodes(String pin, Integer resId);

    Collection<String> findPrivilegeCodesByPin(String pin);

    Collection<Privilege> findNotOwnPrivilegesByRoleId(Integer roleId);

    void setPrivilegesByRoleId(Integer[] privilegeIds, Integer roleId);

    void insUserRole(UserRole role);

    List<String> findAllUsersByClusterId(String resId);

    List<String> findModelPrivileges(String pin);

    Collection<Role> findNotOwnRoles(String pin, Integer type);

    void deletePersist(String name);

    List<Role> findRole(Role role);

    List<Role> queryByErp(String pin, Pagination pagination);

    List<Role> queryNotLinkByErp(String pin, Pagination pagination);

    int countByErp(String pin, Pagination pg);

    int countNotLinkByErp(String pin, Pagination pg);

    String saveUserRoles(String pin, String roleIds);

    void deleteUserRoles(String pin, List<String> roleIds);

    List<Role> listRoles(Pagination pagination);

    int countRoles(Pagination pg);

    String insertRole(Role role);

    Role getRoleById(String id);

    void updateRole(Role role);

    Integer authUpdate(String srcIds, String desId, String type);

    void deleteRoles(List<String> strings);

    void deleteRolePrivileges(String roleId, List<String> ids);

    List<Privilege> queryPrivilegeNotLinkByRoleId(String roleId, Pagination pagination);

    int countPrivilegeNotLinkByRoleId(String roleId, Pagination pg);

    String saveRolePrivileges(String roleId, String privilegeIds);

    boolean hasSuperRole();

    List<Integer> findIndicesByErp(String pin);

    List<Integer> findClustersByErp(String pin);

    void insertUserCluster(String pin, List<Integer> resIds);

    void deleteUserClusters(String pin, List<String> ids);

    int deleteUserClustersSimple(String pin, String cid);
}
