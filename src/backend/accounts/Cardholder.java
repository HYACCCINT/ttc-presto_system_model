package backend.accounts;

import backend.travelcard.ElderlyTravelCard;
import backend.travelcard.StudentTravelCard;
import backend.travelcard.TravelCard;
import backend.travelcard.TravelCardNotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * An account that owns a set of TravelCards
 */
public class Cardholder implements Serializable {

    /**
     * The chosen username of this account.
     */
    private String username;

    /**
     * The chosen email of this account that cannot be changed.
     */
    private String email;

    private String password;
    /**
     * The list of all TravelCards this account owns.
     */
    private ArrayList<TravelCard> travelCards = new ArrayList<>();

    /**
     * A tracker that calculates monthly costs of this account.
     */
    private MonthlyCostTracker monthlyCostTracker;

    /**
     * An userOperations that performs and holds a record of all account activities for this cardHolder.
     */
    private UserOperations userOperations;


    /**
     * Creates a new CardHolder of the given email and username.
     *
     * @param email    The specified email.
     * @param username The specified username.
     */
    public Cardholder(String email, String username, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        monthlyCostTracker = new MonthlyCostTracker();
        userOperations = new UserOperations(this);
    }

    /**
     * Constructor for a new user Cardholder, initializes a new cost tracker
     */
    public Cardholder() {
        monthlyCostTracker = new MonthlyCostTracker();
    }

    /**
     * Modifies the username of this account.
     *
     * @param newUsername The specified new username to be changed.
     */
    public void setUsername(String newUsername) {
        username = newUsername;
    }

    /**
     * Returns the email of this account.
     *
     * @return The email of this account.
     */
    public String getEmail() {
        return email;
    }

    /**
     * A getter for private user password
     *
     * @return password String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Buys a newly made travelcard, adds it to the list of user's collection of cards
     *
     * @param card TravelCard to buy
     * @return card TravelCard passes the instance of the card to Mana
     */
    private TravelCard addTravelCard(TravelCard card) {
        travelCards.add(card);
        card.addListeners(monthlyCostTracker);
        return card;
    }

    /**
     * Creates a standard TravelCard for this account.
     *
     * @return The card created.
     * @throws IOException Thrown if log file cannot be created.
     */
    public TravelCard createTravelCard() throws IOException {
        TravelCard newCard = new TravelCard(email);
        return addTravelCard(newCard);
    }

    /**
     * Creates a specified type of TravelCard for this account.
     *
     * @param type The specified type of TravelCard (ELDERLY or STUDENT).
     * @return The card created.
     * @throws IOException Thrown if log file cannot be created.
     */
    public TravelCard createTravelCard(String type) throws IOException {
        if (type.equalsIgnoreCase("ELDERLY")) {
            TravelCard newCard = new ElderlyTravelCard(email);
            return addTravelCard(newCard);
        } else if (type.equalsIgnoreCase("STUDENT")) {
            TravelCard newCard = new StudentTravelCard(email);
            return addTravelCard(newCard);
        }
        return null;
    }


    /**
     * Returns the username of this cardholder.
     *
     * @return The username of this cardholder.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the list of all travel cards of this cardholder.
     *
     * @return The list of all travel cards.
     */
    public ArrayList<TravelCard> getTravelCards() {
        return travelCards;
    }

    /**
     * Removes the TravelCard, given its ID.
     *
     * @param cardID The specified card ID.
     * @return Returns the removed TravelCard, if it exists.
     */
    public TravelCard removeTravelCard(int cardID) {
        try {
            TravelCard target = getTravelCard(cardID);
            travelCards.remove(target);
            return target;
        } catch (TravelCardNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Returns a TravelCard given its ID.
     *
     * @param cardID The specified cardID.
     * @return Returns the TravelCard of the given ID, if it exists.
     * @throws TravelCardNotFoundException If such card ID does not exist.
     */
    TravelCard getTravelCard(int cardID) throws TravelCardNotFoundException {
        for (TravelCard travelCard : travelCards) {
            if (travelCard.getId() == cardID) {
                return travelCard;
            }
        }
        throw new TravelCardNotFoundException("Travel card does not exist.");
    }

    /**
     * Returns the average monthly cost paid by this account.
     *
     * @return The average monthly cost.
     */
    public double getAverageMonthlyCost() {
        return monthlyCostTracker.getAverageCost();
    }

    /**
     * Adds the required listeners to all TravelCards of this account when the system is initialized.
     */
    void reAddListeners() {
        for (TravelCard card : travelCards) {
            card.addListeners(monthlyCostTracker);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Cardholder
                && getEmail().equalsIgnoreCase(((Cardholder) other).getEmail());
    }

    @Override
    public String toString() {
        return username + " (" + email + ") " + travelCards.size() + " cards";
    }

    public UserOperations getUserOperations() {
        return userOperations;
    }


}
