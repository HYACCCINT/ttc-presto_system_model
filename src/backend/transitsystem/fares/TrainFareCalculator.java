package backend.transitsystem.fares;

/** A fare calculator for Trains. */
public class TrainFareCalculator extends AbstractFareCalculator {
  /**
   * Returns the entrance fee for train (hard set to 0 dollars)
   *
   * @return the set entrance fee (free)
   */
  @Override
  public double getEntryFare() {
    return 0;
  }

  /**
   * Returns the exit fee for train (dependant on distance travelled)
   *
   * @param distance The distance travelled in the backend.trip.
   * @return the fee for the backend.trip (0.5 dollars for each station passed)
   */
  @Override
  public double getExitFare(int distance) {
    return 0.5 * distance;
  }
}
