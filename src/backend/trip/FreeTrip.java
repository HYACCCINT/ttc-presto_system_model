package backend.trip;

/** A trip that may happen on special occasions, where no fare is charged. */
public class FreeTrip extends Trip {

  /**
   * Creates a new FreeTrip with fare cap at $0.0
   *
   * @param transaction The first transaction of this trip.
   */
  public FreeTrip(TripTransaction transaction) {
    super(transaction, 0.0);
  }
}
