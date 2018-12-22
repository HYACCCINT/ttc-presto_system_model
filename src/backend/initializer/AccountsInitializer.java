//package backend.initializer;

//<<<<<<< HEAD
////package backend.initializer;
////
////import backend.accounts.AccountsManager;
////import backend.travelcard.TravelCard;
////
////import java.io.File;
////import java.io.FileInputStream;
////import java.io.IOException;
////import java.io.ObjectInputStream;
////
/////** Loads a saved AccountManager from a configuration file. */
////public class AccountsInitializer {
////
////  /**
////   * Reads from the configuration file and returns the saved AccountManager, if it exists.
////   *
////   * @param accountsFilePath The path to the configuration file.
////   * @return The saved AccountManager, or a new AccountManager if no configuration file exists.
////   */
////  private AccountsManager initializeAccounts(String accountsFilePath) {
////    File accountsFile = new File(accountsFilePath);
////    if (!accountsFile.exists()) return new AccountsManager();
////    try {
////      FileInputStream fileInputStream = new FileInputStream(accountsFile);
////      ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
////      AccountsManager savedAccounts = (AccountsManager) inputStream.readObject();
////      savedAccounts.initialize();
////      return savedAccounts;
////
////    } catch (IOException | ClassNotFoundException e) {
////      e.printStackTrace();
////    }
////    return new AccountsManager();
////  }
////
////  /**
////   * If a file with the name cardsFilePath exists, calls the setNumCards method in TravelCards with
////   * input from file
////   *
////   * @param cardsFilePath path of input file
////   */
////  private void initializeNumCards(String cardsFilePath) {
////    File cardsFile = new File(cardsFilePath);
////    if (!cardsFile.exists()) {
////      return;
////    }
////    try {
////      FileInputStream fileInputStream = new FileInputStream(cardsFilePath);
////      ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
////      TravelCard.setNumCards((Integer) inputStream.readObject());
////    } catch (IOException | ClassNotFoundException e) {
////      e.printStackTrace();
////    }
////  }
////
////  /**
////   * Initializes the AccountManager and numCards when the system starts.
////   *
////   * @param accountsFilePath File path to AccountManager configuration file.
////   * @param cardsFilePath File path to numCards configuration file.
////   * @return
////   */
////  public AccountsManager initialize(String accountsFilePath, String cardsFilePath) {
////    initializeNumCards(cardsFilePath);
////    return initializeAccounts(accountsFilePath);
////  }
////}
//=======
package backend.initializer;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.serializer.AccountSerializer;
import backend.travelcard.TravelCard;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/** Loads a saved AccountManager from a configuration file. */
public class AccountsInitializer {

  /**
   * Reads from the configuration file and returns the saved AccountManager, if it exists.
   *
   * @param accountsFilePath The path to the configuration file.
   * @return The saved AccountManager, or a new AccountManager if no configuration file exists.
   */
  private AccountsManager initializeAccounts(String accountsFilePath) {
    File accountsFile = new File(accountsFilePath);
    if (!accountsFile.exists()) return new AccountsManager();
    try {
      FileInputStream fileInputStream = new FileInputStream(accountsFile);
      ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
      AccountsManager savedAccounts = (AccountsManager) inputStream.readObject();
      savedAccounts.reInitialize();
      return savedAccounts;

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return new AccountsManager();
  }

  /**
   * If a file with the name cardsFilePath exists, calls the setNumCards method in TravelCards with
   * input from file
   *
   * @param cardsFilePath path of input file
   */
  private void initializeNumCards(String cardsFilePath) {
    File cardsFile = new File(cardsFilePath);
    if (!cardsFile.exists() || TravelCard.getNumCards() > 0) {
      return;
    }
    try {
      FileInputStream fileInputStream = new FileInputStream(cardsFilePath);
      ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
      TravelCard.setNumCards((Integer) inputStream.readObject());
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the AccountManager and numCards when the system starts.
   *
   * @param accountsFilePath File path to AccountManager configuration file.
   * @param cardsFilePath File path to numCards configuration file.
   * @return
   */
  public AccountsManager initialize(String accountsFilePath, String cardsFilePath) {
    initializeNumCards(cardsFilePath);
    return initializeAccounts(accountsFilePath);
  }

  public static void main(String[] args) {
    AccountSerializer serializer = new AccountSerializer();
    AccountsManager savedAccountsManager = new AccountsInitializer().initialize(serializer.getFilePath(), serializer.getCardsFilePath());

  }
}

