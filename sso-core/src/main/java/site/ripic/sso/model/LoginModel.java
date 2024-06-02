package site.ripic.sso.model;

import java.util.Map;

public class LoginModel {

    public Long userid;

    /**
     * 此次登录的客户端设备类型
     */
    public String device;

    /**
     * 登录时传入的用户名
     */
    public String loginAccount;

    /**
     * 此次登录用户的ip地址
     */
    public String ip;

    /**
     * 此次登录用户的登录方式
     */
    public String method;

    /**
     * 此次登录 token 有效期
     */
    public Long timeout = 3600L;

    public Long tokenId;
    /**
     * 扩展信息
     */
    public Map<String, Object> extraData;

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
