package backend.travelcard;

/** Thrown when credit is deducted from a travel card with negative balance. */
public class InsufficientBalanceException extends Exception {

  /**
   * Creates a new InsufficientBalanceException
   */
  public InsufficientBalanceException() {}

  /**
   * Creates a new InsufficientBalanceException with a specified error message.
   * @param message The specified error message.
   */
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
