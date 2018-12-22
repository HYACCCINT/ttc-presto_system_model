package backend.serializer;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.travelcard.TravelCard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Serializer to preserve data in current Transit System
 */
public class AccountSerializer extends AbstractSerializer {


    /**
     * External file path where card information is outputed to
     */
  private final String cardsFilePath =
      "data" + File.separator + "cards" + File.separator + "cards.dat";


  public AccountSerializer() {
    super("data" + File.separator + "accounts" + File.separator + "accounts.ser");
  }
    /**
     * Getter for cardsFilePath
     * @return this cardsFilePath
     */
  public String getCardsFilePath() {
    return cardsFilePath;
  }

    /**
     * Calls writeAccounts and writeNumCards to preserve data in current system and writes them to output files
     * @param accountsManager the accountManager whose information to be preserved
     */
  public void saveState(AccountsManager accountsManager) {
    writeAccounts(accountsManager);
    writeNumCards();
  }

    /**
     * Saves the accountManager in a output file of accountsFilePath
     */
  private void writeAccounts(AccountsManager accountsManager) {
    writeObject(accountsManager);
  }

    /**
     * Saves the num card of TravelCards in a output file of cardsFilePath
     */
  private void writeNumCards() {
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(cardsFilePath);
      ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
      outputStream.writeObject(TravelCard.getNumCards());
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    AccountsManager accountsManager = new AccountsManager();
    AccountSerializer serializer = new AccountSerializer();

    serializer.saveState(accountsManager);
  }
}
