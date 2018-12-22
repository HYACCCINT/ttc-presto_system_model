package backend.travelcard;

/**
 * Thrown if a tapping operation is performed on a suspended card.
 */
public class CardSuspendedException extends Exception {

    /**
     * Creates a new CardSuspendedException.
     */
    public CardSuspendedException() {
    }

    /**
     *  Creates a new CardSuspendedException with a specified message
     * @param message The specified error message.
     */
    public CardSuspendedException(String message) {
        super(message);
    }
}
