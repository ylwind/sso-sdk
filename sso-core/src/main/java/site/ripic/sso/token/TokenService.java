package site.ripic.sso.token;

import site.ripic.sso.exception.InvalidTokenException;
import site.ripic.sso.exception.LoginTimeOutException;

/**
 * 生成token工具类，支持用户进行重写
 */
public interface TokenService {

    Long getUseridByToken(String token);

    Long getTokenIdByToken(String token);

    String createToken(long userid, Long loginId, long timeout);

    void verifyToken(String token) throws InvalidTokenException, LoginTimeOutException;
}
