package site.ripic.sso.token;

import com.auth0.jwt.exceptions.*;
import org.apache.commons.lang3.StringUtils;
import site.ripic.sso.SsoManager;
import site.ripic.sso.exception.InvalidTokenException;
import site.ripic.sso.exception.LoginTimeOutException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DefaultTokenService implements TokenService {

    private static JwtService jwtService;

    public DefaultTokenService() {
        String privateKey = SsoManager.getConfig().getPrivateKey();
        String publicKey = SsoManager.getConfig().getPublicKey();
        try {
            DefaultTokenService.jwtService = new JwtService(publicKey, privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getUseridByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("token is empty");
        }
        return jwtService.getUserId(token);
    }

    @Override
    public Long getTokenIdByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("token is empty");
        }
        return jwtService.getJwtId(token);
    }

    @Override
    public String createToken(long userid, Long loginId, long timeout) {
        return jwtService.createToken(userid, loginId, timeout);
    }

    @Override
    public void verifyToken(String token) throws InvalidTokenException, LoginTimeOutException {
        if (StringUtils.isEmpty(token)) {
            throw new InvalidTokenException("token is empty");
        }
        try {
            jwtService.verifyToken(token);
        } catch (TokenExpiredException e1) {
            throw new LoginTimeOutException(e1);
        } catch (JWTDecodeException | SignatureVerificationException | AlgorithmMismatchException |
                 InvalidClaimException e2) {
            throw new InvalidTokenException(e2);
        }

    }
}
