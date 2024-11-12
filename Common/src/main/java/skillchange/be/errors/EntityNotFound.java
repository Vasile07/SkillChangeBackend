package skillchange.be.errors;

public class EntityNotFound extends RuntimeException {
    public EntityNotFound(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
