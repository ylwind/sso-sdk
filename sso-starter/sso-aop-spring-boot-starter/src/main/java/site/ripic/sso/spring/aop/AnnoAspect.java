package site.ripic.sso.spring.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import site.ripic.sso.annotation.SsoIgnore;
import site.ripic.sso.strategy.SsoStrategy;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(0)
public class AnnoAspect {

    public static final String POINTCUT_SIGN =
            "@within(site.ripic.sso.annotation.CheckLogin) || @annotation(site.ripic.sso.annotation.CheckPermission) ||"
                    + "@annotation(site.ripic.sso.annotation.CheckRole) || @annotation(site.ripic.sso.annotation.SsoIgnore)";


    /**
     * 声明AOP签名
     */
    @Pointcut(POINTCUT_SIGN)
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (!SsoStrategy.instance.isAnnotationPresent.apply(method, SsoIgnore.class)) {
            SsoStrategy.instance.checkMethodAnnotation.accept(method);
        }
        return joinPoint.proceed();
    }

}
