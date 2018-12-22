package frontend.adminside.functionpane;

import backend.audit.Auditor;
import backend.transitsystem.AbstractStation;
import frontend.MainSystem;
import frontend.popupwindow.DialogueBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AuditInterfaceController {

    public DatePicker datePicker = new DatePicker();
    public TextField currentDate;
    public TextField totalProfit;
    public TextField totalTraffic;
    public ListView busiestStations;
    String selectedDate;
    public TextArea auditFileDisplayArea = new TextArea();

    private Auditor auditor;

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public void selectDate() {
        try {
            selectedDate = datePicker.getValue().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            displayAuditFile(selectedDate);
        } catch (IOException e) {
            DialogueBox errorDialogue =
                    new DialogueBox("No Audit File Found", "Please select another date.");
            errorDialogue.display();
        }
    }

    private void displayAuditFile(String date) throws IOException {
        File auditFile = new File(auditor.generateFilePath(date));
        FileReader fileReader = new FileReader(auditFile);
        BufferedReader br = new BufferedReader(fileReader);
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = br.readLine()) != null) {
            content.append(line).append(System.lineSeparator());
        }
        auditFileDisplayArea.setText(content.toString());
    }

    private void populateBusiestStations() {
        ObservableList<Map.Entry<AbstractStation, Integer>> list = FXCollections.observableList((auditor.getMaxTrafficStations(10)));
        busiestStations.setItems(list);
        busiestStations.setCellFactory(
                new Callback<ListView<Map.Entry<AbstractStation, Integer>>, ListCell<Map.Entry<AbstractStation, Integer>>>() {
                    @Override
                    public ListCell<Map.Entry<AbstractStation, Integer>> call(ListView<Map.Entry<AbstractStation, Integer>> param) {
                        return new ListCell<Map.Entry<AbstractStation, Integer>>() {
                            @Override
                            protected void updateItem(Map.Entry<AbstractStation, Integer> item, boolean empty) {
                                super.updateItem(item, empty);
                                setText((empty || item == null) ? "" : (item.getKey() + ": " + item.getValue()));
                            }
                        };
                    }
                });
    }

    public void display() {
        currentDate.setText(auditor.getCurrentDate().toString());
        totalProfit.setText(((Double) auditor.getProfit()).toString());
        totalTraffic.setText(((Integer) auditor.getTraffic()).toString());
        populateBusiestStations();
    }
}
