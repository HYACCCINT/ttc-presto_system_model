package frontend.userside.functionpanel;


import backend.accounts.Cardholder;
import backend.travelcard.TravelCard;
import backend.trip.Trip;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

/**
 * Controller for managing CardHolder account.
 */
public class UserInterfaceController implements Observer {
    public Text userNameText = new Text();
    public Text avgCostText = new Text();
    public Pane userPane = new Pane();

    public ListView<Trip> recentTripsList;
    public TextField newNameText;
    public TableView<Map.Entry<String, String>> operationTable;
    public TableColumn<HashMap.Entry<String, String>, String> timeColumn;
    public TableColumn<HashMap.Entry<String, String>, String> descriptionColumn;
    private Cardholder cardholder;
    private LinkedHashMap<String, String> data = new LinkedHashMap<>();
    /**
     * Initializes a LinkedHashMap tht will become a copy of userHistory to be converted to an observable list.
     */


    /**
     * Changes the username of this account.
     */
    public void changeUserName() {

        String name = newNameText.getText();
        try {
            if (name.equals("")) {
                throw new IOException();
            }
            cardholder.getUserOperations().changeUsername(name);
            newNameText.setText("");
            display();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals("update")) {
            this.display();
        }
    }


    public Cardholder getCardholder() {
        return cardholder;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }


    /**
     * Displays this screen.
     */
    public void display() {
        String welcome = cardholder.getUsername() + "     <" + cardholder.getEmail() + ">";
        userNameText.setText(welcome);
        String avgCost = String.valueOf(cardholder.getAverageMonthlyCost());
        avgCostText.setText(avgCost);
        displayList();
        showHistory();
    }

    /**
     * Populates the list of user operation history.
     */
    void showHistory() {
        data.putAll(cardholder.getUserOperations().getOperationHistory());
        timeColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        descriptionColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue()));
        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList(data.entrySet());
        operationTable.setItems(items);
        operationTable.getColumns().setAll(timeColumn, descriptionColumn);
    }

    /**
     * Populates list of recent trips.
     */
    void displayList() {
        ArrayList<Trip> trips = new ArrayList<>();
        for (TravelCard card : cardholder.getTravelCards()) {
            trips.addAll(card.getRecentTrips());
        }
        ObservableList<Trip> travelCardObservableList =
                FXCollections.observableList(trips);
        recentTripsList.setItems(travelCardObservableList);
        recentTripsList.setCellFactory(
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
        recentTripsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Shows operation history upon mouseclick.
     * @param mouseEvent The mouse event fired.
     */
    public void oTableClick(javafx.scene.input.MouseEvent mouseEvent) {
        operationTable.refresh();
        showHistory();

    }
}
