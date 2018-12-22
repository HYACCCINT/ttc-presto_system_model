package frontend.popupwindow;

import backend.accounts.AccountsManager;
import backend.serializer.AccountSerializer;
import backend.travelcard.TravelCard;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Screen to add balance to a card.
 */
public class AddBalanceController implements Initializable {

    /**
     * FXML items.
     */
    public TextField amountInput = new TextField();
    public Button oneButton = new Button();
    public Button twoButton = new Button();
    public Button fiveButton = new Button();
    public Button tenButton = new Button();
    public Text currentBalance = new Text();
    public Button confirmButton = new Button();
    Stage stage = new Stage();

    private TravelCard card;
    private AccountsManager accountsManager;

    public void confirmClicked() {

        card.addBalance(Double.parseDouble(amountInput.getText()));
        currentBalance.setText(((Double) card.getBalance()).toString());
        amountInput.setText("");
    }

    public void setAccountsManager(AccountsManager accountsManager) {
        this.accountsManager = accountsManager;
    }

    public void setCard(TravelCard card) {
        this.card = card;
    }

    public void display(Pane pane) {
        currentBalance.setText(((Double) card.getBalance()).toString());
        Pane mainLayout = pane;
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        amountInput.setOnKeyPressed(
                keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        confirmClicked();
                    }
                });
//        currentBalance.setText(((Double) card.getBalance()).toString());
    }

    public void oneClicked() {
        card.addBalance(10);
        currentBalance.setText(((Double) card.getBalance()).toString());
        new AccountSerializer().saveState(accountsManager);
    }

    public void twoClicked() {
        card.addBalance(20);
        currentBalance.setText(((Double) card.getBalance()).toString());
        new AccountSerializer().saveState(accountsManager);

    }

    public void fiveClicked() {
        card.addBalance(50);
        currentBalance.setText(((Double) card.getBalance()).toString());
        new AccountSerializer().saveState(accountsManager);

    }
}
