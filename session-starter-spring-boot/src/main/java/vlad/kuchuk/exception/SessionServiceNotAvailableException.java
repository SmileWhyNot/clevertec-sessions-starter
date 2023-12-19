package vlad.kuchuk.exception;

public class SessionServiceNotAvailableException extends RuntimeException {
    public SessionServiceNotAvailableException(String message) {
        super(message);
    }
}
