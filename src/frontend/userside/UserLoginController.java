package frontend.userside;

import backend.Exceptions.UserNotFoundException;
import backend.accounts.AccountsManager;
import backend.accounts.Cardholder;
import backend.serializer.AccountSerializer;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;
import frontend.popupwindow.DialogueBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Log-in window for CardHolders.
 */
public class UserLoginController {

    private AccountsManager accountsManager;

    private Stage stage = new Stage();

    @FXML
    public TextField userEmail;
    @FXML
    public PasswordField userPassword;
    @FXML
    public Button loginButton;
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

    @FXML
    public void display(Pane pane) throws IOException {
        stage.setScene(new Scene(pane));
        stage.show();
    }

    @FXML
    /**
     * Displays the window for logging into accounts.
     */
    public void displayUserMain(ActionEvent actionEvent) {

        String email = userEmail.getText();
        try {
            Cardholder cardholder = accountsManager.getCardHolder(email);
            if (!cardholder.getPassword().equals(userPassword.getText())) {
                new DialogueBox("Input Error", "Password does not match!").display();
            } else {
                FXMLLoader mainLoader = new FXMLLoader();
                mainLoader.setLocation(UserMainController.class.getResource("UserMain.fxml"));
                BorderPane mainLayout = mainLoader.load();
                UserMainController userMainController = mainLoader.getController();
                userMainController.getStage().setOnCloseRequest(event -> {
                    System.out.println("Save on close");
                    new AccountSerializer().saveState(accountsManager);
                });
                userMainController.setAccountsManager(accountsManager);
                userMainController.setCardholder(cardholder);
                userMainController.setRoutesManager(routesManager);
                userMainController.setStationManager(stationManager);
                userMainController.display(mainLayout);

                stage.close();
            }
        } catch (UserNotFoundException e) {
            DialogueBox error = new DialogueBox("Error", "User not found!");
            error.display();
        } catch (Exception ee) {
            ee.printStackTrace();
            DialogueBox error = new DialogueBox("Error", "Unknown exception when displaying userMain!");
            error.display();
        }
    }
}
