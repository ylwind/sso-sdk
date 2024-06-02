package site.ripic.sso.listener;

import site.ripic.sso.enums.LoginFailReason;
import site.ripic.sso.model.LoginModel;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * sso组件全局监听中心
 */
public class SsoListenerCenter extends SsoListenerStorage {

    private SsoListenerCenter() {
    }

    /**
     * 执行登录操作监听器
     *
     * @param userid     用户id
     * @param loginModel 登录详细信息对象
     */
    public static void loginSuccess(long userid, LoginModel loginModel) {
        Map<String, SsoListener> ssoListenerList = getSsoListenerList();
        for (Map.Entry<String, SsoListener> entry : ssoListenerList.entrySet()) {
            // 获取线程池
            ThreadPoolExecutor threadPool = entry.getValue().getThreadPool();
            threadPool.execute(() -> entry.getValue().loginSuccess(userid, loginModel));
        }
    }

    public static void loginFail(long userid, LoginModel loginModel, LoginFailReason reason) {
        Map<String, SsoListener> ssoListenerList = getSsoListenerList();
        for (Map.Entry<String, SsoListener> entry : ssoListenerList.entrySet()) {
            // 获取线程池
            ThreadPoolExecutor threadPool = entry.getValue().getThreadPool();
            threadPool.execute(() -> entry.getValue().loginFail(userid, loginModel, reason));
        }
    }

}
