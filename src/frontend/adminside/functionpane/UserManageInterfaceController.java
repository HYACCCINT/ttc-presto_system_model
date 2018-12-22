package frontend.adminside.functionpane;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.serializer.AccountSerializer;
import backend.travelcard.TravelCard;
import backend.trip.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.*;

public class UserManageInterfaceController extends Observable {

    public Button returnButton;
    public ListView<TravelCard> cardList = new ListView<>();

    public Button suspendCard;
    public Button unsuspendCard;
    public Button resolveTrip;
    public ListView<Trip> recentTripList = new ListView<>();
    public Text balanceText = new Text();
    public Text statusText = new Text();
    public ContextMenu cardContextMenu;
    public Button removeCard;
    public Button removeUser;
    public MenuItem suspendButtonContext;
    public MenuItem unsuspendButtonContext;
    public TableView<Map.Entry<String, String>>  userHistory;
    public TableColumn <HashMap.Entry<String, String>, String> descriptionColumnUser;
    public TableColumn <HashMap.Entry<String, String>, String> dateColumnUser;
    public TableColumn descriptionColumn;
    public Text userEmailText;
    public Text userPasswordText;
    private LinkedHashMap<String, String> data = new LinkedHashMap<>();

    Cardholder cardholder;
    private AccountsManager accountsManager;

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void returnClicked() {
        setChanged();
        notifyObservers("audit");
    }



    public void suspendClicked( ) {
        TravelCard card = cardList.getSelectionModel().getSelectedItem();
        cardholder.getUserOperations().suspendCard(card);
        new AccountSerializer().saveState(accountsManager);
        statusText.setText("SUSPENDED");
        displayCardLists();
    }

    public void unsuspendClicked( ) {
        TravelCard card = cardList.getSelectionModel().getSelectedItem();
        cardholder.getUserOperations().unsuspendCard(card);
        new AccountSerializer().saveState(accountsManager);
        statusText.setText("ACTIVE");
        displayCardLists();
    }

    public void resolveTripClicked( ) {
        TravelCard card = cardList.getSelectionModel().getSelectedItem();
        card.resolveCorruptedTrip();
        unsuspendClicked();
        recentTripDisplay(card);
    }

    public void removeCardClicked( ) {
        TravelCard card = cardList.getSelectionModel().getSelectedItem();
        cardholder.removeTravelCard(card.getId());
        new AccountSerializer().saveState(accountsManager);
        displayCardLists();
    }

    public void removeUserClicked( ) {
        accountsManager.removeCardholder(cardholder.getEmail());
        new AccountSerializer().saveState(accountsManager);

    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    public void display() {
        userEmailText.setText(cardholder.getEmail());
        userPasswordText.setText(cardholder.getPassword());

        displayCardLists();
        displayTrips();
    }

    public void displayCardLists() {
        ObservableList<TravelCard> travelCardObservableList = FXCollections.observableArrayList((cardholder.getTravelCards()));
        cardList.setItems(travelCardObservableList);
        cardList.setCellFactory(
                new Callback<ListView<TravelCard>, ListCell<TravelCard>>() {
                    @Override
                    public ListCell<TravelCard> call(ListView<TravelCard> param) {
                        return new ListCell<TravelCard>() {
                            @Override
                            protected void updateItem(TravelCard item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.toString()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && item.isSuspended()) {
                                    setStyle("-fx-control-inner-background: wheat");
                                }
                            }
                        };
                    }
                });
        cardList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void displayTrips(){
        ArrayList<Trip> allRecentTrips = new ArrayList<>();
        for (TravelCard card : cardholder.getTravelCards()){
            allRecentTrips.addAll(card.getRecentTrips());
        }

    }


    public void cardListClick(MouseEvent mouseEvent) {
        cardList.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                cardContextMenu.show(cardList, t.getScreenX(), t.getScreenY());
            }
        });
        cardList.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                TravelCard target = cardList.getSelectionModel().getSelectedItem();
                System.out.println(target.getRecentTrips());
                recentTripDisplay(target);
            }
        });
        cardList.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                cardList.refresh();
            }
        });
        TravelCard card = cardList.getSelectionModel().getSelectedItem();
        balanceText.setText("$ " + ((Double) card.getBalance()).toString());
        if (card.isSuspended()) {
            statusText.setText("SUSPENDED");
        } else {
            statusText.setText("ACTIVE");
        }
    }

    void recentTripDisplay(TravelCard card) {
        ObservableList<Trip> tripObservableList = FXCollections.observableList((card.getRecentTrips()));
        recentTripList.setItems(tripObservableList);
        recentTripList.setCellFactory(
                new Callback<ListView<Trip>, ListCell<Trip>>() {
                    @Override
                    public ListCell<Trip> call(ListView<Trip> param) {
                        return new ListCell<Trip>() {
                            @Override
                            protected void updateItem(Trip item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.toString()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                            }
                        };
                    }
                });
        recentTripList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
