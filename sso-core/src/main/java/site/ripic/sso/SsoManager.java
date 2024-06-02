package site.ripic.sso;

import site.ripic.sso.acl.AccessInterface;
import site.ripic.sso.acl.DefaultAccessInterface;
import site.ripic.sso.config.SsoConfig;
import site.ripic.sso.config.SsoConfigFactory;
import site.ripic.sso.storage.DefaultTemporaryStorage;
import site.ripic.sso.storage.DefaultTokenStorage;
import site.ripic.sso.storage.TemporaryStorage;
import site.ripic.sso.storage.TokenStorage;
import site.ripic.sso.strategy.LogicStrategy;
import site.ripic.sso.strategy.SsoStrategy;
import site.ripic.sso.token.DefaultTokenService;
import site.ripic.sso.token.TokenService;

/**
 * sso全局配置管理类
 */
public class SsoManager {

    /**
     * sso基本信息配置类
     */
    private volatile static SsoConfig config;

    /**
     * 权限控制配置类
     */
    private volatile static AccessInterface accessInterface;

    private volatile static SsoStrategy ssoStrategy;

    /**
     * 临时存储配置类
     */
    private volatile static TemporaryStorage temporaryStorage;

    private volatile static TokenStorage tokenStorage;

    private volatile static TokenService tokenService;

    private volatile static LogicStrategy logicStrategy;

    private SsoManager() {
    }

    public static void setSsoConfig(SsoConfig ssoConfig) {
        SsoManager.config = ssoConfig;
    }

    public static SsoConfig getConfig() {
        if (config == null) {
            synchronized (SsoManager.class) {
                if (config == null) {
                    setSsoConfig(SsoConfigFactory.createSsoConfig());
                }
            }
        }
        return config;
    }

    public static AccessInterface getAccessInterface() {
        if (accessInterface == null) {
            synchronized (SsoManager.class) {
                if (accessInterface == null) {
                    SsoManager.accessInterface = new DefaultAccessInterface();
                }
            }
        }
        return accessInterface;
    }

    public static void setAccessInterface(AccessInterface accessInterface) {
        SsoManager.accessInterface = accessInterface;
    }

    public static SsoStrategy getSsoStrategy() {
        if (ssoStrategy == null) {
            synchronized (SsoManager.class) {
                if (ssoStrategy == null) {
                    SsoManager.ssoStrategy = SsoStrategy.instance;
                }
            }
        }
        return ssoStrategy;
    }

    public static void setSsoStrategy(SsoStrategy ssoStrategy) {
        SsoManager.ssoStrategy = ssoStrategy;
    }

    public static TemporaryStorage getTemporaryStorage() {
        if (temporaryStorage == null) {
            synchronized (SsoManager.class) {
                if (temporaryStorage == null) {
                    temporaryStorage = new DefaultTemporaryStorage();
                }
            }
        }
        return temporaryStorage;
    }

    public static void setTemporaryStorage(TemporaryStorage temporaryStorage) {
        SsoManager.temporaryStorage = temporaryStorage;
    }

    public static TokenStorage getTokenStorage() {
        if (tokenStorage == null) {
            synchronized (SsoManager.class) {
                if (tokenStorage == null) {
                    tokenStorage = new DefaultTokenStorage();
                }
            }
        }
        return tokenStorage;
    }

    public static TokenService getTokenService() {
        if (tokenService == null) {
            synchronized (SsoManager.class) {
                if (tokenService == null) {
                    tokenService = new DefaultTokenService();
                }
            }
        }
        return tokenService;
    }

    public static void setTokenService(TokenService tokenService) {
        SsoManager.tokenService = tokenService;
    }

    public static LogicStrategy getLogicStrategy() {
        if (logicStrategy == null) {
            synchronized (SsoManager.class) {
                if (logicStrategy == null) {
                    logicStrategy = new LogicStrategy();
                }
            }
        }
        return logicStrategy;
    }

    public static void setLogicStrategy(LogicStrategy logicStrategy) {
        SsoManager.logicStrategy = logicStrategy;
    }

}
