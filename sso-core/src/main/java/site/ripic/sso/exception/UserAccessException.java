package site.ripic.sso.exception;

public class UserAccessException extends RuntimeException {
    public UserAccessException() {
        super();
    }

    public UserAccessException(String message) {
        super(message);
    }

    public UserAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAccessException(Throwable cause) {
        super(cause);
    }

    protected UserAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
