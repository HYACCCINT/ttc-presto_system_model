package backend.Exceptions;

/** Exception thrown in the case of attempting to manipulate a non-existent CardHolder */
public class UserNotFoundException extends Exception {

  /**
   * Constructs a new exception with the specified message.
   *
   * @param message The specified message to be printed.
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
