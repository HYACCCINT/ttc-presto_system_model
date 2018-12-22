package backend.travelcard;

import backend.travelcard.balances.StudentBalance;

import java.io.IOException;

/**
 * A student TravelCard that always have 10% off the fare.
 */
public class StudentTravelCard extends TravelCard {

  /**
   * Creates a new StudentTravelCard with a specified email address.
   * @param email The specified email address.
   * @throws IOException Thrown if the logger cannot be initialized.
   */
  public StudentTravelCard(String email) throws IOException {
    super(email, new StudentBalance(19));
  }

  @Override
  public String toString() {
    String ret = "Student Card #" + getId();
    if (isSuspended()) ret += " [Suspended]";
    return ret;
  }
}
