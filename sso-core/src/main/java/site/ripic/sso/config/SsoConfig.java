package site.ripic.sso.config;

/**
 * sso全局配置类
 */
public class SsoConfig {

    private String publicKey;

    private String privateKey;

    private Long defaultTimeOut = 3600L;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Long getDefaultTimeOut() {
        return defaultTimeOut;
    }

    public void setDefaultTimeOut(Long defaultTimeOut) {
        this.defaultTimeOut = defaultTimeOut;
    }
}
