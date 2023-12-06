package vlad.kuchuk.exception;

public class SessionManagerException extends RuntimeException {
    public SessionManagerException(String message) {
        super(message);
    }

    public SessionManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
