package backend.initializer;

import backend.serializer.StationSerializer;
import backend.transitsystem.StationManager;

/**
 * Initializes StationManager by reading from serialized file.
 */
public class StationsInitializer extends AbstractInitializer {


    /**
     * Initializes the AccountManager and numCards when the system starts.
     *
     * @param stationsFilePath File path to AccountManager configuration file.
     * @return
     */
    public StationManager initialize(String stationsFilePath) {
        StationManager stationManager = (StationManager) deserialize(new StationSerializer().getFilePath());
        if (stationManager == null) {
            stationManager = new StationManager();
        }
        return stationManager;
    }
}
