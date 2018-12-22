package backend.travelcard;

/** Thrown if a TravelCard is not found in this system. */
public class TravelCardNotFoundException extends Exception {

  /**
   * Creates a new TravelCardNotFoundException
   */
  public TravelCardNotFoundException() {}

  /**
   * Creates a new TravelCardNotFoundException with a specified message.
   * @param message The specified error message.
   */
  public TravelCardNotFoundException(String message) {
    super(message);
  }
}
