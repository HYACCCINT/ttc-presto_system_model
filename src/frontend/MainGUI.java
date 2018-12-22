package frontend;

import backend.accounts.AccountsManager;
import backend.audit.Auditor;
import backend.initializer.AccountsInitializer;
import backend.initializer.RoutesInitializer;
import backend.initializer.StationsInitializer;
import backend.serializer.AccountSerializer;
import backend.serializer.RouteSerializer;
import backend.serializer.StationSerializer;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import frontend.adminside.AdminMainController;
import frontend.cardtap.CardTappingController;
import frontend.popupwindow.DateConfiguratorController;
import frontend.popupwindow.DialogueBox;
import frontend.userside.UserLoginController;
import frontend.userside.UserRegisterController;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * The master class for front end GUI, the application will be initialized when running MainGUI, and
 * all components necessary for image view and tag manipulation will either be initialized in the
 * start method or be initialized in an object initialized in the start method.
 */
public class MainGUI extends Application {

    /**
     * The configurated system date input by user.
     */
    public static LocalDate systemDate;
    /**
     * The AccountManager instance of this system.
     */
    private AccountsManager accountsManager;
    /**
     * The RoutesManager instance of this system.
     */
    private RoutesManager routesManager;
    /**
     * The StationManager instance of this system.
     */
    private StationManager stationManager;

    /**
     * Stage window for the main application
     */
    private Stage mainStage;

    /**
     * Master border pane that holds all other panes (such as tree, center, etc.)
     */
    private AnchorPane mainLayout;

    /**
     * Initializes all Manager instances from file.
     */
    private void initialize() {
        AccountSerializer serializer = new AccountSerializer();
        accountsManager =
                new AccountsInitializer()
                        .initialize(serializer.getFilePath(), serializer.getCardsFilePath());
        routesManager = new RoutesInitializer().initialize(new RouteSerializer().getFilePath());
        stationManager = new StationsInitializer().initialize(new StationSerializer().getFilePath());
    }

    /**
     * Launches application.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        launch(args);
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                Auditor.getAuditor().writeAuditReport();
            }

        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        mainStage.setTitle("Transit System");
        FXMLLoader clientLoader = new FXMLLoader();
        display(clientLoader);
    }

    /**
     * Displays the main screen of this application.
     * @param mainLoader The FXML loader.
     * @throws IOException Thrown if fxml cannot be loaded.
     */
    private void display(FXMLLoader mainLoader) throws IOException {

        mainLoader.setLocation(MainGUI.class.getResource("MainView.fxml"));
        mainLayout = mainLoader.load();

        Scene scene = new Scene(mainLayout);
        mainStage.setScene(scene);
        mainStage.show();
        configureSystemDate();
    }

    /**
     * Opens the screen to configure system date.
     */
    private void configureSystemDate() {
        new DateConfiguratorController().display();
    }

    @FXML
    /**
     * Displays the Admin interface.
     */
    private void displayAdminMain() {
        try {
            initialize();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminside/AdminMain.fxml"));
            BorderPane pane = loader.load();
            AdminMainController controller = loader.getController();
            controller.setAccountsManager(accountsManager);
            controller.setRoutesManager(routesManager);
            controller.setStationManager(stationManager);
            controller.display(pane);
        } catch (Exception e) {
            e.printStackTrace();
            DialogueBox error = new DialogueBox("Error", "IOException");
            error.display();
        }
    }

    @FXML
    /**
     * Displays the User Login window.
     */
    private void displayUserLogin() {
        try {
            initialize();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userside/UserLogin.fxml"));
            Pane pane = loader.load();
            UserLoginController controller = loader.getController();
            controller.setAccountsManager(accountsManager);
            controller.setRoutesManager(routesManager);
            controller.setStationManager(stationManager);
            controller.display(pane);
        } catch (IOException e) {
            e.printStackTrace();
            DialogueBox error = new DialogueBox("Error", "IOException");
            error.display();
        }
    }

    @FXML
    /**
     * Displays the Account Registration window.
     */
    private void displayUserRegister() {
        try {
            initialize();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("userside/UserRegister.fxml"));
            Pane pane = loader.load();
            UserRegisterController controller = loader.getController();
            controller.setAccountsManager(accountsManager);
            controller.display(pane);
        } catch (IOException e) {
            new DialogueBox("Error", "IOException").display();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @FXML
    /**
     * Displays screen for Card Tapping.
     */
    private void displayCardTapping() {
        try {
            initialize();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cardtap/CardTapping.fxml"));
            Pane pane = loader.load();
            CardTappingController controller = loader.getController();
            controller.setAccountsManager(accountsManager);
            controller.setRoutesManager(routesManager);
            controller.setStationManager(stationManager);
            controller.populateRoutes();
            controller.display(pane);
        } catch (IOException e) {
            new DialogueBox("Error", "IOException").display();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
