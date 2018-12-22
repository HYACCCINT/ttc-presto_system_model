package frontend.popupwindow;

import backend.audit.Auditor;
import frontend.MainGUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class DateConfiguratorController {
    public DatePicker selectedDate = new DatePicker();
    public Button currentDayButton = new Button();

    Stage stage = new Stage();


    public void selectDate() {
        MainGUI.systemDate = selectedDate.getValue();
        Stage stage = (Stage) selectedDate.getScene().getWindow();
        stage.close();
        configureAuditorDate();
    }

    public void useCurrentDay() {
        MainGUI.systemDate = LocalDate.now();
        Stage stage = (Stage) currentDayButton.getScene().getWindow();
        stage.close();
        configureAuditorDate();
    }

    private void configureAuditorDate() {
        if (Auditor.getAuditor().getCurrentDate() == null) {
            Auditor.getAuditor().reset(MainGUI.systemDate);
            new DialogueBox("", "Statistics has been reset for " + Auditor.getAuditor().getCurrentDate()).display();
        }
    }

    public void display() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AddBalanceController.class.getResource("DateConfigurator.fxml"));
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
