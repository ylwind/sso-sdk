package site.ripic.sso.acl;

import java.util.List;

/**
 * acl接口
 * 用户手动实现，用于获取用户权限，获取用户状态的接口
 */
public interface AccessInterface {

    /**
     * 获取权限列表
     *
     * @param userid userid
     * @return 权限列表
     */
    List<String> getUserPermissionList(Long userid);

    /**
     * 获取角色列表
     *
     * @param userid userid
     * @return 角色列表
     */
    List<String> getUserRoleList(Long userid);

    /**
     * 检查用户是否有权限
     *
     * @param userid         userid
     * @param permissionList 权限列表
     * @return true：有权限，false：无权限
     */
    boolean checkPermission(long userid, List<String> permissionList);

    /**
     * 检查用户是否有角色
     *
     * @param userid   userid
     * @param roleList 角色列表
     * @return true:有角色，false:无角色
     */
    boolean checkRole(long userid, List<String> roleList);

    /**
     * 检查用户是否被锁定
     *
     * @param userid userid
     * @return true:被锁定，false:未锁定
     */
    boolean isUserLocked(Long userid);

    /**
     * 检查用户是否过期
     *
     * @param userid userid
     * @return true:过期，false:未过期
     */
    boolean isUserExpired(Long userid);

    /**
     * 检查用户是否被禁用
     *
     * @param userid userid
     * @return true:禁用，false:未禁用
     */
    boolean isUserDisabled(Long userid);
}
