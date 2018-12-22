package frontend.userside;

import backend.accounts.AccountsManager;
import backend.Exceptions.EmailAlreadyExistsException;
import backend.serializer.AccountSerializer;
import frontend.popupwindow.DialogueBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Screen to handle Account Registration.
 */
public class UserRegisterController {

  /**
   * The AccountsManager instance of this application.
   */
  private AccountsManager accountsManager;

  @FXML public Button submitButton;
  @FXML public TextField usernameField;
  @FXML public TextField emailField;
  @FXML public TextField passwordField;

  private Stage stage = new Stage();

  public void setAccountsManager(AccountsManager accountsManager) {
    this.accountsManager = accountsManager;
  }

  /**
   * Displays the window on a specified pane.
   * @param pane The pane to show the screen on.
   */
  public void display(Pane pane) {
    stage.setScene(new Scene(pane));
    stage.show();
  }

  @FXML
  /**
   * Registers the specified account to the manager from user input.
   */
  public void registerUser(ActionEvent actionEvent) {
    String userName = usernameField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();
    if (userName.length() == 0 || email.length() == 0 || password.length() == 0) {
      new DialogueBox("Input Error", "Please fill out all fields!").display();
    } else {
      try {
        accountsManager.createCardholder(email, userName, password );
        DialogueBox success =
            new DialogueBox(
                "Success!", "You can now login with your email:" + System.lineSeparator() + email);
        stage.close();
        success.display();
        new AccountSerializer().saveState(accountsManager);
      } catch (EmailAlreadyExistsException e) {
        new DialogueBox(
                "Error",
                "An account with this email already exists!"
                    + System.lineSeparator()
                    + "Please try another email.")
            .display();
      }
    }
  }
}
