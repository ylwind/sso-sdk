package site.ripic.sso.strategy;

import site.ripic.sso.SsoManager;
import site.ripic.sso.annotation.CheckLogin;
import site.ripic.sso.annotation.CheckPermission;
import site.ripic.sso.annotation.CheckRole;
import site.ripic.sso.exception.InvalidTokenException;
import site.ripic.sso.exception.UserAccessException;
import site.ripic.sso.model.LoginModel;
import site.ripic.sso.token.TokenService;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Consumer;

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

    // 默认使用jdk的注解处理器
    public BiFunction<AnnotatedElement, Class<? extends Annotation>, Annotation> getAnnotation = AnnotatedElement::getAnnotation;


    public BiFunction<Method, Class<? extends Annotation>, Boolean> isAnnotationPresent = (method, annotationClass) -> {
        return instance.getAnnotation.apply(method, annotationClass) != null ||
                instance.getAnnotation.apply(method.getDeclaringClass(), annotationClass) != null;
    };
    public Consumer<AnnotatedElement> checkElementAnnotation = (element) -> {

        CheckLogin checkLogin = (CheckLogin) SsoStrategy.instance.getAnnotation.apply(element, CheckLogin.class);
        if (checkLogin != null && !SsoManager.getAnnoStrategy().check(checkLogin)) {
            throw new InvalidTokenException("user is not login");
        }

        CheckRole checkRole = (CheckRole) SsoStrategy.instance.getAnnotation.apply(element, CheckRole.class);
        if (checkRole != null && !SsoManager.getAnnoStrategy().check(checkRole)) {
            throw new UserAccessException("用户不具有用户组权限");
        }
        CheckPermission checkPermission = (CheckPermission) SsoStrategy.instance.getAnnotation.apply(element, CheckPermission.class);
        if (checkPermission != null && !SsoManager.getAnnoStrategy().check(checkPermission)) {
            throw new UserAccessException("用户不具有权限");
        }
    };
    public Consumer<Method> checkMethodAnnotation = (method) -> {

        // 先校验 Method 所属 Class 上的注解
        instance.checkElementAnnotation.accept(method.getDeclaringClass());

        // 再校验 Method 上的注解
        instance.checkElementAnnotation.accept(method);
    };


}
