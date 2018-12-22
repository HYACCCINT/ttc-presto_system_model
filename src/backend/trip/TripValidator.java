package backend.trip;

import backend.transitsystem.AbstractStation;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Helper class which validates the completion of a backend.trip, and notifies the user if they
 * there is an abnormal behavior when tapping in/out of a vehicle
 */
class TripValidator {

  /** Legal duration a cardholder can exit from the station he/she entered from. */
  private final Duration timeLimitSameStation = Duration.ofMinutes(30);

  /** Legal duration of a full trip. If this time is exceeded, a new trip is created. */
  private final Duration timeLimitPerTrip = Duration.ofHours(2);

  /**
   * Checks if the entry to a
   *
   * @param trip the last backend.trip that the user took
   * @throws IllegalEntryException a warning for abnormal backend.trip behavior
   */
  void validateEntry(Trip trip) throws IllegalEntryException {
    if (trip != null && !trip.isComplete()) {
      throw new IllegalEntryException();
    }
  }

  /**
   * Throws an IllegalEntryException when an user attempts to tap out of a station when 1. There is
   * no trip recorded 2. The user did not tap in 3. The user does not exit on the same day when
   * he/she entered.
   *
   * @param currentTrip the current backend.trip that the user is on
   * @param newTransaction the transaction for this backend.trip
   * @throws IllegalExitException a warning for abnormal behavior
   */
  void validateExit(Trip currentTrip, TripTransaction newTransaction) throws IllegalExitException {
    if (currentTrip == null
        || currentTrip.isComplete()
        || !isLegalExit(currentTrip.getLastTransaction(), newTransaction)
        || !isSameDay(
            currentTrip.getLastTransaction().getTimestamp(), newTransaction.getTimestamp())) {
      throw new IllegalExitException();
    }
  }

  /**
   * Checks if the exit of a trip is legal. An exit is legal if it is accessible from its entrance.
   * If it is the same as the entrance station, it should not exceed the legal time limit.
   *
   * @param lastEntry the backend.trip that the user is on
   * @param newTransaction the transaction the user made for this backend.trip
   * @return true iff the user traveled a non-negative distance, in the specified time limit
   */
  private boolean isLegalExit(TripTransaction lastEntry, TripTransaction newTransaction) {
    AbstractStation entryStation = lastEntry.getStation();
    AbstractStation exitStation = newTransaction.getStation();
    if (entryStation.getDistance(exitStation) == -1) {
      return false;
    } else if (entryStation.equals(exitStation)) {
      Duration timeElapsed =
          Duration.between(lastEntry.getTimestamp(), newTransaction.getTimestamp());
      return timeElapsed.compareTo(timeLimitSameStation) <= 0;
    }
    return true;
  }

  /**
   * Checks if the user begins and ends a trip on the same day
   *
   * @param entryTime date that the user begins the journey
   * @param exitTime date that the user ends the journey
   * @return true iff the entryTime date is the same as the exitTime date
   */
  private boolean isSameDay(LocalDateTime entryTime, LocalDateTime exitTime) {
    return entryTime.getDayOfYear() == exitTime.getDayOfYear();
  }

  /**
   * Checks if the user has passed the time limit for this backend.trip on this transaction
   *
   * @param lastExit the backend.trip this user is on
   * @param transaction the transaction that the user wishes to complete
   * @return true iff this transaction made by the user is within the time limit of this trip
   */
  boolean isContinuable(TripTransaction firstEntry, TripTransaction lastExit, TripTransaction transaction) {
    Duration timeElapsed = Duration.between(firstEntry.getTimestamp(), transaction.getTimestamp());
    return lastExit.getStation().equals(transaction.getStation())
        && timeElapsed.compareTo(timeLimitPerTrip) < 0;
  }
}
