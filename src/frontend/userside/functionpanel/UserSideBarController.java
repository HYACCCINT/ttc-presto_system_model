package frontend.userside.functionpanel;

import backend.Exceptions.UserNotFoundException;
import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.serializer.AccountSerializer;
import backend.travelcard.TravelCard;
import backend.trip.Trip;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * Controller for Sidebar menu in Cardholder Screen.
 */
public class UserSideBarController extends java.util.Observable {
    Cardholder cardholder;
    public TabPane userTab;
    public Button addCardButton;
    public Button suspendButton;
    public Button unsuspendButton;
    public ListView<TravelCard> cardList = new ListView<>();
    public ContextMenu cardContextMenu;
    public ContextMenu cardTypes;
    public ListView<Trip> tripsList = new ListView<>();
    public MenuItem elderlyCard;
    public MenuItem studentCard;

    private AccountsManager accountsManager;

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    /**
     * Creates an ElderlyTravelCard.
     */
    public void addElderlyCard() {
        cardholder.getUserOperations().createCard("ELDERLY");
        new AccountSerializer().saveState(accountsManager);
        displayLists();
    }

    /**
     * Creates a StudentTravelCard
     */
    public void addStudentCard() {
        cardholder.getUserOperations().createCard("STUDENT");
        new AccountSerializer().saveState(accountsManager);
        displayLists();
    }

    /**
     * Creates a standard TravelCard
     */
    public void addCard() {
        cardholder.getUserOperations().createCard();
        new AccountSerializer().saveState(accountsManager);
        displayLists();
    }

    /**
     * Suspends a card.
     *
     * @throws UserNotFoundException Thrown when specified user is not found
     */
    public void suspendCard() throws UserNotFoundException {
        ObservableList<TravelCard> oCardList = cardList.getSelectionModel().getSelectedItems();
        for (TravelCard card : oCardList) {
            accountsManager.getCardHolder(card.getEmail()).getUserOperations().suspendCard(card);
        }
        new AccountSerializer().saveState(accountsManager);
        cardList.refresh();
    }


    /**
     * Populates listview items with associated TravelCards and RecentTrips of this CardHolder.
     */
    public void displayLists() {
        ObservableList<TravelCard> travelCardObservableList =
                FXCollections.observableArrayList((cardholder.getTravelCards()));
        cardList.setItems(travelCardObservableList);
        cardList.setCellFactory(
                new Callback<ListView<TravelCard>, ListCell<TravelCard>>() {
                    @Override
                    public ListCell<TravelCard> call(ListView<TravelCard> param) {
                        return new ListCell<TravelCard>() {
                            @Override
                            protected void updateItem(TravelCard item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : item.toString());
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && item.isSuspended()) {
                                    setStyle("-fx-control-inner-background: wheat");
                                }
                            }
                        };
                    }
                });
        cardList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ArrayList<Trip> trips = new ArrayList<>();
        for (TravelCard card : cardholder.getTravelCards()) {
            trips.addAll(card.getRecentTrips());
        }
        ObservableList<Trip> tripObservableList =
                FXCollections.observableArrayList(trips);
        tripsList.setItems(tripObservableList);
        tripsList.setCellFactory(
                new Callback<ListView<Trip>, ListCell<Trip>>() {
                    @Override
                    public ListCell<Trip> call(ListView<Trip> param) {
                        return new ListCell<Trip>() {
                            @Override
                            protected void updateItem(Trip item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : item.toString());
                                setStyle("-fx-control-inner-background: #f5f5f5");
                            }
                        };
                    }
                });
        tripsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Adds listeners to clicking on card lists.
     * @param mouseEvent The mose event fired.
     */
    public void cardListClick(MouseEvent mouseEvent) {
        cardList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        cardContextMenu.show(cardList, t.getScreenX(), t.getScreenY());
                    }
                });
        cardList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        TravelCard target = cardList.getSelectionModel().getSelectedItem();
                        setChanged();
                        notifyObservers(target);
                    }
                });
        cardList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        cardList.refresh();
                    }
                });
    }

    /**
     * Updates the screen to show information of the selected travel card.
     */
    public void trackCard() {
        TravelCard target = cardList.getSelectionModel().getSelectedItem();
        setChanged();
        notifyObservers(target);
    }


}
