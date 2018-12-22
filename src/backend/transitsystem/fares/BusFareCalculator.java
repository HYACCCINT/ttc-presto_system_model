package backend.transitsystem.fares;

/** A fare calculator for Bus. */
public class BusFareCalculator extends AbstractFareCalculator {

  /**
   * Returns the entrance fee for bus (hard set to 2 dollars)
   *
   * @return the set entrance fee
   */
  @Override
  public double getEntryFare() {
    return 2.0;
  }

  /**
   * Returns the exit fee for bus (hard set to zero dollars)
   *
   * @param distance The distance travelled in the backend.trip.
   * @return the set exit fee
   */
  @Override
  public double getExitFare(int distance) {
    return 0;
  }
}
