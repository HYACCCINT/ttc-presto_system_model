package backend.transitsystem;

import backend.Exceptions.InvalidInputException;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A route of stations.
 *
 * @param <E> Any subtype of AbstractStation.
 */
public class Route<E extends AbstractStation> implements Serializable {

    /**
     * The name of this route.
     */
    private String name;

    /**
     * The first station of this route.
     */
    private E head;

    /**
     * The last station of this route.
     */
    private E tail;

    private String type;


    /**
     * Creates a new Route with at least one station.
     *
     * @param name The specified name.
     * @param head The station of this route.
     */
    public Route(String name, AbstractStation head) throws InvalidInputException {
        this.name = name;
        this.head = (E) head;
        tail = (E) head;

        if (head != null && head.getType().equalsIgnoreCase("Bus")) {
            type = "bus";
        } else if (head != null && head.getType().equalsIgnoreCase("Train")) {
            type = "train";
        } else {
            throw new InvalidInputException("Unknown station class");
        }
    }

    public Route(String name, String type) {
        this.name = name;
        this.head = null;
        tail = head;
        this.type = type;
    }

    public String getType() {
        return type;
    }


    public E getHead() {
        return head;
    }

    public E getTail() {
        return tail;
    }

    /**
     * Returns a station of a given name.
     *
     * @param stationName The specified name.
     * @return The station, if it exists in this route.
     * @throws StationNotFoundException If no such station exists.
     */
    E getStation(String stationName) throws StationNotFoundException {
        if (head.getName().equals(stationName)) return head;
        E curr = head;
        while (!curr.getNext(this).equals(curr)) {
            curr = (E) curr.getNext(this);
            if (curr.getName().equals(stationName)) return curr;
        }
        throw new StationNotFoundException();
    }

    /**
     * Returns the name of this route.
     *
     * @return The name of this route.
     */
    public String getName() {
        return name;
    }


    public ArrayList<AbstractStation> getStations() {
        ArrayList<AbstractStation> stations = new ArrayList<>();
        AbstractStation cur = this.head;
        if (cur == null) {
            return stations;
        }
        while (cur.hasNext(this)) {
            stations.add(cur);
            cur = cur.getNext(this);
        }
        stations.add(cur);
        return stations;
    }

    public void setHead(E head) {
        this.head = head;
    }

    public void setTail(E tail) {
        this.tail = tail;
    }

    void deleteOnlyStation() {
        if (head.equals(tail)) {
            head = null;
            tail = null;
        }
    }

    void removeHead() {
        if (!head.equals(tail)) {
            E newHead = (E) head.getNext(this);
            head = newHead;
        }else{head = null; tail= null;}
    }

    void removeTail() {
        if (!head.equals(tail)) {
            E newTail = (E) head.getPrev(this);
            tail = newTail;
        }else{head = null; tail= null;}
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(getName() + ": " + head.toString());
        E curr = head;
        while (curr.hasNext(this)) {
            curr = (E) curr.getNext(this);
            ret.append(" -> ").append(curr.toString());
        }
        return ret.toString();
    }

}
