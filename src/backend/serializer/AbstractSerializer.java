package backend.serializer;

import backend.audit.Auditor;
import backend.transitsystem.AbstractStation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Saves object states by writing it into a file.
 */
public abstract class AbstractSerializer {

    /**
     * File path to the serialized file.
     */
    private String filePath;

    /**
     * Initializes a serializer with a specified file path.
     * @param filePath The specified file path to store the serialized file.
     */
    AbstractSerializer(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns file path to the serialized file.
     * @return The file path.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Writes object to file and saves its states.
     * @param object The object to be written to file.
     */
    void writeObject(Object object) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(object);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
