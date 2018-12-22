package backend.transitsystem;

import backend.Exceptions.StationAreadyExistsException;
import frontend.popupwindow.DialogueBox;

import java.io.Serializable;
import java.util.ArrayList;

public class StationManager implements Serializable {
    ArrayList<AbstractStation> masterList = new ArrayList<>();
    ArrayList<BusStop> busStops = new ArrayList<>();
    ArrayList<TrainStation> trainStations = new ArrayList<>();


    public StationManager() {
    }


    /**
     * Instantiates a new station and adds it to routr
     * @param route to add to
     * @param name the new name of the staion
     * @param station the last station of the route
     * @return the new station
     * @throws StationAreadyExistsException when a station already exists
     */
    public AbstractStation newStationToRoute(Route route, String name, AbstractStation station) throws StationAreadyExistsException, StationNotFoundException{
        System.out.println(route.getType());
        if (route.getType().equals("bus")) {
            BusStop stop;
            if (stationExists(name, "Bus")) {
                stop = getBusStop(name);
                if (stop.hasRoute(route)) {
                    throw new StationAreadyExistsException("station is already in route!");
                }
            } else {
                stop = (BusStop) newStationAlone(name, "bus");
            }

            stop.setPrevious(station, route);

            masterList.add(stop);
            busStops.add(stop);
            return stop;
        } else if (route.getType().equals("train")) {
            TrainStation trainStation;
            if (stationExists(name, "Train")) {
                trainStation = getTrainStation(name);
                if (trainStation.hasRoute(route)) {
                    throw new StationNotFoundException("station is already in route!");
                }
            } else {
                trainStation = (TrainStation) newStationAlone(name, "train");
            }
            trainStation.setPrevious(station, route);

            if (!masterList.contains(trainStation)) {
                masterList.add(trainStation);
            }
            if (trainStations.contains(trainStation)) {
                trainStations.add(trainStation);
            }
            return trainStation;
        } else {
            throw new IllegalArgumentException("invalid route type");
        }
    }

    /**
     * Creates a station independent of any route
     * @param name name of the station to be created
     * @param type type of station to be created
     * @return the created station
     */
    public AbstractStation newStationAlone(String name, String type) {

        if (type.equals("bus")) {
            for (BusStop bus : busStops) {
                try {
                    if (bus.getName().equals(name)) {
                        throw new StationAreadyExistsException("repetitive station");
                    }
                } catch (StationAreadyExistsException e) {
                    DialogueBox d = new DialogueBox("Error", " Station Already Exists!");
                    d.display();
                }
            }
            BusStop stop = new BusStop(name);
            masterList.add(stop);
            busStops.add(stop);
            return stop;
        } else if (type.equals("train")) {
            for (TrainStation trainStation : trainStations) {
                try {
                    if (trainStation.getName().equals(name)) {
                        throw new StationAreadyExistsException("repetitive station");
                    }
                } catch (StationAreadyExistsException e) {
                    DialogueBox d = new DialogueBox("Error", " Station Already Exists!");
                    d.display();
                }
            }
            TrainStation tstation = new TrainStation(name);
            masterList.add(tstation);
            trainStations.add(tstation);
            return tstation;
        } else {
            DialogueBox d = new DialogueBox("Error", " Invalid Input !");
            d.display();
        }
        return null;
    }

    /**
     * Deletes station with name from the route
     * @param route the route from which the station is to be deleted
     * @param name the name of the station to be deleted
     * @throws Exception exception
     */
    public void deleteStationFromRoute(Route route, String name) throws Exception {

        AbstractStation station = getStation(name);
        station.removeFromRoute(route);
    }


    /**
     * Getter for master list
     * @return the master list of this program
     */
    public ArrayList<AbstractStation> getMasterList() {
        return masterList;
    }


    /**
     * Getter for station
     * @param cmd name of station
     * @return station with name
     * @throws Exception
     */
     AbstractStation getStation(String cmd) throws Exception {
        for (AbstractStation station : masterList) {
            if (station.getName().equals(cmd)) {
                return station;
            }
        }
        throw new StationNotFoundException("station not found!");
    }

    /**
     * Removes a station
     * @param station The station to be removed
     * @throws Exception Thrown in case if station not found
     */
    public void deleteStation(AbstractStation station) throws Exception {
        ArrayList<Route> routes = station.getRoutes();

        for (Route r : routes) {
            deleteStationFromRoute(r, station.getName());
        }
        if (station instanceof BusStop) {
            busStops.remove(station);
        }
        if (station instanceof TrainStation) {
            trainStations.remove(station);
        }
        masterList.remove(station);
    }

    /**
     * Returns whether a given station exists.
     * @param station The target station
     * @return True if it exists.
     */
    public boolean stationExists(AbstractStation station) {
        return masterList.contains(station);
    }


    /**
     * Returns true if station exists.
     * @param stationname Name of the station
     * @param type Station type
     * @return True if the station exists.
     */
    public boolean stationExists(String stationname, String type) {
        for (AbstractStation station : masterList) {
            if (station.getName().equals(stationname) && station.getType().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;

    }

    /**
     * Returns a BusStop by its name.
     * @param name The specified name
     * @return The target train station.
     * @throws StationNotFoundException Thrown if no station is found.
     */
    public BusStop getBusStop(String name) throws StationNotFoundException {
        for (BusStop busStop : busStops) {
            if (busStop.getName().equals(name)) {
                return busStop;
            }
        }
        throw new StationNotFoundException();
    }

    /**
     * Returns a TrainStation by its name.
     * @param name The specified name
     * @return The target train station.
     * @throws StationNotFoundException Thrown if no station is found.
     */
    public TrainStation getTrainStation(String name) throws StationNotFoundException {
        for (TrainStation trainStation : trainStations) {
            if (trainStation.getName().equals(name)) {
                return trainStation;
            }
        }
        throw new StationNotFoundException();
    }
}
