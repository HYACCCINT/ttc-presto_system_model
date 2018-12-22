package backend.trip;

/** Exception thrown in the case of attempting to perform an illegal entrance */
public class IllegalEntryException extends Exception {
  public IllegalEntryException() {}

  /**
   * Initializes a new IllegalEntranceException with a trace of message
   *
   * @param message defines exception
   */
  public IllegalEntryException(String message) {
    super(message);
  }
}
