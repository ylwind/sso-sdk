package site.ripic.sso.acl;

import site.ripic.sso.exception.BadConfigException;

import java.util.List;

public class DefaultAccessInterface implements AccessInterface {
    @Override
    public List<String> getUserPermissionList(Long userid) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public List<String> getUserRoleList(Long userid) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public boolean checkPermission(long userid, List<String> permissionList) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public boolean checkRole(long userid, List<String> roleList) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public boolean isUserLocked(Long userid) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public boolean isUserExpired(Long userid) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }

    @Override
    public boolean isUserDisabled(Long userid) {
        throw new BadConfigException("The AccessInterface is not configured.");
    }
}
