package site.ripic.sso.listener;


import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SsoListenerStorage {

    private static Map<String, SsoListener> ssoListenerList = new ConcurrentHashMap<>();

    /**
     * 添加监听
     *
     * @param listenerName listener名称
     * @param ssoListener  listener实体
     */
    public static void addListener(String listenerName, SsoListener ssoListener) {
        if (ssoListener == null || StringUtils.isEmpty(listenerName)) {
            throw new IllegalArgumentException("listener为空，请检查");
        }
        if (ssoListenerList.containsKey(listenerName)) {
            throw new IllegalStateException("存在同名listener，请检查");
        }
        ssoListenerList.put(listenerName, ssoListener);
    }

    /**
     * 根据名字获取监听器
     *
     * @param listenerName 监听器名称
     * @return 监听器
     */
    public static SsoListener getListener(String listenerName) {
        if (StringUtils.isEmpty(listenerName)) {
            throw new IllegalArgumentException("listener为空，请检查");
        }
        return ssoListenerList.get(listenerName);
    }

    public static void removeListener(String listenerName) {
        if (StringUtils.isEmpty(listenerName)) {
            throw new IllegalArgumentException("listener为空，请检查");
        }
        ssoListenerList.remove(listenerName);
    }

    public static void clear() {
        ssoListenerList.clear();
    }

    public static Map<String, SsoListener> getSsoListenerList() {
        return ssoListenerList;
    }
}
