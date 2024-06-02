package site.ripic.sso.strategy;

import site.ripic.sso.SsoManager;
import site.ripic.sso.annotation.CheckLogin;
import site.ripic.sso.annotation.CheckPermission;
import site.ripic.sso.annotation.CheckRole;

/**
 * 注解鉴权使用
 */
public class AnnoStrategy {

    public boolean checkLogin(CheckLogin cl) {
        return SsoManager.getLogicStrategy().isLogin();
    }

    public boolean checkRole(CheckRole cr) {
        if (cr.role() == null || cr.role().length == 0) {
            return true;
        }
        switch (cr.type()) {
            case OR:
                return SsoManager.getLogicStrategy().hasAnyRole(cr.role());
            case AND:
                return SsoManager.getLogicStrategy().hasAllRole(cr.role());
            case NOT:
                return !SsoManager.getLogicStrategy().hasAnyRole(cr.role());
            default:
                return false;
        }
    }

    public boolean checkPermission(CheckPermission cp) {
        if (cp.permissions() == null || cp.permissions().length == 0) {
            return true;
        }
        switch (cp.type()) {
            case OR:
                return SsoManager.getLogicStrategy().hasAnyPermission(cp.permissions());
            case AND:
                return SsoManager.getLogicStrategy().hasAllPermission(cp.permissions());
            case NOT:
                return !SsoManager.getLogicStrategy().hasAnyPermission(cp.permissions());
            default:
                return false;
        }
    }
}
