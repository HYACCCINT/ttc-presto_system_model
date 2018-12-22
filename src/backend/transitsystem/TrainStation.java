package backend.transitsystem;

import backend.transitsystem.fares.TrainFareCalculator;

/** A train station. */
public class TrainStation extends AbstractStation {

  /**
   * Creates a train station of given location name.
   *
   * @param name The specified location name.
   */
  public TrainStation(String name) {
    super(name, new TrainFareCalculator());
  }

  public String getType() {
    return "TRAIN";
  }

  /**
   * Returns true if the other station is also a TrainStation at the same location.
   *
   * @param other The other station to be compared.
   * @return True if both stations are the same.
   */
  public boolean equals(TrainStation other) {
    return getName().equals(other.getName());
  }

  @Override
  public boolean isSameVehicle(AbstractStation otherStation) {
    return otherStation instanceof TrainStation;
  }

  @Override
  public String toString() {
    return getName() + " Train Station";
  }
}
