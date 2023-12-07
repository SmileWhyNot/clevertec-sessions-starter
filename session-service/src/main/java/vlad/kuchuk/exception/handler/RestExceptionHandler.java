package vlad.kuchuk.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import vlad.kuchuk.exception.SessionCreationException;
import vlad.kuchuk.exception.SessionOperationException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BAD_REQUEST = "BAD_REQUEST";

    @ExceptionHandler(SessionCreationException.class)
    public ResponseEntity<ApiError> handleInvalidLangException(SessionCreationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(SessionOperationException.class)
    public ResponseEntity<ApiError> handleInvalidLangException(SessionOperationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @Nullable
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST, "NOT_PASSED_VALIDATION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST, "NOT_PASSED_VALIDATION");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}