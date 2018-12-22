package frontend.adminside.functionpane;

import backend.transitsystem.*;
import frontend.MainSystem;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Observer;

public class StationInterfaceController extends java.util.Observable {
    public ListView<Route> inRouteList;
    public ListView<AbstractStation> relatedStationList;
    public TextField routeInputTextField;
    public Button addInputedRoute;
    public Button deleteInputedRoute;
    public TableView stationHistoryTable;
    public TableColumn timeColumn;
    public TableColumn descriptionColumn;
    public ContextMenu routeListContextMenu;
    public MenuItem deleteRouteMenuClick;
    public ContextMenu relatedStationListContextMenu;
    AbstractStation station;
    public Text stationTypeText = new Text();

    private RoutesManager routesManager;
    private StationManager stationManager;

    public void setRoutesManager(RoutesManager routesManager) {
        this.routesManager = routesManager;
    }

    public void setStationManager(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public void setStation(AbstractStation station) {
        this.station = station;
    }

    public void display(){
        if(station instanceof BusStop){
            stationTypeText.setText("Bus Stop " + station.getName());
        }
        if(station instanceof TrainStation){
            stationTypeText.setText("Train Station " + station.getName());
        }
        displayRoutesList();
        inRouteList.refresh();
        relatedStationList.refresh();
    }

    void displayRoutesList(){
        ObservableList<Route> routeObservableList = FXCollections.observableList((station.getRoutes()));
        inRouteList.setItems(routeObservableList);
        inRouteList.setCellFactory(
                new Callback<ListView<Route>, ListCell<Route>>() {
                    @Override
                    public ListCell<Route> call(ListView<Route> param) {
                        return new ListCell<Route>() {
                            @Override
                            protected void updateItem(Route item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getName()));
                                setStyle("-fx-control-inner-background: #f5f5f5");

                            }
                        };
                    }
                });
        inRouteList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
    void displayRelatedStations(Route route){
        ObservableList<AbstractStation> stationObservableList = FXCollections.observableList((route.getStations()));
        relatedStationList.setItems(stationObservableList);
        relatedStationList.setCellFactory(
                new Callback<ListView<AbstractStation>, ListCell<AbstractStation>>() {
                    @Override
                    public ListCell<AbstractStation> call(ListView<AbstractStation> param) {
                        return new ListCell<AbstractStation>() {
                            @Override
                            protected void updateItem(AbstractStation item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getName()));
                                setStyle("-fx-control-inner-background: #f5f5f5");
                                if (item != null && item.equals(station)) {
                                    setStyle("-fx-control-inner-background: wheat");
                                }
                            }
                        };
                    }
                });
        relatedStationList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }


    public void returnButtonClick() {
        setChanged();
        notifyObservers("audit");
    }

    public void inRouteListClick(MouseEvent mouseEvent) {
        inRouteList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        routeListContextMenu.show(inRouteList, t.getScreenX(), t.getScreenY());
                    }
                });
        inRouteList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        Route target = inRouteList.getSelectionModel().getSelectedItem();
                        displayRelatedStations(target);
                    }
                });
        inRouteList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        inRouteList.refresh();
                    }
                });
    }

    public void relatedStationListClick(MouseEvent mouseEvent) {
        relatedStationList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.SECONDARY) {
                        relatedStationListContextMenu.show(inRouteList, t.getScreenX(), t.getScreenY());
                    }
                });
        relatedStationList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                        editSelectedStation();
                    }
                });
        relatedStationList.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                t -> {
                    if (t.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                        relatedStationList.refresh();
                    }
                });
    }

    public void editSelectedStation() {
        AbstractStation target = relatedStationList.getSelectionModel().getSelectedItem();
        setChanged();
        notifyObservers(target);
    }


    public void editSelectedRoute() {
        Route target = inRouteList.getSelectionModel().getSelectedItem();
        setChanged();
        notifyObservers(target);
    }

    public void addEInputedRouteClick() {
        String cmd = routeInputTextField.getText();
        Route route = null;
        if(!cmd.equals("")){
            route = routesManager.getRoute(cmd);


        }else if (inRouteList.getSelectionModel().getSelectedItem() != null){
             route = inRouteList.getSelectionModel().getSelectedItem();
        }
        routesManager.appendStop(this.station, route);
    }

    public void deleteInputedRouteClick() {
        String cmd = routeInputTextField.getText();
        Route route = null;
        if(!cmd.equals("")){
            route = routesManager.getRoute(cmd);
        }else if (inRouteList.getSelectionModel().getSelectedItem() != null){
            route = inRouteList.getSelectionModel().getSelectedItem();
        }
        try{
        stationManager.deleteStationFromRoute(route, this.station.getName());
        relatedStationList.getItems().clear();
        }
        catch (Exception e){e.printStackTrace();}
    }


    public void addCInputedRouteClick() {
        String cmd = routeInputTextField.getText();
        routeInputTextField.setText("");
        if(!cmd.equals("")){
            String type = "";
            if(this.station instanceof BusStop){
                type="bus";
            }
            else if(this.station instanceof TrainStation){
                type="train";
            }
            try{
            Route route = routesManager.addRoute(cmd, this.station, type);
            setChanged();
            notifyObservers(route);
                setChanged();
                notifyObservers("refreshSides");
           inRouteList.refresh();
            displayRoutesList();
           displayRelatedStations(route);
            }catch(Exception e){e.printStackTrace();}

        }
    }
}
