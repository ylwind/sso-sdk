package site.ripic.sso.strategy;

import org.apache.commons.lang3.StringUtils;
import site.ripic.sso.SsoManager;
import site.ripic.sso.acl.AccessInterface;
import site.ripic.sso.enums.LoginFailReason;
import site.ripic.sso.exception.InvalidTokenException;
import site.ripic.sso.exception.UserStatusException;
import site.ripic.sso.listener.SsoListenerCenter;
import site.ripic.sso.model.LoginModel;
import site.ripic.sso.storage.TokenStorage;
import site.ripic.sso.token.TokenService;

import java.util.*;

/**
 * 逻辑类策略相关
 */
public class LogicStrategy {

    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // ---- token相关

    /**
     * 获取当前会话下的可信token
     *
     * @return token，如果token无效则抛出异常
     */
    public String getToken() {
        String currentToken = threadLocal.get();
        if (currentToken == null) {
            synchronized (this) { // 或使用其他锁机制来保护临界区
                currentToken = threadLocal.get();
                if (currentToken == null) {
                    currentToken = SsoManager.getTokenStorage().getToken();
                    validToken(currentToken);
                    threadLocal.set(currentToken);
                }
            }
        }
        return currentToken;
    }

    /**
     * 提供一种方案，手动修改当前请求的token值，但是不建议使用
     *
     * @param threadLocal 待设置的token
     */
    @Deprecated
    public void setThreadLocal(String threadLocal) {
        this.threadLocal.set(threadLocal);
    }


    /**
     * 验证token
     *
     * @param token token
     * @throws InvalidTokenException token无效则抛出异常
     */
    private void validToken(String token) throws InvalidTokenException {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("token is invalid");
        }
        TokenService tokenService = SsoManager.getTokenService();
        Long tokenId = tokenService.getTokenIdByToken(token);
        tokenService.verifyToken(token);
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        boolean tokenValid = tokenStorage.isTokenValid(tokenId);
        if (!tokenValid) {
            throw new InvalidTokenException("token is invalid");
        }
    }

    /**
     * 获取当前会话下的可信tokenInfo
     *
     * @return LoginModel，如果不可信或无效则抛出异常
     * @throws InvalidTokenException token无效则抛出异常
     */
    public LoginModel getTokenInfo() throws InvalidTokenException {
        // 获取当前登录的tokenId
        String token = getToken();
        TokenService tokenService = SsoManager.getTokenService();
        Long tokenId = tokenService.getTokenIdByToken(token);
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        return tokenStorage.getLoginModelByTokenId(tokenId);
    }

    /**
     * 获取当前会话下的userid
     *
     * @return userid，如果不可信或为空则抛出异常
     * @throws InvalidTokenException token无效则抛出异常
     */
    public Long getUserid() throws InvalidTokenException {
        return getTokenInfo().getUserid();
    }

    /**
     * 判断是否登录
     *
     * @return 如果登录则返回true，否则返回false；此方法不会抛出异常
     */
    public boolean isLogin() {
        try {
            return getUserid() != null;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    /**
     * 获取的token过期时间
     *
     * @return token过期时间，单位秒，不存在则返回0
     */
    public Long getTokenTimeOut(Long tokenId) {
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        return tokenStorage.getTtl(tokenId);
    }

    /**
     * 获取当前会话的token过期时间
     *
     * @return token过期时间，单位秒，不存在则返回0
     * @throws InvalidTokenException token无效则抛出异常
     */
    public Long getTokenTimeOut() throws InvalidTokenException {
        return getTokenTimeOut(getTokenInfo().getTokenId());
    }

    // ----- 登录相关操作

    /**
     * 登录操作
     *
     * @param userid     userid
     * @param loginModel 登录实体
     * @return token
     */
    public String login(Long userid, LoginModel loginModel) {
        // 首先检查默认值
        if (userid == null) {
            throw new IllegalArgumentException("userid is null");
        }
        loadGlobalConfig(loginModel);
        // 检查用户状态
        checkUserStatus(userid, loginModel);
        String token = SsoStrategy.instance.createLoginToken.apply(userid, loginModel);
        // 存储token
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        tokenStorage.saveToken(userid, loginModel, loginModel.getTimeout());
        // 这里可以考虑更新本地threadLocal，为了方便，这里直接进行删除，强制刷新一下
        threadLocal.remove();
        // 发送通知
        SsoListenerCenter.loginSuccess(userid, loginModel);
        return token;
    }

    /**
     * 登出账号
     * * @param tokenId 需要登出的tokenId
     */
    public void logout(List<Long> tokenId) {
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        tokenStorage.removeToken(tokenId);
    }

    /**
     * 登出当前账户
     */
    public void logout() {
        LoginModel tokenInfo = this.getTokenInfo();
        logout(Collections.singletonList(tokenInfo.getTokenId()));
    }

    /**
     * userid维度登出账户
     *
     * @param userid userid
     */
    public void logout(long userid) {
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        tokenStorage.removeAllTokenByUserid(userid);
    }

    /**
     * 加载全局配置
     * 填充默认超时时间、填充tokenid
     *
     * @param loginModel loginModel
     */
    private void loadGlobalConfig(LoginModel loginModel) {
        // 读取全局配置
        if (loginModel == null) {
            loginModel = new LoginModel();
        }
        if (loginModel.getTimeout() == null) {
            loginModel.setTimeout(SsoManager.getConfig().getDefaultTimeOut());
        }
        if (loginModel.getTokenId() == null) {
            loginModel.setTokenId(SsoManager.getTokenStorage().getNewTokenId());
        }
    }

    /**
     * 检查用户状态
     * 如果检查失败，会发送登录失败通知
     *
     * @param userid     userid
     * @param loginModel 登录实体
     */
    private void checkUserStatus(long userid, LoginModel loginModel) {
        AccessInterface access = SsoManager.getAccessInterface();
        if (access.isUserDisabled(userid)) {
            SsoListenerCenter.loginFail(userid, loginModel, LoginFailReason.USER_DISABLED);
            throw new UserStatusException(LoginFailReason.USER_DISABLED.getUserTip());
        }
        if (access.isUserExpired(userid)) {
            SsoListenerCenter.loginFail(userid, loginModel, LoginFailReason.USER_EXPIRED);
            throw new UserStatusException(LoginFailReason.USER_EXPIRED.getUserTip());
        }
        if (access.isUserLocked(userid)) {
            SsoListenerCenter.loginFail(userid, loginModel, LoginFailReason.USER_LOCKED);
            throw new UserStatusException(LoginFailReason.USER_LOCKED.getUserTip());
        }
    }

    // -- 权限相关

    /**
     * 获取用户权限
     *
     * @param userid userid
     * @return 用户权限列表
     */
    public List<String> getUserPermissionList(long userid) {
        return SsoManager.getAccessInterface().getUserPermissionList(userid);
    }

    /**
     * 获取用户角色
     *
     * @param userid userid
     * @return 权限列表
     */
    public List<String> getUserRoleList(long userid) {
        return SsoManager.getAccessInterface().getUserRoleList(userid);
    }

    /**
     * 获取当前用户权限
     *
     * @return 权限列表
     * @throws InvalidTokenException token无效则抛出异常
     */
    public List<String> getUserPermissionList() throws InvalidTokenException {
        try {
            return this.getUserPermissionList(this.getUserid());
        } catch (InvalidTokenException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 获取当前用户角色
     *
     * @return 角色列表
     * @throws InvalidTokenException token无效则抛出异常
     */
    public List<String> getUserRoleList() throws InvalidTokenException {
        try {
            return this.getUserRoleList(this.getUserid());
        } catch (InvalidTokenException e) {
            return Collections.emptyList();
        }
    }

    /**
     * 判断用户是否拥有权限
     *
     * @param permission 权限
     * @param userid     userid
     * @return true or false
     */
    public boolean hasPermission(String permission, long userid) {
        return getUserPermissionList(userid).contains(permission);
    }

    /**
     * 判断用户是否拥有权限
     *
     * @param permission 权限
     * @return true or false
     * @throws InvalidTokenException token无效则抛出异常
     */
    public boolean hasPermission(String permission) throws InvalidTokenException {
        return hasPermission(permission, this.getUserid());
    }

    /**
     * 判断用户是否拥有角色
     *
     * @param role   角色
     * @param userid userid
     * @return true or false
     */
    public boolean hasRole(String role, long userid) {
        return this.getUserRoleList(userid).contains(role);
    }

    /**
     * 判断当前会话用户是否拥有角色
     *
     * @param role 角色
     * @return true or false
     * @throws InvalidTokenException token无效则抛出异常
     */
    public boolean hasRole(String role) throws InvalidTokenException {
        return this.hasRole(role, this.getUserid());
    }

    /**
     * 判断用户是否拥有所有权限
     *
     * @param permissionList 权限列表
     * @param userid         userid
     * @return true or false
     */
    public boolean hasAllPermission(List<String> permissionList, long userid) {
        if (permissionList == null || permissionList.isEmpty()) {
            return true;
        }
        return new HashSet<>(getUserPermissionList(userid)).containsAll(permissionList);
    }

    /**
     * 判断当前会话用户是否拥有所有权限
     *
     * @param permissionList 权限列表
     * @return true or false
     * @throws InvalidTokenException token无效则抛出异常
     */
    public boolean hasAllPermission(List<String> permissionList) throws InvalidTokenException {
        return this.hasAllPermission(permissionList, this.getUserid());
    }

    /**
     * 判断用户是否拥有所有角色
     *
     * @param roleList 角色列表
     * @param userid   userid
     * @return true or false
     */
    public boolean hasAllRole(List<String> roleList, long userid) {
        if (roleList == null || roleList.isEmpty()) {
            return true;
        }
        return new HashSet<>(getUserRoleList(userid)).containsAll(roleList);
    }

    /**
     * 判断当前会话用户是否拥有所有角色
     *
     * @param roleList 角色列表
     * @return true or false
     * @throws InvalidTokenException token无效则抛出异常
     */
    public boolean hasAllRole(List<String> roleList) throws InvalidTokenException {
        return this.hasAllRole(roleList, this.getUserid());
    }

    /**
     * 判断用户是否拥有任意权限
     *
     * @param permissionList 权限列表
     * @param userid         userid
     * @return true or false
     */
    public boolean hasAnyPermission(List<String> permissionList, long userid) {
        if (permissionList == null || permissionList.isEmpty()) {
            return true;
        }
        return new HashSet<>(getUserPermissionList(userid)).stream().anyMatch(permissionList::contains);
    }

    /**
     * 判断当前会话用户是否拥有任意权限
     *
     * @param permissionList 权限列表
     * @return true or false
     */
    public boolean hasAnyPermission(List<String> permissionList) throws InvalidTokenException {
        return hasAnyPermission(permissionList, this.getUserid());
    }

    /**
     * 判断用户是否拥有任意角色
     *
     * @param roleList 角色列表
     * @param userid   userid
     * @return true or false
     */
    public boolean hasAnyRole(List<String> roleList, long userid) {
        if (roleList == null || roleList.isEmpty()) {
            return true;
        }
        return new HashSet<>(getUserRoleList(userid)).stream().anyMatch(roleList::contains);
    }

    /**
     * 判断当前会话用户是否拥有任意角色
     *
     * @param roleList 角色列表
     * @return true or false
     * @throws InvalidTokenException token无效则抛出异常
     */
    public boolean hasAnyRole(List<String> roleList) throws InvalidTokenException {
        return hasAnyRole(roleList, this.getUserid());
    }

    public boolean hasAllPermission(String... permissionList) throws InvalidTokenException {
        return hasAllPermission(Arrays.asList(permissionList));
    }

    public boolean hasAllRole(String... roleList) throws InvalidTokenException {
        return hasAllRole(Arrays.asList(roleList));
    }

    public boolean hasAnyPermission(String... permissionList) throws InvalidTokenException {
        return hasAnyPermission(Arrays.asList(permissionList));
    }

    public boolean hasAnyRole(String... roleList) throws InvalidTokenException {
        return hasAnyRole(Arrays.asList(roleList));
    }

    public boolean hasAllPermission(long userid, String... permissionList) throws InvalidTokenException {
        return hasAllPermission(Arrays.asList(permissionList), userid);
    }

    public boolean hasAllRole(long userid, String... roleList) throws InvalidTokenException {
        return hasAllRole(Arrays.asList(roleList), userid);
    }

    public boolean hasAnyPermission(long userid, String... permissionList) throws InvalidTokenException {
        return hasAnyPermission(Arrays.asList(permissionList), userid);
    }

    public boolean hasAnyRole(long userid, String... roleList) throws InvalidTokenException {
        return hasAnyRole(Arrays.asList(roleList), userid);
    }
}
