package site.ripic.sso.exception;

public class BadConfigException extends RuntimeException{

    public BadConfigException() {
        super();
    }

    public BadConfigException(String message) {
        super(message);
    }

    public BadConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConfigException(Throwable cause) {
        super(cause);
    }

    protected BadConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
