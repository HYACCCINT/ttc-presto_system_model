package backend.trip;

import java.io.Serializable;

/** A segment of continuous trips. */
public class TripSegment implements Serializable {

  /** The start of this segment. */
  private TripTransaction entrance;

  /** The end of this segment. */
  private TripTransaction exit;

  /**
   * Creates a new segment with an entry transaction
   *
   * @param entrance The entry transaction.
   */
  TripSegment(TripTransaction entrance) {
    this.entrance = entrance;
  }

  /**
   * Completes this backend.trip segment,
   *
   * @param exit The exit transaction.
   */
  void completeSegment(TripTransaction exit) {
    this.exit = exit;
  }

  /**
   * Returns true if this segment is completed.
   *
   * @return True if this segment has an exit.
   */
  boolean isComplete() {
    return exit != null;
  }

  /**
   * Returns the entry transaction of this segment.
   *
   * @return The entry transaction of this segment.
   */
  TripTransaction getEntrance() {
    return entrance;
  }

  /**
   * Returns the exit transaction of this segment.
   *
   * @return The exit transaction of this segment.
   */
  TripTransaction getExit() {
    return exit;
  }

  @Override
  public String toString() {
    return "Entrance: " + entrance + ", Exit: " + exit;
  }
}
