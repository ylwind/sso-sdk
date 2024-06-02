package site.ripic.sso.storage;

import site.ripic.sso.exception.BadConfigException;
import site.ripic.sso.model.LoginModel;

import java.util.List;

public class DefaultTokenStorage implements TokenStorage {

    @Override
    public List<Long> getAllValidTokenIdByUserid(long userid) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public Long getUseridByTokenId(Long tokenId) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public LoginModel getLoginModelByTokenId(Long tokenId) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public Long getNewTokenId() {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public void saveToken(long userid, LoginModel model, long timeout) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public void updateTokenValidTime(Long tokenId, long timeout) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public void removeToken(List<Long> tokenIdList) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public void removeAllTokenByUserid(long userid) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public String getToken() {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public boolean isTokenValid(Long tokenId) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }

    @Override
    public long getTtl(Long tokenId) {
        throw new BadConfigException("The tokenStorage is not configured.");
    }
}
