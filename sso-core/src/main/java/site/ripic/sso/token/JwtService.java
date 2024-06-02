package site.ripic.sso.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtService {

    private final Algorithm algorithm;

    public JwtService(String publicKey, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        ECPrivateKey privateKey1 = (ECPrivateKey) kf.generatePrivate(privateKeySpec);
        ECPublicKey publicKey1 = (ECPublicKey) kf.generatePublic(publicKeySpec);
        this.algorithm = Algorithm.ECDSA384(publicKey1, privateKey1);
    }

    public String createToken(Long userId, Long jwtId, long timeout) {
        // 设置过期时间
        Date date = new Date(System.currentTimeMillis() + timeout * 1000);
        Map<String, Object> header = new HashMap<>(2);
        header.put("alg", "ES384");
        return JWT.create()
                .withHeader(header)
                .withClaim("userId", userId)
                .withExpiresAt(date)
                .withJWTId(String.valueOf(jwtId))
                .sign(algorithm);
    }

    public Long getUserId(String token) {
        return JWT.decode(token).getClaim("userId").asLong();
    }

    public Long getJwtId(String token) {
        String string = JWT.decode(token).getClaim("jit").asString();
        return Long.parseLong(string);
    }

    public void verifyToken(String token) {
        JWT.require(algorithm).build().verify(token);
    }
}
