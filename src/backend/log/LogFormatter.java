package backend.log;


import java.util.logging.LogRecord;

/**
 * Custom formatter for log messages.
 */
class LogFormatter extends java.util.logging.Formatter {

    /**
     * Format LogRecord into a easily readable string.
     * @param record The specified LogRecord.
     * @return Returns a user friendly log message.
     */
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
}
