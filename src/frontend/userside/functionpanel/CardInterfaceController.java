package frontend.userside.functionpanel;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.serializer.AccountSerializer;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import backend.travelcard.TravelCard;
import backend.trip.Trip;
import frontend.popupwindow.AddBalanceController;
import frontend.popupwindow.DialogueBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Observable;

public class CardInterfaceController extends Observable {
    public Button returnButton = new Button();
    public TextField searchTripField = new TextField();
    public Button searchTripButton = new Button();
    public Text balanceText = new Text();
    public Text statusText = new Text();
    public TableView<Trip> recentTripsTableCard = new TableView<>();
    public TableColumn<Trip, String> startColumn = new TableColumn<>();
    public TableColumn<Trip, String> endColumn = new TableColumn<>();
    public TableColumn<Trip, String> descriptionColumn = new TableColumn<>();
    public TableColumn<Trip, String> fareColumn = new TableColumn<>();
    public Button addBalanceButton = new Button();
    public Button suspendButton = new Button();
    public Button unsuspendButton = new Button();
    public ListView recentTrips;

    private AccountsManager accountsManager;
    private RoutesManager routesManager;
    private StationManager stationManager;
    private Cardholder cardholder;
    private TravelCard card;

    public void setRoutesManager(RoutesManager routesManager) {
        this.routesManager = routesManager;
    }

    public void setStationManager(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    public void setCard(TravelCard card) {
        this.card = card;
    }

    public void display() {
        balanceText.setText("$ " + ((Double) this.card.getBalance()).toString());
        if (card.isSuspended()) {
            statusText.setText("SUSPENDED");
        } else {
            statusText.setText("ACTIVE");
        }
        populateRecentTrips();
    }

    private void populateRecentTrips() {
        ObservableList<Trip> travelCardObservableList =
                FXCollections.observableList((card.getRecentTrips()));
        recentTrips.setItems(travelCardObservableList);
        recentTrips.setCellFactory(
                new Callback<ListView<Trip>, ListCell<Trip>>() {
                    @Override
                    public ListCell<Trip> call(ListView<Trip> param) {
                        return new ListCell<Trip>() {
                            @Override
                            protected void updateItem(Trip item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : item.toString());
                            }
                        };
                    }
                });
        recentTrips.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void addBalance() {
        try {
            FXMLLoader loader = new FXMLLoader(AddBalanceController.class.getResource("AddBalance.fxml"));
            Pane pane = loader.load();
            AddBalanceController controller = loader.getController();
            controller.setAccountsManager(accountsManager);
            controller.setCard(card);
            controller.display(pane);
        } catch (IOException e) {
            e.printStackTrace();
            DialogueBox error = new DialogueBox("Error", "IOException");
            error.display();
        }
    }

    public void suspendCard() {
        cardholder.getUserOperations().suspendCard(card);
        new AccountSerializer().saveState(accountsManager);
        display();
    }

    public void unsuspendCard() {
        cardholder.getUserOperations().unsuspendCard(card);
        new AccountSerializer().saveState(accountsManager);
        display();
    }

    public void searchTrip(ActionEvent actionEvent) {
    }

    public void returnToUser() {
        setChanged();
        notifyObservers("user");
    }

    public TravelCard getCard() {
        return card;
    }


}
