package backend.Exceptions;

/** Exception thrown in the case of attempting to create a repetitive CardHolder */
public class EmailAlreadyExistsException extends Exception {

  /**
   * Initializes a new EmailAlreadyExistsException with a trace of message
   *
   * @param message defines exception
   */
  public EmailAlreadyExistsException(String message) {
    super(message);
  }
}
