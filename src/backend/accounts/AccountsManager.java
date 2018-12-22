package backend.accounts;

import backend.Exceptions.EmailAlreadyExistsException;
import backend.Exceptions.UserNotFoundException;
import backend.travelcard.TravelCard;
import backend.travelcard.TravelCardNotFoundException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Manages all Cardholders in the system. Is serializable
 */
public class AccountsManager implements Serializable {

    /**
     * List of all accounts
     */
    private ArrayList<Cardholder> allCardHolders = new ArrayList<>();


    /**
     * Returns a Cardholder, given its email.
     *
     * @param email The given email.
     * @return The Cardholder with the given email, if it exists.
     * @throws UserNotFoundException If no such email exist.
     */
    public Cardholder getCardHolder(String email) throws UserNotFoundException {
        for (Cardholder cardholder : allCardHolders) {
            if (cardholder.getEmail().equals(email)) {
                return cardholder;
            }
        }
        throw new UserNotFoundException("Cardholder does not exist.");
    }

    /**
     * Returns true if Cardholder with given email exists.
     *
     * @param email The email of the target Cardholder.
     * @return True if such Cardholder exists.
     */
    private boolean hasUser(String email) {

        for (Cardholder cardholder : allCardHolders) {
            if (cardholder.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new Cardholder with given email and username.
     *
     * @param email    The specified email.
     * @param username The specified username.
     * @throws EmailAlreadyExistsException If the specified email has already been taken by another
     *                                     account.
     */
    public void createCardholder(String email, String username, String password) throws EmailAlreadyExistsException {
        if (hasUser(email))
            throw new EmailAlreadyExistsException("Email already exists, please choose another one.");

        Cardholder newAccount = new Cardholder(email, username, password);
        allCardHolders.add(newAccount);

    }

    /**
     * Removes the Cardholder, with the given email, from this manager.
     *
     * @param email The specified email of the Cardholder.
     * @return The removed Cardholder.
     */
    public Cardholder removeCardholder(String email) {
        try {
            Cardholder target = getCardHolder(email);
            allCardHolders.remove(target);
            return target;
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a TravelCard given its ID which belongs to the Cardholder with the given email.
     *
     * @param email The specified email of the Cardholder.
     * @param id    The specified ID of the TravelCard.
     * @return Returns the TravelCard, if it exists.
     */
    public TravelCard getTravelCard(String email, int id)
            throws UserNotFoundException, TravelCardNotFoundException {
        Cardholder user = getCardHolder(email);
        return user.getTravelCard(id);
    }

    /**
     * Initializes this AccountsManager and adds necessary listeners to its contained accounts.
     */
    public void reInitialize() {
        for (Cardholder user : allCardHolders) {
            user.reAddListeners();
        }
    }

    /**
     * Returns a String representation of all accounts in this manager.
     *
     * @return Returns a user friendly String representation of all accounts.
     */
    public String toString() {
        return allCardHolders.toString();
    }

    /**
     * Returns the list of all card holders in the manager.
     *
     * @return All CardHolders.
     */
    public ArrayList<Cardholder> getAllCardHolders() {
        return allCardHolders;
    }


}
