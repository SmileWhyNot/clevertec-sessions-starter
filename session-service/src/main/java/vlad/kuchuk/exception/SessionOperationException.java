package vlad.kuchuk.exception;

public class SessionOperationException extends RuntimeException {
    public SessionOperationException(String message) {
        super(message);
    }
}