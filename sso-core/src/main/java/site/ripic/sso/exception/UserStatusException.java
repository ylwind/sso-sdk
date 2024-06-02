package site.ripic.sso.exception;

public class UserStatusException extends RuntimeException{

    public UserStatusException() {
        super();
    }

    public UserStatusException(String message) {
        super(message);
    }

    public UserStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserStatusException(Throwable cause) {
        super(cause);
    }

    protected UserStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
