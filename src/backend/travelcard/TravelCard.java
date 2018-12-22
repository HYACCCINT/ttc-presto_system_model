package backend.travelcard;

import backend.accounts.MonthlyCostTracker;
import backend.audit.Auditor;
import backend.log.LogFileHandler;
import backend.transitsystem.AbstractStation;
import backend.travelcard.balances.Balance;
import backend.trip.IllegalEntryException;
import backend.trip.IllegalExitException;
import backend.trip.Trip;
import backend.trip.TripRegistrar;
import frontend.MainSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A travel card that tracks balance and trips.
 */
public class TravelCard implements Serializable {


  /** The total number of cards in this system. */
  private static int numCards = 0;

  /** The unique ID of this card. */
  private final int id;

  /** Whether this card is suspended. */
  private boolean suspended;

  /** Email of card holder is stored in a Travel Card for efficient retrieval. */
  private String email;

  /** The most recent trips made by this card. */
  private FixedStack<Trip> recentTrips;

  /** The balance of this card. */
  private Balance balance;

  /** The registrar that handles logic to create or complete trips for this card. */
  private transient TripRegistrar tripRegistrar;

  /** The logger for logging card tapping activities. */
  private static final Logger logger = Logger.getLogger(TravelCard.class.getName());

  /**
   * Creates a TravelCard owned by the specified email.
   *
   * @param email The specified email.
   */
  public TravelCard(String email) throws IOException {
    this.email = email;
    numCards++;
    id = numCards;
    balance = new Balance(19.0);
    recentTrips = new FixedStack<>(3);
    tripRegistrar = new TripRegistrar();
    suspended = false;
    logger.setUseParentHandlers(false);
    logger.addHandler(new LogFileHandler());
  }

  /**
   * Creates a TravelCard owned by the specified email.
   *
   * @param email The specified email.
   */
  TravelCard(String email, Balance balance) throws IOException {
    this(email);
    this.balance = balance;
  }

  /**
   * Returns the total number of cards created.
   *
   * @return The highest card number.
   */
  public static int getNumCards() {
    return numCards;
  }

  /**
   * Sets the incrementer of total number of cards created.
   *
   * @param count The highest card number.
   */
  public static void setNumCards(int count) {
    numCards = count;
  }


    /**
     * Attach observers that updates when trips are completed by this card.
     *
     * @param costTracker The listener that listens to backend.trip changes.
     */
    public void addListeners(MonthlyCostTracker costTracker) {
        if (tripRegistrar == null) tripRegistrar = new TripRegistrar();
        balance.addListener(Auditor.getAuditor());
        tripRegistrar.addTrafficListener(Auditor.getAuditor());
        tripRegistrar.addCostListener(costTracker);
        try {
            if (logger.getHandlers().length == 0) logger.addHandler(new LogFileHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the unique ID of this card.
     *
     * @return The ID of this card.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current balance of this card.
     *
     * @return The current balance.
     */
    public double getBalance() {
        return balance.getBalance();
    }

    /**
     * Adds balance to this card.
     *
     * @param value The value to be added.
     */
    public void addBalance(double value) {
        balance.add(value);
        System.out.println();
        logger.log(Level.INFO, "$" + value + " added to Card #" + getId());
    }

    /**
     * Registers a new backend.trip at specified station and timestamp to this card.
     *
     * @param station   The specified station.
     * @param timestamp The specified timestamp.
     */
    public double tapIn(AbstractStation station, LocalDateTime timestamp)
            throws CardSuspendedException, InsufficientBalanceException, IllegalEntryException {
        try {
            if (isSuspended()) throw new CardSuspendedException();
            double oldBalance = getBalance();
            tripRegistrar.registerTrip(station, timestamp, recentTrips, balance);
            logger.log(Level.INFO, "Card #" + getId() + " entered " + station + " at " + timestamp);
            if (oldBalance > getBalance()) {
                logger.log(
                        Level.INFO, "$" + (oldBalance - getBalance()) + " deducted from Card #" + getId());
                return oldBalance - getBalance();
            }
        } catch (CardSuspendedException cse) {
            logger.log(Level.WARNING, "Card #" + getId() + " attempts to use suspended card.");
            throw cse;
        } catch (InsufficientBalanceException ibe) {
            logger.log(
                    Level.WARNING,
                    "Card #" + getId() + " has insufficient balance to pay for trip fare.",
                    ibe);
            throw ibe;
        } catch (IllegalEntryException iee) {
            logger.log(
                    Level.SEVERE,
                    "Illegal entry behaviour detected for Card# " + getId() + " at " + station);
            suspend();
            throw iee;
        }
        return 0;
    }

    /**
     * Ends a current trip at specified station and timestamp to this card.
     *
     * @param station   The specified station.
     * @param timestamp The specified timestamp.
     */
    public void tapOut(AbstractStation station, LocalDateTime timestamp)
            throws CardSuspendedException, InsufficientBalanceException, IllegalExitException {
        try {
            if (isSuspended())
                throw new CardSuspendedException(
                        "Your card is suspended. Please contact the customer service.");
            double oldBalance = getBalance();
            tripRegistrar.updateTrip(station, timestamp, recentTrips.getLast(), balance);
            logger.log(Level.INFO, "Card #" + getId() + " exited from " + station + " at " + timestamp);
            if (oldBalance > getBalance()) {
                logger.log(
                        Level.INFO, "$" + (oldBalance - getBalance()) + " deducted from Card #" + getId());
            }
        } catch (CardSuspendedException cse) {
            logger.log(Level.WARNING, "Card #" + getId() + " attempts to use suspended card.");
            throw cse;
        } catch (InsufficientBalanceException ibe) {
            logger.log(
                    Level.WARNING,
                    "Card #" + getId() + " has insufficient balance to pay for trip fare.");
            throw ibe;
        } catch (IllegalExitException iee) {
            logger.log(
                    Level.SEVERE,
                    "Illegal exit behaviour detected for Card# " + getId() + " at " + station);
            suspend();
            throw iee;
        }
    }

    /**
     * Suspends this card.
     */
    public void suspend() {
        suspended = true;
        // addToHistory("suspend");
    }

    public String getEmail() {
        return email;
    }

    /**
     * Unsuspends this card.
     */
    public void activate() {
        suspended = false;
    }

    /**
     * Returns true if this card is suspended.
     *
     * @return Whether this card is suspended.
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Returns a list of the most recent trips travelled on this card.
     *
     * @return The list of recent trips.
     */
    public FixedStack<Trip> getRecentTrips() {
        return recentTrips;
    }

    /**
     * Resolves the current corrupted backend.trip caused by abnormal behaviour.
     */
    public void resolveCorruptedTrip() {
        tripRegistrar.removeCorruptedTrip(recentTrips);
    }

    @Override
    public String toString() {
        String ret = "Card #" + id;
        if (isSuspended()) ret += " [Suspended]";
        return ret;
    }


}
