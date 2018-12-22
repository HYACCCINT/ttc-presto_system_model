package frontend.adminside;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.audit.Auditor;
import backend.serializer.RouteSerializer;
import backend.serializer.StationSerializer;
import backend.transitsystem.AbstractStation;
import backend.transitsystem.Route;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import backend.trip.TripRegistrar;
import frontend.adminside.functionpane.*;
import frontend.popupwindow.DialogueBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class AdminMainController implements Observer {
    public BorderPane adminBorderPane;
    AdminSideBarController adminSideBarController;
    Pane sidePane;
    AuditInterfaceController auditInterfaceController;
    Pane auditPane;
    RouteInterfaceController routeInterfaceController;
    Pane routePane;
    UserManageInterfaceController userManageInterfaceController;
    Pane userPane;
    StationInterfaceController stationInterfaceController;
    Pane stationPane;
    Stage mainStage = new Stage();

    private Auditor auditor;
    private AccountsManager accountsManager;
    private RoutesManager routesManager;

    private StationManager stationManager;

    public MenuBar menuBar = new MenuBar();

    public void setStationManager(StationManager stationManager) {
        this.stationManager = stationManager;
    }

    public void setRoutesManager(RoutesManager routesManager) {
        this.routesManager = routesManager;
    }

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void enableFreeTrips() {
        TripRegistrar.setCurrentlyFree(true);
        new DialogueBox("Success", "Trips are now free of charge.").display();
    }

    public void disableFreeTrips() {
        TripRegistrar.setCurrentlyFree(false);
        new DialogueBox("Success", "Trips are now paid for.").display();
    }


    public void display(BorderPane pane) {
        FXMLLoader routeLoader = new FXMLLoader();
        FXMLLoader tripLoader = new FXMLLoader();
        FXMLLoader sideLoader = new FXMLLoader();
        FXMLLoader userLoader = new FXMLLoader();
        FXMLLoader auditLoader = new FXMLLoader();
        FXMLLoader stationLoader = new FXMLLoader();

        try {
            adminBorderPane = pane;
            mainStage.setScene(new Scene(adminBorderPane));
            mainStage.show();
            initPanes(userLoader, routeLoader, sideLoader, auditLoader, stationLoader);
            routeInterfaceController = routeLoader.getController();
            adminSideBarController = sideLoader.getController();
            userManageInterfaceController = userLoader.getController();
            auditInterfaceController = auditLoader.getController();
            stationInterfaceController = stationLoader.getController();
            setAll();
            adminSideBarController.displayLists();
            auditInterfaceController.display();
        } catch (IOException e) {
            DialogueBox error = new DialogueBox("Error", "IOException initializing userMain!");
            error.display();
            e.printStackTrace();
        }
    }

    void initPanes(
            FXMLLoader userLoader,
            FXMLLoader routeLoader,
            FXMLLoader sideLoader,
            FXMLLoader auditLoader,
            FXMLLoader stationLoader)
            throws IOException {
        stationPane = loadPane("functionpane/StationInterface.fxml", stationLoader);
        routePane = loadPane("functionpane/RouteInterface.fxml", routeLoader);
        sidePane = loadPane("functionpane/AdminSideBar.fxml", sideLoader);
        userPane = loadPane("functionpane/UserManageInterface.fxml", userLoader);
        auditPane = loadPane("functionpane/AuditInterface.fxml", auditLoader);
        adminBorderPane.setLeft(sidePane);
        adminBorderPane.setCenter(auditPane);
    }

    private Pane loadPane(String fxml, FXMLLoader loader) throws IOException {
        loader.setLocation(AdminMainController.class.getResource(fxml));
        return loader.load();
    }

    void setAll() {
        adminSideBarController.addObserver(this);
        adminSideBarController.setAccountsManager(accountsManager);
        adminSideBarController.setRoutesManager(routesManager);
        adminSideBarController.setStationManager(stationManager);
        routeInterfaceController.addObserver(this);
        stationInterfaceController.addObserver(this);
        userManageInterfaceController.addObserver(this);
        userManageInterfaceController.setAccountsManager(accountsManager);
        auditInterfaceController.setAuditor(Auditor.getAuditor());
        mainStage.setOnCloseRequest( event -> {
            new RouteSerializer().saveState(routesManager);
            new StationSerializer().saveState(stationManager);
        });
    }


    void showRoutes(Route route) {
        routeInterfaceController.setRoute(route);
        routeInterfaceController.setRoutesManager(routesManager);
        routeInterfaceController.setStationManager(stationManager);
        adminBorderPane.setCenter(routePane);
        routeInterfaceController.display();
    }

    void showUsers(Cardholder user) {
        userManageInterfaceController.setCardholder(user);
        adminBorderPane.setCenter(userPane);
        userManageInterfaceController.display();
    }

    void showAudit(Auditor auditor) {
        auditInterfaceController.setAuditor(Auditor.getAuditor());
        adminBorderPane.setCenter(auditPane);
        auditInterfaceController.display();
    }

    void showStationPane(AbstractStation station) {
        stationInterfaceController.setStation(station);
        stationInterfaceController.setRoutesManager(routesManager);
        stationInterfaceController.setStationManager(stationManager);
        adminBorderPane.setCenter(stationPane);
        stationInterfaceController.display();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Route) {
            showRoutes((Route) arg);
        }
        if (arg instanceof Cardholder) {
            showUsers((Cardholder) arg);
        }
        if (arg instanceof AbstractStation) {
            showStationPane((AbstractStation) arg);
        } else if (arg instanceof String) {
            String i = (String) arg;
            switch (i) {
                case "audit":
                    showAudit(auditor);
                    break;
                case "refreshSides":
                  adminSideBarController.displayLists();
                  break;
                default:
                    showAudit(auditor);
            }
        }

    }
}
