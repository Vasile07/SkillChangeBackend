package skillchange.be;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import skillchange.be.errors.BadCredentialsException;
import skillchange.be.errors.EntityNotFound;
import skillchange.be.errors.JWTValidationException;

// https://en.wikipedia.org/wiki/List_of_HTTP_status_codes

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFound entityNotFound, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(entityNotFound.getMessage(), status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException badCredentialsException, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(badCredentialsException.getMessage(), status);
    }

    @ExceptionHandler(JWTValidationException.class)
    public ResponseEntity<String> handleJWTValidationException(JWTValidationException jwtValidationException, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(jwtValidationException.getMessage(), status);
    }
}
