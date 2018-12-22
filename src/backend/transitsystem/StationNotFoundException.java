package backend.transitsystem;

/** Thrown if a station does not exist in the system. */
public class StationNotFoundException extends Exception {

  StationNotFoundException() {}

  public StationNotFoundException(String message) {
    super(message);
  }
}
