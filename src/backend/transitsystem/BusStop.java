package backend.transitsystem;

import backend.transitsystem.fares.BusFareCalculator;

/** A bus stop. */
public class BusStop extends AbstractStation {

  /**
   * Creates a new BusStop at specified location name.
   *
   * @param name Specified location name.
   */
  public BusStop(String name) {
    super(name, new BusFareCalculator());
  }

  @Override
  public String getType() {
    return "BUS";
  }

  /**
   * Returns true if the other station is also a BusStop at this location.
   *
   * @param other The other station to be compared.
   * @return True if both stations is the same.
   */
  public boolean equals(BusStop other) {
    return getName().equals(other.getName());
  }

  @Override
  public boolean isSameVehicle(AbstractStation otherStation) {
    return otherStation instanceof BusStop;
  }

  @Override
  public String toString() {
    return getName() + " Bus Stop";
  }
}
