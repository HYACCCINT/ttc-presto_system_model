package backend.travelcard.balances;

import backend.travelcard.InsufficientBalanceException;

/**
 * The balance of an StudentTravelCard. A 10% discount is always applied.
 */
public class StudentBalance extends Balance {

    /**
     * Creates an ElderlyBalance with an initial value.
     * @param value
     */
    public StudentBalance(double value) {
        super(value);
    }

    @Override
    public void deduct(double value) throws InsufficientBalanceException {
        checkBalance();
        balance -= value * 0.9;
        changeSupport.firePropertyChange("balance", null, value * 0.9);
    }
}
