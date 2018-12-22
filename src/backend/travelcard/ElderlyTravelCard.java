package backend.travelcard;

import backend.travelcard.balances.ElderlyBalance;

import java.io.IOException;

/**
 * An Elderly Travel Card that pays half the fare.
 */
public class ElderlyTravelCard extends TravelCard {

  /**
   * Creates a new ElderlyTravelCard with a specified email.
   * @param email The specified email.
   * @throws IOException Thrown if logger cannot be initialized.
   */
  public ElderlyTravelCard(String email) throws IOException {
    super(email, new ElderlyBalance(19));
  }

  @Override
  public String toString() {
    String ret = "Elderly Card #" + getId();
    if (isSuspended()) ret += " [Suspended]";
    return ret;
  }
}
