package backend.transitsystem;

import backend.transitsystem.fares.AbstractFareCalculator;

import java.io.Serializable;
import java.util.*;

/**
 * An abstract station that may or may not have connecting or adjacent stations.
 */
public abstract class AbstractStation implements Serializable {

    /**
     * The location name of this station.
     */
    private final String name;

    private HashMap<String, AbstractStation> prevStations = new HashMap<>();

    private HashMap<String, AbstractStation> nextStations = new HashMap<>();

    ArrayList<Route> routes = new ArrayList<>();

    /**
     * The fare to enter/exit this station.
     */
    private AbstractFareCalculator fareCalculator;

    private transient int distanceFromSource;

    /**
     * Creates a station of specified name and fare calculating method.
     *
     * @param name           Specified location name
     * @param fareCalculator Specified fare calculating method.
     */
    public AbstractStation(String name, AbstractFareCalculator fareCalculator) {
        this.name = name;
        this.fareCalculator = fareCalculator;
    }

    public abstract String getType();

    /**
     * Returns the name of this station.
     *
     * @return The nameo f this station.
     */
    public String getName() {
        return name;
    }

    /**
     * Registers the station after this station on the same route.
     *
     * @param next The next station.
     */
    void append(AbstractStation next, Route route) {

        if (!this.routes.contains(route)) {
            this.routes.add(route);
        }
        if (route.getTail() == null) {
            setIniR(route);
        }
        if (route.getTail().equals(this)) {//adding to the tail of a route
            if (next != null) {
                next.nextStations.remove(route.getName());
                next.nextStations.put(route.getName(), next);
                route.setTail(next);
            }
        }
        if (this.nextStations.containsKey(route.getName())) {
            if (!(this.getNext(route).equals(this))) {//double check is not tail
                if (this.nextStations.get(route.getName()).equals(next)) {
                    //next is already the proceeding station
                    return;
                }
            }

            this.nextStations.remove(route.getName());
        }
        this.nextStations.put(route.getName(), next);
        if (next != null) {
            if (!next.prevStations.containsKey(route.getName())) {
                next.setPrevious(this, route);
            }
            if (!next.getPrev(route).equals(this)) {
                next.setPrevious(this, route);
            }
            if (!next.routes.contains(route)) {
                next.routes.add(route);
            }
        }

    }


    void setPrevious(AbstractStation station, Route route) {
        if (!this.routes.contains(route)) {
            this.routes.add(route);
        }
        if (route.getHead() == null) {
            setIniR(route);
        }
        if (route.getHead().equals(this)) {//is head of route
            if (station != null) {
                station.prevStations.remove(route.getName());
                route.setHead(station);
            }
        }
        if (this.prevStations.containsKey(route.getName())) {
            if (!(this.prevStations.get(route.getName()).equals(this))) {//double check is not head
                if (this.prevStations.get(route.getName()).equals(station)) {
                    //already previous station
                    return;
                }
            }
            this.prevStations.remove(route.getName());
        }
        this.prevStations.put(route.getName(), station);

        if (station != null) {
            if (!station.nextStations.containsKey(route) | !station.getNext(route).equals(this)) {
                station.append(this, route);
            }
            if (!station.routes.contains(route)) {
                station.routes.add(route);
            }
        }

    }




    /**
     * Returns the station prior to this station on the same route.
     *
     * @return The previous station.
     */
    public AbstractStation getPrev(Route route) {
        return this.prevStations.get(route.getName());
    }

    /**
     * Returns the station after this station on the same route.
     *
     * @return The next station.
     */
    public AbstractStation getNext(Route route) {
        return this.nextStations.get(route.getName());
    }

    /**
     * Compares only the location name of two stations.
     *
     * @param otherStation The station to be compared.
     * @return True if both stations have the same location name.
     */
    public boolean equals(AbstractStation otherStation) {
        return name.equals(otherStation.getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AbstractStation) {
            return equals((AbstractStation) other);
        }
        return false;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setIniR(Route route) {
        if(!this.routes.contains(route)){
        this.routes.add(route);}
        this.nextStations.put(route.getName(), this);
        this.prevStations.put(route.getName(), this);
        route.setHead(this);
        route.setTail(this);
    }

    /**
     * Compares the vehicle type of two stations.
     *
     * @param otherStation The station to be compared.
     * @return True if both stations is of the same vehicle type.
     */
    public abstract boolean isSameVehicle(AbstractStation otherStation);

    public boolean hasNext(Route route) {
        return (this.nextStations.containsKey(route.getName()) && (!this.nextStations.get(route.getName()).equals(this)));
    }

    public int dijkstraDistance(AbstractStation destination) {
        distanceFromSource = 0;
        HashSet<AbstractStation> visited = new HashSet<>();
        Comparator<AbstractStation> distanceComparator =
                (AbstractStation a1, AbstractStation a2) ->
                        Integer.compare(a1.distanceFromSource, a2.distanceFromSource);
        Queue<AbstractStation> queue = new PriorityQueue<>(1, distanceComparator);
        queue.add(this);
        AbstractStation vertex;
        int newDistance;

        while (!queue.isEmpty()) {
            vertex = queue.poll();
            visited.add(vertex);
            for (AbstractStation neighbour : vertex.getNeighbours()) {
                if (visited.contains(neighbour)) {
                    continue;
                }

                newDistance = vertex.distanceFromSource + 1;

                if (!queue.contains(neighbour)) neighbour.distanceFromSource = Integer.MAX_VALUE;
                if (newDistance < neighbour.distanceFromSource) neighbour.distanceFromSource = newDistance;

                if (neighbour.equals(destination)) {
                    return neighbour.distanceFromSource;
                }

                if (!queue.contains(neighbour)) queue.add(neighbour);
            }
        }

        return 0;
    }

    public void removeFromRoute(Route route) throws Exception {
        if (!routes.contains(route)) {
            throw new NoSuchFieldException("route not found!");
        }
        AbstractStation prevS;
        AbstractStation nextS;
        if (route.getHead() != null && this.equals(route.getHead())) {//is first station of route
            if (this.nextStations.containsKey(route.getName()) && (!this.nextStations.get(route.getName()).equals(this))) {
                //is not unique station in route
                route.removeHead();
                nextS = this.nextStations.get(route.getName());
            } else if (this.equals(route.getTail())) {//is both head and tail -- is the only station in route
                route.deleteOnlyStation();

            }
        }
        if (route.getTail() != null && this.equals(route.getTail())) {//is last station, but not the only station in the route
            //which means it definitely has a previous station
            prevS = this.prevStations.get(route.getName());
            route.removeTail();
            prevS.setPrevious(prevS, route);
        }

        //station is middle station in route
        prevS = this.prevStations.get(route.getName());
        nextS = this.nextStations.get(route.getName());
        if (prevS != null) {
            if (!prevS.equals(this)) {
                if (nextS != null) {
                    if (!nextS.equals(this)) {
                        prevS.append(nextS, route);
                    }
                }
                //station is last station in route
                prevS.append(prevS, route);
            } else if (prevS.equals(this)) {
                if (nextS != null) {
                    if (!nextS.equals(this)) {
                        nextS.setPrevious(nextS, route);
                    }
                }
            }
        }
        this.nextStations.remove(route.getName());
        this.prevStations.remove(route.getName());
        this.routes.remove(route);

    }

    public int getDistance(AbstractStation destination) {
        if (!isSameVehicle(destination)) {
            return -1;
        } else if (equals(destination)) {
            return 0;
        }
        return dijkstraDistance(destination);
    }

    /**
     * Returns the fare calculator of this station.
     *
     * @return The fare calculator.
     */
    public AbstractFareCalculator getFareCalculator() {
        return fareCalculator;
    }


    public HashSet<AbstractStation> getNeighbours() {
        HashSet<AbstractStation> neighbours = new HashSet<>();
        for (Route route : this.routes) {
            neighbours.add(prevStations.get(route.getName()));
            neighbours.add(nextStations.get(route.getName()));
        }
        //neighbours.addAll(intersections);
        neighbours.remove(null);
        return neighbours;
    }


    @Override
    public String toString() {
        return name + ": " + distanceFromSource;
    }

    public boolean hasRoute(Route route) {
        return this.routes.contains(route);
    }

}
