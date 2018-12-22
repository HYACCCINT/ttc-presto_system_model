package backend.serializer;

import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;

import java.io.File;

/**
 * Saves states of RouteManager by writing it to a file.
 */
public class RouteSerializer extends AbstractSerializer {

    /**
     * Initializes a RouteSerializer
     */
    public RouteSerializer() {
        super("data" + File.separator + "routes" + File.separator + "routes.ser");
    }

    /**
     * Saves state of a specified RoutesManager.
     * @param routesManager The RoutesManager to be saved.
     */
    public void saveState(RoutesManager routesManager) {
        writeObject(routesManager);
    }

    public static void main(String[] args) {
        try {
            StationManager stationManager = new StationManager();
            RoutesManager routesManager = new RoutesManager();

            new StationSerializer().saveState(stationManager);
            new RouteSerializer().saveState(routesManager);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
