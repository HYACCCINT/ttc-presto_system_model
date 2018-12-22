package backend.trip;

/** Exception thrown in the case of attempting to perform an illegal exit */
public class IllegalExitException extends Exception {
  public IllegalExitException() {}

  /**
   * Initializes a new IllegalExitException with a trace of message
   *
   * @param message defines exception
   */
  public IllegalExitException(String message) {
    super(message);
  }
}
