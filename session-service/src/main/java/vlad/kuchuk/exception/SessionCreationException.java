package vlad.kuchuk.exception;

public class SessionCreationException extends RuntimeException {
    public SessionCreationException() {
    }

    public SessionCreationException(String message) {
        super(message);
    }
}
