package backend.trip;

import backend.transitsystem.AbstractStation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/** Wrapper class of crucial information of a backend.trip. */
public class TripTransaction implements Serializable {

  /** The station of a backend.trip. */
  private AbstractStation station;

  /** The time of a backend.trip. */
  private LocalDateTime timestamp;

  /**
   * Creates a new transaction that stores the station and timestamp of a backend.trip.
   *
   * @param station The specified station
   * @param timestamp The specified timestamp
   */
  TripTransaction(AbstractStation station, LocalDateTime timestamp) {
    this.station = station;
    this.timestamp = timestamp;
  }

  /**
   * Returns the station of this transaction.
   *
   * @return The station of this transaction.
   */
  public AbstractStation getStation() {
    return station;
  }

  /**
   * Returns the timestamp of this transaction.
   *
   * @return The timestamp of this transaction.
   */
  LocalDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "(" + station + ", " + timestamp + ")";
  }
}
