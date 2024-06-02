package site.ripic.sso.strategy;

import site.ripic.sso.SsoManager;
import site.ripic.sso.model.LoginModel;
import site.ripic.sso.token.TokenService;

import java.util.function.BiFunction;

/**
 * 核心策略接口
 * 算法类相关的策略接口
 */
public class SsoStrategy {

    // 单例
    public static final SsoStrategy instance = new SsoStrategy();

    /**
     * 创建登录token
     */
    public BiFunction<Long, LoginModel, String> createLoginToken = (userid, loginModel) -> {
        if (userid == 0L || loginModel == null || loginModel.getTokenId() == null) {
            throw new IllegalArgumentException("userid or loginModel is null");
        }
        return distributeLoginToken(userid, loginModel);
    };

    private SsoStrategy() {
    }

    private String distributeLoginToken(long userid, LoginModel loginModel) {
        // 通过jwt生成token
        TokenService tokenService = SsoManager.getTokenService();
        return tokenService.createToken(userid, loginModel.getTokenId(), loginModel.getTimeout());
    }


}
