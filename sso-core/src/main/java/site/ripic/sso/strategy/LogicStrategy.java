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

    private void validToken(String token) {
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
     */
    public LoginModel getTokenInfo() {
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
     */
    public Long getUserid() {
        return getTokenInfo().getUserid();
    }

    public boolean isLogin() {
        try {
            return getUserid() != null;
        } catch (InvalidTokenException e) {
            return false;
        }
    }

    public Long getTokenTimeOut(Long tokenId) {
        TokenStorage tokenStorage = SsoManager.getTokenStorage();
        return tokenStorage.getTtl(tokenId);
    }

    public Long getTokenTimeOut() {
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
    public List<String> getUserPermissionList(long userid) {
        return SsoManager.getAccessInterface().getUserPermissionList(userid);
    }

    public List<String> getUserRoleList(long userid) {
        return SsoManager.getAccessInterface().getUserRoleList(userid);
    }

    public List<String> getUserPermissionList() {
        try {
            return SsoManager.getAccessInterface().getUserPermissionList(this.getUserid());
        } catch (InvalidTokenException e) {
            return Collections.emptyList();
        }
    }

    public List<String> getUserRoleList() {
        try {
            return SsoManager.getAccessInterface().getUserRoleList(this.getUserid());
        } catch (InvalidTokenException e) {
            return Collections.emptyList();
        }
    }

    public boolean hasPermission(String permission, long userid) {
        return getUserPermissionList(userid).contains(permission);
    }

    public boolean hasPermission(String permission) {
        return getUserPermissionList().contains(permission);
    }

    public boolean hasRole(String role, long userid) {
        return getUserRoleList(userid).contains(role);
    }

    public boolean hasRole(String role) {
        return getUserRoleList().contains(role);
    }

    public boolean hasAllPermission(List<String> permissionList, long userid) {
        return new HashSet<>(getUserPermissionList(userid)).containsAll(permissionList);
    }

    public boolean hasAllPermission(List<String> permissionList) {
        return new HashSet<>(getUserPermissionList()).containsAll(permissionList);
    }

    public boolean hasAllRole(List<String> roleList, long userid) {
        return new HashSet<>(getUserRoleList(userid)).containsAll(roleList);
    }

    public boolean hasAllRole(List<String> roleList) {
        return new HashSet<>(getUserRoleList()).containsAll(roleList);
    }

    public boolean hasAnyPermission(List<String> permissionList, long userid) {
        return new HashSet<>(getUserPermissionList(userid)).stream().anyMatch(permissionList::contains);
    }

    public boolean hasAnyPermission(List<String> permissionList) {
        return new HashSet<>(getUserPermissionList()).stream().anyMatch(permissionList::contains);
    }

    public boolean hasAnyRole(List<String> roleList, long userid) {
        return new HashSet<>(getUserRoleList(userid)).stream().anyMatch(roleList::contains);
    }

    public boolean hasAnyRole(List<String> roleList) {
        return new HashSet<>(getUserRoleList()).stream().anyMatch(roleList::contains);
    }

    public boolean hasAllPermission(String... permissionList) {
        return hasAllPermission(Arrays.asList(permissionList));
    }

    public boolean hasAllRole(String... roleList) {
        return hasAllRole(Arrays.asList(roleList));
    }

    public boolean hasAnyPermission(String... permissionList) {
        return hasAnyPermission(Arrays.asList(permissionList));
    }

    public boolean hasAnyRole(String... roleList) {
        return hasAnyRole(Arrays.asList(roleList));
    }

    public boolean hasAllPermission(long userid, String... permissionList) {
        return hasAllPermission(Arrays.asList(permissionList), userid);
    }

    public boolean hasAllRole(long userid, String... roleList) {
        return hasAllRole(Arrays.asList(roleList), userid);
    }

    public boolean hasAnyPermission(long userid, String... permissionList) {
        return hasAnyPermission(Arrays.asList(permissionList), userid);
    }

    public boolean hasAnyRole(long userid, String... roleList) {
        return hasAnyRole(Arrays.asList(roleList), userid);
    }
}
