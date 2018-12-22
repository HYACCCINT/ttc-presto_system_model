package backend.initializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Initializes objects by reading from serialized files.
 */
public abstract class AbstractInitializer {

    /**
     * Reads from the configuration file and returns the saved AccountManager, if it exists.
     *
     * @param filePath The path to the configuration file.
     * @return The saved AccountManager, or a new AccountManager if no configuration file exists.
     */
     Object deserialize(String filePath) {
        File serializedFile = new File(filePath);
        if (!serializedFile.exists()) return null;
        try {
            FileInputStream fileInputStream = new FileInputStream(serializedFile);
            ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
