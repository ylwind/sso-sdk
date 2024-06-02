package site.ripic.sso.storage;

/**
 * 临时存储接口
 * 这里存储临时变量，比如登录的code信息，有过期时间的token信息
 * 建议存储在redis中，避免多机部署造成数据不一致的情况
 */
public interface TemporaryStorage {

    void set(String key, String value, long timeout);

    String get(String key);

    void remove(String key);

    long getTtl(String key);


    void updateTimeout(String key, long timeout);

    void updateValue(String key, String value, long timeout);
}
