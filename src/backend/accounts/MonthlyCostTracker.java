package backend.accounts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.time.Month;
import java.util.HashMap;

/**
 * A tracker of credit flow of a single Cardholder
 */
public class MonthlyCostTracker implements PropertyChangeListener, Serializable {

    /**
     * Maps the amount of credit user spends to the corresponding month
     */
    HashMap<Month, Double> monthlyCosts;

    /**
     * Constructor initiates this monthly cost hash map
     */
    MonthlyCostTracker() {
        monthlyCosts = new HashMap<>();
    }

    /**
     * Increments the expenditure of the month by cost Used to update cost when user takes a new trip.
     *
     * @param month the current month
     * @param cost  the amount spent by cardholder
     */
    private void incrementCost(Month month, Double cost) {
        Double currentTotal = monthlyCosts.get(month);
        monthlyCosts.replace(month, currentTotal + cost);
    }

    /**
     * Calculates and returns the average amount spent in all of the months.
     *
     * @return the average spending per month
     */
    double getAverageCost() {
        int months = 0;
        double total = 0;
        for (Month month : monthlyCosts.keySet()) {
            months++;
            total += monthlyCosts.get(month);
        }
        if (months == 0) return 0;
        return total / months;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("monthlyCost")) {
            Month eventMonth = (Month) evt.getOldValue();
            if (monthlyCosts.containsKey(eventMonth)) {
                incrementCost(eventMonth, (Double) evt.getNewValue());
            } else {
                monthlyCosts.put(eventMonth, (Double) evt.getNewValue());
            }
        }
    }
}
