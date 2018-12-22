package frontend.userside;

import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import backend.travelcard.TravelCard;
import backend.trip.Trip;
import frontend.popupwindow.DialogueBox;
import frontend.userside.functionpanel.CardInterfaceController;
import frontend.userside.functionpanel.UserInterfaceController;
import frontend.userside.functionpanel.UserSideBarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Screen for CardHolder clients to manage their accounts and cards.
 */
public class UserMainController implements Observer {
    Cardholder cardholder;
    public BorderPane userBorderPane;
    private CardInterfaceController cardInterfaceController;
    private Pane cardPane;
    private UserSideBarController userSideBarController;
    private Pane sideBar;
    private UserInterfaceController userInterfaceController;
    private Pane userPane;
    private Stage mainStage = new Stage();

    private AccountsManager accountsManager;
    RoutesManager routesManager;
    StationManager stationManager;

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

    /**
     * Displays the window on the specified pane.
     *
     * @param pane The specified pane.
     */
    void display(BorderPane pane) {
        FXMLLoader cardLoader = new FXMLLoader();

        FXMLLoader sideLoader = new FXMLLoader();
        FXMLLoader userLoader = new FXMLLoader();

        try {
            userBorderPane = pane;
            mainStage.setScene(new Scene(userBorderPane));
            mainStage.show();
            initPanes(userLoader, cardLoader, sideLoader);
            cardInterfaceController = cardLoader.getController();
            userSideBarController = sideLoader.getController();
            userInterfaceController = userLoader.getController();
            setAll();
            userSideBarController.displayLists();
            userInterfaceController.display();
        } catch (IOException e) {
            DialogueBox error = new DialogueBox("Error", "IOException initializing userMain!");
            error.display();
            e.printStackTrace();
        }
    }

    /**
     * Initializes all panes on this screen.
     *
     * @param userLoader FXML loader for main user pane.
     * @param cardLoader FXML loader for main cards pane.
     * @param sideLoader FXML loader for main side menu pane.
     * @throws IOException
     */
    void initPanes(
            FXMLLoader userLoader, FXMLLoader cardLoader, FXMLLoader sideLoader)
            throws IOException {
        cardPane = loadPane("functionpanel/CardInterface.fxml", cardLoader);
        sideBar = loadPane("functionpanel/UserSideBar.fxml", sideLoader);
        userPane = loadPane("functionpanel/UserInterface.fxml", userLoader);
        userBorderPane.setLeft(sideBar);
        userBorderPane.setCenter(userPane);
    }

    /**
     * Load pane from specified FXML files.
     *
     * @param fxml   FXML files.
     * @param loader Loader for FXML files.
     * @return Returns the loaded pane.
     * @throws IOException Thrown when FXML file not found.
     */
    private Pane loadPane(String fxml, FXMLLoader loader) throws IOException {
        loader.setLocation(UserMainController.class.getResource(fxml));
        return loader.load();
    }

    /**
     * Sets all instances of managers to controllers associated to this screen.
     */
    void setAll() {
        cardInterfaceController.setAccountsManager(accountsManager);
        cardInterfaceController.setCardholder(cardholder);
        cardInterfaceController.addObserver(this);

        userSideBarController.setAccountsManager(accountsManager);
        userSideBarController.setCardholder(cardholder);
        userSideBarController.addObserver(this);
        userInterfaceController.setCardholder(cardholder);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof TravelCard) {
            showCardInterface((TravelCard) arg);

        } else if (arg instanceof Trip) {
        } else if (arg instanceof String) {
            String i = (String) arg;
            switch (i) {
                case "user":
                    showUserInterface();
                    break;
                default:
                    showUserInterface();
            }
        }
    }

    /**
     * Displays screen for managing travel cards.
     *
     * @param travelCard The selected card to be modified.
     */
    void showCardInterface(TravelCard travelCard) {
        cardInterfaceController.setCard(travelCard);
        cardInterfaceController.setRoutesManager(routesManager);
        cardInterfaceController.setStationManager(stationManager);
        userBorderPane.setCenter(cardPane);
        cardInterfaceController.display();
    }

    /**
     * Displays screen for account management.
     */
    void showUserInterface() {
        userBorderPane.setCenter(userPane);
        userInterfaceController.display();
    }

    /**
     * Returns the Stage of this screen.
     *
     * @return The Stage.
     */
    public Stage getStage() {
        return mainStage;
    }

}
