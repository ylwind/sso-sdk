package site.ripic.sso.listener;

import site.ripic.sso.enums.LoginFailReason;
import site.ripic.sso.model.LoginModel;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * sso监听实现类，这里使用了抽象类，使用需要重写方法
 */
public interface SsoListener {

    // 线程池，这里需要异步调用
    ThreadPoolExecutor getThreadPool();

    void loginSuccess(long loginUser, LoginModel loginModel);

    /**
     * 登录失败监听
     * 程序可以根据此监听器进行风控、记录等
     *
     * @param loginUser  登录的userid，可能为空
     * @param loginModel 登录信息
     * @param reason     失败原因，这里使用枚举，后续可以自由添加
     */
    void loginFail(Long loginUser, LoginModel loginModel, LoginFailReason reason);
}
