package backend.accounts;

import backend.travelcard.TravelCard;
import frontend.popupwindow.DialogueBox;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Performs and keeps record of operations of user
 */
public class UserOperations implements Serializable {

    /**
     * The CardHolder of this userOperation these operations.
     */
    Cardholder cardholder;
    HashMap<String, String> operationHistory = new HashMap<>();

    /**
     * Initializes this UserOperations
     * @param cardholder sets card holder
     */
    public UserOperations(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    /**
     * Getter for cardholder
     * @return this cardholder
     */
    public Cardholder getCardholder() {
        return cardholder;
    }

    /**
     * Setter for this cardholder
     */
    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    /**
     * Creates a card for this cardholder
     */
    public void createCard() {
        try {
            TravelCard card = cardholder.createTravelCard();
            addHistory("addCard", ((Integer) card.getId()).toString());
        } catch (IOException e) {
            DialogueBox d = new DialogueBox("Error","IOException when creating card");
            d.display();
        }
    }

    /**
     * Creates special type cards
     * @param type the type of card to create
     */
    public void createCard(String type) {
        try {
            if (type.equalsIgnoreCase("ELDERLY")) {
                TravelCard card = cardholder.createTravelCard("ELDERLY");
                addHistory("addElderlyCard", ((Integer) card.getId()).toString());
            } else if (type.equalsIgnoreCase("STUDENT")) {
                TravelCard card = cardholder.createTravelCard("STUDENT");
                addHistory("addStudentCard", ((Integer) card.getId()).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the userName of the user
     * @param newUserName new userName
     */
    public void changeUsername(String newUserName) {
        String old = cardholder.getUsername();
        cardholder.setUsername(newUserName);
        String detail = old + " to " + newUserName;
        addHistory("newUsername", detail);
    }

    /**
     * Suspend card and add operation to history
     * @param card Travel Card
     */
    public void suspendCard(TravelCard card) {
        card.suspend();
        addHistory("suspendCard", ((Integer) card.getId()).toString());
    }

    /**
     * Unsuspend card and add operation to history
     * @param card Travel Card
     */
    public void unsuspendCard(TravelCard card) {
        card.activate();
        addHistory("unsuspendCard", ((Integer) card.getId()).toString());
    }

    /**
     * Adds to operation history based on commda cmd, with description of detail
     * @param cmd command
     * @param detail description
     */
    void addHistory(String cmd, String detail) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        switch (cmd) {
            case "addCard":
                operationHistory.put(time.toString(), "Added Card #" + detail);
                break;
            case "addElderlyCard":
                operationHistory.put(time.toString(), "Added Elderly Card #" + detail);
                break;
            case "addStudentCard":
                operationHistory.put(time.toString(), "Added Student Card #" + detail);
                break;
            case "suspendCard":
                operationHistory.put(time.toString(), "Suspended Card #" + detail);
                break;
            case "unsuspendCard":
                operationHistory.put(time.toString(), "Unsuspended Card #" + detail);
                break;
            case "newUsername":
                operationHistory.put(time.toString(), "Changed username from  " + detail);
                break;
            case "tapin":
                operationHistory.put(time.toString(), "Began trip at " + detail);
                break;
            case "tapout":
                operationHistory.put(time.toString(), "Ended trip at " + detail);
                break;
        }
    }


    /**
     * Getter for user operation history
     * @return history
     */
    public HashMap<String, String> getOperationHistory() {
        return operationHistory;
    }
}
