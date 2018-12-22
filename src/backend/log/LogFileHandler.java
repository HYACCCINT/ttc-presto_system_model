package backend.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;

/**
 * Custom file handler for logger
 */
public class LogFileHandler extends FileHandler {

    /**
     * Creates a LogFileHandler in specified file (log.txt).
     * @throws IOException Thrown if log file cannot be created.
     */
    public LogFileHandler() throws IOException {
        super("log.txt", true);
        setFormatter(new LogFormatter());
    }

}