package frontend.cardtap;

import backend.accounts.AccountsManager;
import backend.Exceptions.UserNotFoundException;
import backend.audit.Auditor;
import backend.serializer.AccountSerializer;
import backend.transitsystem.AbstractStation;
import backend.transitsystem.Route;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import backend.travelcard.CardSuspendedException;
import backend.travelcard.InsufficientBalanceException;
import backend.travelcard.TravelCard;
import backend.travelcard.TravelCardNotFoundException;
import backend.trip.IllegalEntryException;
import backend.trip.IllegalExitException;
import backend.trip.TripRegistrar;
import frontend.MainGUI;
import frontend.popupwindow.DialogueBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * The Screen to handle Card Tapping.
 */
public class CardTappingController {

  /**
   * The Manager instances of this application.
   */
  private AccountsManager accountsManager;
  private RoutesManager routesManager;
  private StationManager stationManager;
  private Auditor auditor;

  /**
   * FXML items.
   */
  public TextField cardID;
  public TextField userEmail;
  public TextField date;
  public TextField hour;
  public TextField minute;
  public Button tapIn;
  public Button tapOut;
  public Text freeStatus;
  public ListView<Route> routesListView;
  public ListView<AbstractStation> stationsListView;
  public Pane mainPane;
  Stage mainStage = new Stage();

  public CardTappingController() {}

  public void setAccountsManager(AccountsManager accountsManager) {
    this.accountsManager = accountsManager;
  }

  public void setRoutesManager(RoutesManager routesManager) {
    this.routesManager = routesManager;
  }

  public void setStationManager(StationManager stationManager) {
    this.stationManager = stationManager;
  }

  /**
   * Returns a TravelCard with the given email and card id
   * @param email The specified email
   * @param id The specified card id
   * @return The retrieved TravelCard.
   */
  private TravelCard getTravelCard(String email, int id) {
    try {
      return accountsManager.getTravelCard(email, id);
    } catch (NullPointerException | NumberFormatException npe) {
      new DialogueBox("Input Error", "Please fill in all the fields.");
    } catch (UserNotFoundException unfe) {
      new DialogueBox("Input Error", "User not found.").display();
    } catch (TravelCardNotFoundException tcnfe) {
      new DialogueBox("Input Error", "Travel card not found.").display();
    }
    return null;
  }

  /**
   * Taps a card IN/OUT
   * @param type User specified tapping IN/OUT
   */
  private void tap(String type) {
    try {
      TravelCard card = getTravelCard(userEmail.getText(), Integer.parseInt(cardID.getText()));
      LocalTime time =
          LocalTime.of(Integer.parseInt(hour.getText()), Integer.parseInt(minute.getText()));
      LocalDateTime dateTime = LocalDateTime.of(MainGUI.systemDate, time);
      AbstractStation station = stationsListView.getSelectionModel().getSelectedItem();
      double oldBalance = card.getBalance();
      if (type.equalsIgnoreCase("IN")) {
        card.tapIn(station, dateTime);
      } else if (type.equalsIgnoreCase("OUT")) {
        card.tapOut(station, dateTime);
      }
      double newBalance = card.getBalance();
      new DialogueBox(
              "",
              "Fare: $"
                  + (oldBalance - newBalance)
                  + System.lineSeparator()
                  + "$"
                  + newBalance
                  + " remaining.")
          .display();
    } catch (IllegalEntryException | IllegalExitException iee) {
      new DialogueBox(
              "Warning",
              "Abnormal behaviour detected, your card has been suspended."
                  + System.lineSeparator()
                  + "Please contact the customer service.")
          .display();
    } catch (CardSuspendedException cse) {
      new DialogueBox(
              "Warning",
              "Your card has been suspended."
                  + System.lineSeparator()
                  + "Please contact the customer service.")
          .display();
    } catch (InsufficientBalanceException ibe) {
      new DialogueBox(
              "Warning",
              "There is insufficient balance in your card."
                  + System.lineSeparator()
                  + "Please add credit to your card at the nearest machine.")
          .display();
    } finally {
      new AccountSerializer().saveState(accountsManager);
    }
  }

  /**
   * Taps in a card.
   */
  public void tapIn() {
    tap("IN");
  }

  /**
   * Taps out a card.
   */
  public void tapOut() {
    tap("OUT");
  }

  /**
   * Populates all stations of a selected route to screen.
   */
  public void populateStations() {
    Route selectedRoute = routesListView.getSelectionModel().getSelectedItem();
    ObservableList<AbstractStation> stations =
        FXCollections.observableList(selectedRoute.getStations());
    stationsListView.setItems(stations);
    stationsListView.refresh();
  }

  /**
   * Populates all routes to be selected.
   */
  public void populateRoutes() {
    routesListView.setItems(FXCollections.observableArrayList(routesManager.getAllRoutes()));
    routesListView.setCellFactory(
        new Callback<ListView<Route>, ListCell<Route>>() {
          @Override
          public ListCell<Route> call(ListView<Route> param) {
            return new ListCell<Route>() {
              @Override
              protected void updateItem(Route item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : (item.getName()));
              }
            };
          }
        });
    routesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  /**
   * Display this screen.
   * @param pane The pane that holds this interface.
   */
  public void display(Pane pane) {
    if (TripRegistrar.isCurrentlyFree()) {
      freeStatus.setVisible(true);
    } else {
      freeStatus.setVisible(false);
    }
    date.setText(MainGUI.systemDate.toString());
    mainStage.setScene(new Scene(pane));
    mainStage.show();
  }
}
