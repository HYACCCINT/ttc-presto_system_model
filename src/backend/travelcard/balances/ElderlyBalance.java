package backend.travelcard.balances;

import backend.travelcard.InsufficientBalanceException;

/**
 * The balance of an ElderlyTravelCard. A 50% discount is always applied.
 */
public class ElderlyBalance extends Balance {

    /**
     * Creates an ElderlyBalance with an initial value.
     * @param value
     */
    public ElderlyBalance(double value) {
        super(value);
    }

    @Override
    public void deduct(double value) throws InsufficientBalanceException {
        checkBalance();
        balance -= value / 2;
        changeSupport.firePropertyChange("balance", null, value / 2);
    }
}
