package theater;

/**
 * This class is for a custom exception for unknown for play type handling.
 */
public class UnknownPlayTypeException extends RuntimeException {
    public UnknownPlayTypeException(String message) {
        super(message);
    }
}
