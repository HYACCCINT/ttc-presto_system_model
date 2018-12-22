package backend.serializer;

import backend.transitsystem.StationManager;

import java.io.File;

/**
 * Saves state of a StationSerializer by writing it to file.
 */
public class StationSerializer extends AbstractSerializer {

    /**
     * Initializes a StationSerializer.
     */
    public StationSerializer() {
        super("data" + File.separator + "stations" + File.separator + "stations.ser");
    }

    /**
     * Saves state of the specified StationManager.
     * @param routesManager The specified StationManager to be saved.
     */
    public void saveState(StationManager routesManager) {
        writeObject(routesManager);
    }
}
