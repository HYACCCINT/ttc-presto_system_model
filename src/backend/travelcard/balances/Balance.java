package backend.travelcard.balances;

import backend.travelcard.InsufficientBalanceException;
import backend.travelcard.TravelCard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/** The balance stored in a TravelCard */
public class Balance implements Serializable {

  /** The monetary balance. */
  double balance;

  /** The support to notify listeners. */
  PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  /**
   * Creates a new balance of given value.
   *
   * @param value The spcified value.
   */
  public Balance(double value) {
    balance = value;
  }

  /**
   * Checks if there is sufficient balance for future transactions.
   *
   * @throws InsufficientBalanceException If there is negative balance.
   */
  void checkBalance() throws InsufficientBalanceException {
    if (balance < 0) {
      throw new InsufficientBalanceException("There is insufficient balance in your travel card.");
    }
  }

  /**
   * Adds credits to the balance.
   *
   * @param value The value to be added.
   */
  public void add(double value) {
    balance += value;
  }

  /**
   * Deduct this balance.
   *
   * @param value The balance to be deducted.
   * @throws InsufficientBalanceException If there is insufficient balance to be deducted.
   */
  public void deduct(double value) throws InsufficientBalanceException {
    checkBalance();
    balance -= value;
    changeSupport.firePropertyChange("balance", null, value);
  }

  /**
   * Returns the current balance.
   *
   * @return This balance.
   */
  public double getBalance() {
    return balance;
  }

  /**
   * Attaches a listener that observes changes to this balance.
   *
   * @param listener The listener to be added.
   */
  public void addListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener("balance", listener);
  }
}
