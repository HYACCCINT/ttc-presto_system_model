package backend.trip;

import backend.transitsystem.AbstractStation;
import backend.travelcard.FixedStack;
import backend.travelcard.InsufficientBalanceException;
import backend.travelcard.balances.Balance;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.LocalDateTime;

/** Handles complicated logic to register new trips/update current trips. */
public class TripRegistrar implements Serializable {

  /** The status of whether all trips are currently free. */
  private static boolean currentlyFree = false;

  /** The checker for abnormal behaviours in trips. */
  private transient TripValidator tripValidator;

  /** The support for notifying listeners of this registrar. */
  private PropertyChangeSupport trafficChangeSupport = new PropertyChangeSupport(this);

  /** Creates a new TripRegistrar and initializes a validator. */
  public TripRegistrar() {
    tripValidator = new TripValidator();
  }

  /**
   * Sets the status of free trips.
   *
   * @param bool True if all trips are free now.
   */
  public static void setCurrentlyFree(boolean bool) {
    currentlyFree = bool;
  }

  /**
   * Returns whether all trips are free now.
   *
   * @return True if all trips are free now.
   */
  public static boolean isCurrentlyFree() {
    return currentlyFree;
  }

  /**
   * Adds a listener that updates when trips is updated.
   *
   * @param listener The listener to be added.
   */
  public void addTrafficListener(PropertyChangeListener listener) {
    trafficChangeSupport.addPropertyChangeListener("travelled", listener);
  }

  /**
   * Adds a listener that updates when balance is deducted.
   *
   * @param listener The listener to be added.
   */
  public void addCostListener(PropertyChangeListener listener) {
    trafficChangeSupport.addPropertyChangeListener("monthlyCost", listener);
  }

  /**
   * Deducts balance based on the maximum fare cap of a given tip.
   *
   * @param trip The current backend.trip.
   * @param balance The balance to deduct.
   * @param fare The fare to calculate from.
   * @return The fare deducted.
   * @throws InsufficientBalanceException If there is not enough balance to deduct from.
   */
  private double chargeFare(Trip trip, Balance balance, double fare)
      throws InsufficientBalanceException {
    fare = trip.getMaxFare(fare);
    if (fare > 0) {
      balance.deduct(fare);
      trip.payFare(fare);
    }
    return fare;
  }

  /**
   * Creates a new backend.trip or continues a current backend.trip.
   *
   * @param station The entry point to this backend.trip.
   * @param time The timestamp of this backend.trip.
   * @param trips The list of recent trips.
   * @param balance The balance to deduct from.
   * @throws InsufficientBalanceException If there is not enough balance to deduct from.
   * @throws IllegalEntryException If there is abnormal behaviour detected.
   */
  public void registerTrip(
      AbstractStation station, LocalDateTime time, FixedStack<Trip> trips, Balance balance)
      throws InsufficientBalanceException, IllegalEntryException {
    Trip latest = trips.getLast();
    tripValidator.validateEntry(latest);
    TripTransaction transaction = new TripTransaction(station, time);
    if (latest != null
        && tripValidator.isContinuable(
            latest.getFirstTransaction(), latest.getLastTransaction(), transaction)) {
      continueTrip(latest, transaction, balance);
    } else {
      createTrip(trips, transaction, balance);
    }
  }

  /**
   * Continues the current backend.trip.
   *
   * @param trip The current backend.trip.
   * @param transaction The transaction to be stored.
   * @param balance The balance to deduct from.
   * @throws InsufficientBalanceException If there is not enough balance to deduct from.
   */
  private void continueTrip(Trip trip, TripTransaction transaction, Balance balance)
      throws InsufficientBalanceException {
    double fare = transaction.getStation().getFareCalculator().getEntryFare();
    double fareCharged = chargeFare(trip, balance, fare);
    trip.addSegment(transaction);
    trafficChangeSupport.firePropertyChange(
        "monthlyCost", transaction.getTimestamp().getMonth(), fareCharged);
    trafficChangeSupport.firePropertyChange("travelled", transaction.getStation(), 0);
  }

  /**
   * Creates a new backend.trip and pushes it to the list of recent trips.
   *
   * @param trips The list of recent trips.
   * @param transaction The transaction to be stored.
   * @param balance The balance to deduct from.
   * @throws InsufficientBalanceException If there is not enough balance to deduct from.
   */
  private void createTrip(FixedStack<Trip> trips, TripTransaction transaction, Balance balance)
      throws InsufficientBalanceException {
    double fare = transaction.getStation().getFareCalculator().getEntryFare();
    Trip newTrip;
    if (isCurrentlyFree()) {
      newTrip = new FreeTrip(transaction);
    } else {
      newTrip = new Trip(transaction);
    }
    double fareCharged = chargeFare(newTrip, balance, fare);
    trips.push(newTrip);
    trafficChangeSupport.firePropertyChange(
        "monthlyCost", transaction.getTimestamp().getMonth(), fareCharged);
    trafficChangeSupport.firePropertyChange("travelled", transaction.getStation(), 0);
  }

  /**
   * Updates and completes the current backend.trip
   *
   * @param exitStation The exit point of this backend.trip.
   * @param timestamp The timestamp of this exit.
   * @param currentTrip The current backend.trip.
   * @param balance The balance to deduct from.
   * @throws InsufficientBalanceException If there is not enough balance to deduct from.
   * @throws IllegalExitException If there is abnormal behaviour detected.
   */
  public void updateTrip(
      AbstractStation exitStation, LocalDateTime timestamp, Trip currentTrip, Balance balance)
      throws InsufficientBalanceException, IllegalExitException {
    TripTransaction newTransaction = new TripTransaction(exitStation, timestamp);
    tripValidator.validateExit(currentTrip, newTransaction);
    int distanceTravelled = currentTrip.getLastTransaction().getStation().getDistance(exitStation);
    double fare = newTransaction.getStation().getFareCalculator().getExitFare(distanceTravelled);
    double fareCharged = chargeFare(currentTrip, balance, fare);
    currentTrip.addSegment(newTransaction);
    trafficChangeSupport.firePropertyChange("travelled", exitStation, distanceTravelled);
    trafficChangeSupport.firePropertyChange("monthlyCost", timestamp.getMonth(), fareCharged);
  }

  /**
   * Remove the current backend.trip where abnormal behaviour is detected (ie. corrupted)
   *
   * @param trips The list of recent trips.
   */
  public void removeCorruptedTrip(FixedStack<Trip> trips) {
    trips.popLast();
  }
}
