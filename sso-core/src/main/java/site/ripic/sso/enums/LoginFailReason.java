package site.ripic.sso.enums;

public enum LoginFailReason {

    USER_NOT_EXIST(1, "用户不存在", false, "用户不存在"),
    USER_LOCKED(2, "用户被锁定", true, "用户被锁定"),
    USER_EXPIRED(3, "用户已过期", true, "用户已过期"),
    USER_DISABLED(4, "用户被禁用", true, "用户被禁用"),
    USER_PASSWORD_ERROR(5, "密码错误", true, "密码错误"),
    ;

    private int code;
    private String desc;
    private boolean showForUser;
    private String userTip;

    LoginFailReason(int code, String desc, boolean showForUser, String userTip) {
        this.code = code;
        this.desc = desc;
        this.showForUser = showForUser;
        this.userTip = userTip;
    }

    public static LoginFailReason getByCode(int code) {
        for (LoginFailReason loginFailReason : LoginFailReason.values()) {
            if (loginFailReason.getCode() == code) {
                return loginFailReason;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isShowForUser() {
        return showForUser;
    }

    public String getUserTip() {
        return userTip;
    }
}
