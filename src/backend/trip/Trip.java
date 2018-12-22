package backend.trip;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A backend.trip taken by a TravelCard made up of TripSegments contained within the instance
 */
public class Trip implements Serializable {

    /**
     * A list of segments that make up this backend.trip
     */
    private ArrayList<TripTransaction> segments = new ArrayList<>();

    /**
     * The maximum fare that will be charged in a single backend.trip.
     */
    private double fareCap;

    /**
     * Initializes a Trip instance with a beginning TripTransaction transaction
     *
     * @param transaction the transaction that stores the time and station of the beginning (entrance)
     *                    of this tip
     */
    Trip(TripTransaction transaction) {
        fareCap = 6.0;
        segments.add(transaction);
    }

    /**
     * Initializes a Trip instances with a beginning TripTransaction and a specified fare cap.
     *
     * @param transaction the transaction that stores the time and station of the beginning (entrance)
     *                    *                    of this tip
     * @param fareCap     The specified fare cap of this trip.
     */
    Trip(TripTransaction transaction, double fareCap) {
        this.fareCap = fareCap;
        segments.add(transaction);
    }

    /**
     * Returns the first transaction of this trip.
     *
     * @return The first trip transaction.
     */
    public TripTransaction getFirstTransaction() {
        return segments.get(0);
    }

    /**
     * Returns the last transaction of this trip.
     *
     * @return The last trip transaction.
     */
    public TripTransaction getLastTransaction() {
        return segments.get(segments.size() - 1);
    }

    /**
     * Returns the maximum fare to be charged.
     *
     * @param fare The fare to be compared.
     * @return The fare to be charged, bounded by the cap.
     */
    double getMaxFare(double fare) {
        return Math.min(fare, fareCap);
    }

    /**
     * Decrement the amount of fare this backend.trip will have to pay for.
     *
     * @param fare The fare that was already paid.
     */
    void payFare(double fare) {
        fareCap -= fare;
    }

    /**
     * Adds a continuous TripSegment segment to this Trip
     *
     * @param segment the new segment to be added
     */
    void addSegment(TripTransaction segment) {
        segments.add(segment);
    }

    /**
     * Checks if this backend.trip is complete
     *
     * @return true iff this backend.trip is complete
     */
    boolean isComplete() {
        return segments.size() % 2 == 0;
    }

    /**
     * Returns this backend.trip in human readable format
     *
     * @return the string representation of this backend.trip
     */
    @Override
    public String toString() {
        String ret = "Entrance: " + getFirstTransaction() + System.lineSeparator();
        if (getFirstTransaction().equals(getLastTransaction())) {
            ret += "(Ongoing)";
        } else {
            ret += "Exit: " + getLastTransaction();
        }
        return ret;
    }

}
