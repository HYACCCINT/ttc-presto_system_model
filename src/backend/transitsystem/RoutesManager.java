package backend.transitsystem;

import backend.Exceptions.InvalidInputException;
import backend.Exceptions.RouteAlreadyExistsException;
import frontend.popupwindow.DialogueBox;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Manages all routes in the transit system.
 */
public class RoutesManager implements Serializable {

    /**
     * All routes in the transit system.
     */
    private ArrayList<Route> allRoutes = new ArrayList<>();


    /**
     * Creates a RouteManager given a list of routes.
     */
    public RoutesManager() {
    }

    /**
     * returns if a route already
     * @param name
     * @param type
     * @return
     */
    boolean hasRoute(String name, String type) {
        for (Route route : allRoutes) {
            if (route.getName().equals(name) && route.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    public Route addRoute(String name, AbstractStation head, String type) throws InvalidInputException, RouteAlreadyExistsException {
        switch (type) {
            case "bus":
                if (head instanceof BusStop) {
                    if (hasRoute(name, type)) {
                        throw new RouteAlreadyExistsException("route already exists");
                    }
                    try {
                        Route<BusStop> route = new Route<>(name, (BusStop) head);
                        allRoutes.add(route);
                        head.setIniR(route);
                        return route;
                    } catch (InvalidInputException e) {
                        DialogueBox d = new DialogueBox("Error", "Attempted to created invalid route,please try again");
                        d.display();
                    }
                }
                break;
            case "train":
                if (head instanceof TrainStation) {
                    if (hasRoute(name, type)) {
                        throw new RouteAlreadyExistsException("route already exists");
                    }
                    try {
                        Route<TrainStation> route = new Route<>(name, (TrainStation) head);
                        allRoutes.add(route);
                        head.setIniR(route);
                        return route;
                    } catch (InvalidInputException e) {
                        DialogueBox d = new DialogueBox("Error", "Attempted to created invalid route,please try again");
                        d.display();
                    }
                }
        }
        throw new InvalidInputException("head is not the same as described");

    }

    /**
     * Initializes a new route to the system
     * @param name name of route
     * @param type type of route
     * @return the newly created route
     */
    public Route addRoute(String name, String type) {
        switch (type) {
            case "bus":
                if (hasRoute(name, type)) {
                    try {
                        throw new RouteAlreadyExistsException("");
                    } catch (RouteAlreadyExistsException e) {
                        DialogueBox d = new DialogueBox("Error", " Route Already Exists!");
                        d.display();
                    }
                }
                Route<BusStop> route = new Route<>(name, type);
                allRoutes.add(route);
                return route;
            case "train":
                if (hasRoute(name, type)) {
                    try {
                        throw new RouteAlreadyExistsException("");
                    } catch (RouteAlreadyExistsException e) {
                        DialogueBox d = new DialogueBox("Error", " Route Already Exists!");
                        d.display();
                    }
                }
                Route<TrainStation> troute = new Route<>(name, type);
                allRoutes.add(troute);
                return troute;
        }
        DialogueBox d = new DialogueBox("Error", " Invalid Station Type");
        d.display();
        return null;


    }


    /**
     * Adds a station to the end of this route.
     *
     * @param station
     */
    public void appendStop(AbstractStation station, Route route) {
        if (station == null) {
            return;
        }
        if ((station instanceof BusStop && route.getType().equals("bus")) | (station instanceof TrainStation && route.getType().equals("train"))) {
            if (route.getTail() == null | route.getHead() == null) {
                route.setHead(station);
                route.setTail(station);
            }
            route.getTail().append(station, route);
            route.setTail(station);
        }
    }


    /**
     * Returns the route of the given name.
     *
     * @param routeName The specified name.
     * @return The route, if it exists.
     */
    public Route getRoute(String routeName) {
        for (Route route : allRoutes) {
            if (route.getName().equals(routeName)) {
                return route;
            }
        }
        return null;
    }


    /**
     * Getter for route list
     * @return list of all routes
     */
    public ArrayList<Route> getAllRoutes() {
        return allRoutes;
    }


}