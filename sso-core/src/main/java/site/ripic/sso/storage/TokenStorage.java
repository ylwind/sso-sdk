package site.ripic.sso.storage;

import site.ripic.sso.model.LoginModel;

import java.util.List;

/**
 * token存储器
 */
public interface TokenStorage {

    /**
     * 按照登录的先后顺序，返回当前user下的所有有效token
     *
     * @param userid userid
     * @return 根据时间先后，返回Token列表，如果Token不存在返回空列表
     */
    List<Long> getAllValidTokenIdByUserid(long userid);

    /**
     * 根据tokenId获取用户id
     *
     * @param tokenId tokenId
     * @return 用户信息，如果用户不存在返回null
     */
    Long getUseridByTokenId(Long tokenId);

    LoginModel getLoginModelByTokenId(Long tokenId);

    /**
     * 获取新的tokenId
     *
     * @return tokenId，保证在整个集群内是唯一的
     */
    Long getNewTokenId();

    /**
     * 设置用户tokenId
     *
     * @param userid  用户的userid
     * @param timeout token过期时间
     */
    void saveToken( long userid, LoginModel model, long timeout);


    /**
     * 更新tokenid的过期时间
     *
     * @param tokenId tokenid
     * @param timeout 新的过期时间，该时间为有效时间-当前时间
     */
    void updateTokenValidTime(Long tokenId, long timeout);

    /**
     * 移除tokenid
     *
     * @param tokenIdList tokenId
     */
    void removeToken(List<Long> tokenIdList);

    /**
     * 根据用户id删除所有token信息
     *
     * @param userid userid
     */
    void removeAllTokenByUserid(long userid);

    /**
     * 获取当前请求的token信息
     *
     * @return token
     */
    String getToken();

    boolean isTokenValid(Long tokenId);

    long getTtl(Long tokenId);
}
