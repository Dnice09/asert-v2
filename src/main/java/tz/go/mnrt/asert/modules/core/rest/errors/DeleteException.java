package tz.go.mnrt.asert.modules.core.rest.errors;

public class DeleteException extends RuntimeException {
    public DeleteException(String entity) {
        super(String.format("You cannot delete this {%s} ", entity));
    }
}
