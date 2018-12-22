package frontend.adminside.functionpane;

import backend.accounts.Cardholder;
import backend.transitsystem.AbstractStation;
import backend.transitsystem.Route;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import frontend.popupwindow.DialogueBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Observable;

public class RouteInterfaceController extends Observable {
    public TableView stationTable;
    public TableColumn timeColumn;
    public TableColumn descriptionColumn;
    public TextField inputStation = new TextField();
    public ContextMenu stationContext;
    public MenuItem deleteStationFromRoute;
    public Text numStationText;
    Route route;
    public ListView<AbstractStation> stationList = new ListView<>();
    public Button returnButton = new Button();
    public Button addStationButton = new Button();
    public Text routeStatus;
    public Button deleteStationButton = new Button();
    public ListView<Cardholder> peopleList = new ListView<>();

    private RoutesManager routesManager;
    private StationManager stationManager;

    public void setRoutesManager(RoutesManager routesManager) {
        this.routesManager = routesManager;
    }

    public void setStationManager(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void returnClick() {
        setChanged();
        notifyObservers("audit");
    }


    public void addStationClick() throws Exception {
        String cmd = inputStation.getText();
        inputStation.setText("");
        if (cmd.length() != 0) {
            stationManager.newStationToRoute(route, cmd, route.getTail());
            display();
            setChanged();
            notifyObservers("refreshSides");
        } else {
            DialogueBox alertBox = new DialogueBox("Warning", "Command Not Found!");
            alertBox.display();
        }

//
    }

    void deleteFromRoute(String cmd) {
        if (!(cmd.length() == 0)) {
            try {
                stationManager.deleteStationFromRoute(this.route, cmd);
                displayLists();

            } catch (Exception e) {
                e.printStackTrace();
                DialogueBox alertBox = new DialogueBox("Warning", "Unknown exception when deleting!");
                alertBox.display();
            }
            displayLists();
        } else {
            DialogueBox alertBox = new DialogueBox("Warning", "Command Not Found!");
            alertBox.display();
        }
    }


    public void display() {
        displayLists();
        if (this.route.getType().equals("bus")) {
            routeStatus.setText("Bus Route");
        } else if (this.route.getType().equals("train")) {
            routeStatus.setText("Train Station");
        }
        numStationText.setText(((Integer) this.route.getStations().size()).toString());
    }

    void displayLists() {
        ObservableList<AbstractStation> stationObservableList = FXCollections.observableList((route.getStations()));
        stationList.setItems(stationObservableList);
        stationList.setCellFactory(
                new Callback<ListView<AbstractStation>, ListCell<AbstractStation>>() {
                    @Override
                    public ListCell<AbstractStation> call(ListView<AbstractStation> param) {
                        return new ListCell<AbstractStation>() {
                            @Override
                            protected void updateItem(AbstractStation item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getName()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && item.getRoutes().size() == 0) {
                                    setStyle("-fx-control-inner-background: wheat");
                                }
                            }
                        };
                    }
                });
        stationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


}
