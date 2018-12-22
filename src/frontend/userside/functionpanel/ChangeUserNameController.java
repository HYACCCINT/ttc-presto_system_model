package frontend.userside.functionpanel;

import backend.accounts.Cardholder;
import frontend.popupwindow.DialogueBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller that handles username change.
 */
public class ChangeUserNameController extends java.util.Observable {

    public Cardholder cardholder;
    public TextField newName = new TextField();
    public Button confirmButton = new Button();
    Stage stage = new Stage();

    /**
     * Confirms and changes the username of this account.
     */
    public void confirmNameChange() {
        String name = newName.getText();
        try {
            if (name.equals("")) {
                throw new IOException();
            }
            cardholder.setUsername(name);
            setChanged();
            notifyObservers("update");
        } catch (IOException e) {
            System.out.println("name should not be blank");

        }

    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    /**
     * Displays this screen.
     */
    public void display() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ChangeUserNameController.class.getResource("ChangeUserName.fxml"));
            Pane mainLayout = loader.load();
            stage.setScene(new Scene(mainLayout));

            stage.show();
        } catch (IOException e) {
            DialogueBox d =
                    new DialogueBox(
                            "Error", "IOException occurred");
            d.display();
        }
    }


}


