package backend.transitsystem.fares;

import java.io.Serializable;

/** An abstract calculator that calculates trip fares. */
public abstract class AbstractFareCalculator implements Serializable {

  /**
   * Returns the fare to pay when entering a transit system.
   *
   * @return The fare to pay.
   */
  public abstract double getEntryFare();

  /**
   * Returns the fare to pay when exiting a transit system, depending on distance travelled.
   *
   * @param distance The distance travelled in the backend.trip.
   * @returnThe fare to pay.
   */
  public abstract double getExitFare(int distance);
}
