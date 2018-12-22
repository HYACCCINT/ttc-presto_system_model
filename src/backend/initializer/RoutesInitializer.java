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

import backend.serializer.RouteSerializer;
import backend.serializer.StationSerializer;
import backend.transitsystem.BusStop;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import backend.transitsystem.TrainStation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Loads a saved AccountManager from a configuration file.
 */
public class RoutesInitializer extends AbstractInitializer{

    /**
     * Initializes the AccountManager and numCards when the system starts.
     *
     * @param routesFilePath File path to AccountManager configuration file.
     * @return
     */
    public RoutesManager initialize(String routesFilePath) {
        RoutesManager routesManager = (RoutesManager)  deserialize(routesFilePath);
        if (routesManager == null) {
            routesManager = new RoutesManager();
        }
        return routesManager;
    }

    public static void main(String[] args) {
        RoutesManager routesManager = new RoutesInitializer().initialize(new RouteSerializer().getFilePath());
        StationManager stationManager = new StationsInitializer().initialize(new StationSerializer().getFilePath());

    }
}

