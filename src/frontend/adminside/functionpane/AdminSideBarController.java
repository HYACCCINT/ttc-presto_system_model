package frontend.adminside.functionpane;

import backend.Exceptions.InvalidInputException;
import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.transitsystem.*;
import frontend.popupwindow.DialogueBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.util.InputMismatchException;
import java.util.Observable;

/**
 * Sidebar menu for admin operations.
 */
public class AdminSideBarController extends Observable {
    public TabPane userTab;
    public Button addRouteButton;
    public ListView<Route> routeList = new ListView<>();
    public ContextMenu routeContextMenu;
    public ListView<Cardholder> usersList = new ListView<>();
    public ContextMenu userContextMenu;
    public Button deleteRouteButton;
    public Button addStationButton;
    public Button deleteStationButton;
    public ListView<AbstractStation> stationListMain = new ListView<>();
    public ContextMenu routeContextMenu1;
    public ContextMenu stationContextMenu;
    public TextField routeEditTextField;
    public TextField stationEditTextField;
    public Button addBRouteButton;
    public Button addTStationButton;
    public Button addBStopButton;

    private AccountsManager accountsManager;
    private RoutesManager routesManager;
    private StationManager stationManager;

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
     *
     */
    public void editRoute() {
        Route route = routeList.getSelectionModel().getSelectedItem();
        setChanged();
        notifyObservers(route);
    }


    /**
     * Populates list of all routes in the manager.
     */
    private void populateRoutes() {
        ObservableList<Route> routeObservableList =
                FXCollections.observableList(routesManager.getAllRoutes());
        routeList.setItems(routeObservableList);
        routeList.setCellFactory(
                new Callback<ListView<Route>, ListCell<Route>>() {
                    @Override
                    public ListCell<Route> call(ListView<Route> param) {
                        return new ListCell<Route>() {
                            @Override
                            protected void updateItem(Route item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getName()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && item.getHead() == item.getTail()) {
                                    setStyle("-fx-control-inner-background: wheat");
                                }
                            }
                        };
                    }
                });
        routeList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    /**
     * Populates all stations of selected route.
     */
    private void populateStations() {
        ObservableList<AbstractStation> stationObservableList =
                FXCollections.observableArrayList(stationManager.getMasterList());
        stationListMain.setItems(stationObservableList);
        stationListMain.setCellFactory(
                new Callback<ListView<AbstractStation>, ListCell<AbstractStation>>() {
                    @Override
                    public ListCell<AbstractStation> call(ListView<AbstractStation> param) {
                        return new ListCell<AbstractStation>() {
                            @Override
                            protected void updateItem(AbstractStation item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getName()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && (item.getRoutes().size() == 0)) {
                                    setStyle("-fx-control-inner-background: wheat");
                                } else if (item != null && (item instanceof TrainStation)) {
                                    setStyle("-fx-control-inner-background: #E1FFFF");
                                } else if (item != null && (item instanceof BusStop)) {
                                    setStyle("-fx-control-inner-background: #98FB98");
                                }
                            }
                        };
                    }
                });
        stationListMain.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Populates list of all cardholders.
     */
    public void displayLists() {
        ObservableList<Cardholder> userObservableList =
                FXCollections.observableArrayList(accountsManager.getAllCardHolders());
        usersList.setItems(userObservableList);
        usersList.setCellFactory(
                new Callback<ListView<Cardholder>, ListCell<Cardholder>>() {
                    @Override
                    public ListCell<Cardholder> call(ListView<Cardholder> param) {
                        return new ListCell<Cardholder>() {
                            @Override
                            protected void updateItem(Cardholder item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getEmail()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                            }
                        };
                    }
                });
        usersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        populateRoutes();
        populateStations();
    }


    /**
     * Switch to audit screen.
     */
    public void showAudit() {
        setChanged();
        notifyObservers("audit");
    }

    /**
     * Updates selected route when clicked
     *
     * @param mouseEvent The mouse event fired.
     */
    public void routeListClick(MouseEvent mouseEvent) {
        routeList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        routeContextMenu.show(routeList, t.getScreenX(), t.getScreenY());
                    }
                });
        routeList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        editRoute();
                    }
                });
        routeList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        routeList.refresh();
                    }
                });
    }

    public void userListClick(MouseEvent mouseEvent) {
        usersList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        userContextMenu.show(routeList, t.getScreenX(), t.getScreenY());
                    }
                });
        usersList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        Cardholder target = usersList.getSelectionModel().getSelectedItem();
                        setChanged();
                        notifyObservers(target);
                    }
                });
        usersList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        usersList.refresh();
                    }
                });
    }

    public void userDetails() {
        Cardholder target = usersList.getSelectionModel().getSelectedItem();
    }


    public void stationListClick(MouseEvent mouseEvent) {
        stationListMain.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        stationContextMenu.show(stationListMain, t.getScreenX(), t.getScreenY());
                    }
                });
        stationListMain.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        AbstractStation target = stationListMain.getSelectionModel().getSelectedItem();
                        setChanged();
                        notifyObservers(target);
                    }
                });
        stationListMain.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        stationListMain.refresh();
                    }
                });
    }

    public void deleteStationClick() throws Exception {
        AbstractStation station = stationListMain.getSelectionModel().getSelectedItem();
        if (stationManager.stationExists(station)) {
            stationManager.deleteStation(station);
        }
        setChanged();
        notifyObservers("refreshSides");
        setChanged();
        notifyObservers("audit");
    }

    public void editStationClick() {
        AbstractStation station = stationListMain.getSelectionModel().getSelectedItem();
        setChanged();
        notifyObservers(station);
    }


    public void addTStationFromInputClick() {
        String cmd = stationEditTextField.getText();
        try {
            if (cmd.equals("")) {
                throw new InvalidInputException("invalid command");
            }
        } catch (InvalidInputException e) {
            DialogueBox d = new DialogueBox("Error", "Invalid Station Name!");
            d.display();
        }

        AbstractStation station = stationManager.newStationAlone(cmd, "train");
        setChanged();
        notifyObservers(station);
        setChanged();
        notifyObservers("refreshSides");

    }

    public void addBStopFromInputClick() {
        String cmd = stationEditTextField.getText();
        if (cmd.equals("")) {
            DialogueBox d = new DialogueBox("Error", " Invalid Command!");
            d.display();
        }
        AbstractStation station = stationManager.newStationAlone(cmd, "bus");
        setChanged();
        notifyObservers(station);
        setChanged();
        notifyObservers("refreshSides");
    }

    public void addBRouteFromInputClick() {
        String cmd = routeEditTextField.getText();
        if (cmd.equals("")) {
            DialogueBox d = new DialogueBox("Error", " Invalid Command!");
            d.display();
        }
        Route route = routesManager.addRoute(cmd, "bus");
        setChanged();
        notifyObservers(route);
        setChanged();
        notifyObservers("refreshSides");
    }


    public void addTRouteFromInputClick() {
        String cmd = routeEditTextField.getText();
        if (cmd.equals("")) {
            throw new InputMismatchException("invalid command");
        }
        Route route = routesManager.addRoute(cmd, "train");
        setChanged();
        notifyObservers(route);
        setChanged();
        notifyObservers("refreshSides");
    }
}
