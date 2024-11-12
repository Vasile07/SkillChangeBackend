package skillchange.be.errors;

public class JWTValidationException extends RuntimeException {
    public JWTValidationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
