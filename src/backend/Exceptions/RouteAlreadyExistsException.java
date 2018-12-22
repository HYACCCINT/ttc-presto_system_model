package backend.Exceptions;

/** Exception thrown in the case of attempting to add repetitive routes */
public class RouteAlreadyExistsException extends Exception {

    /**
     * Initializes a new RouteAlreadyExistsException with a trace of message
     *
     * @param message defines exception
     */
    public RouteAlreadyExistsException(String message) {
        super(message);
    }
}
