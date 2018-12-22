package backend.audit;

import backend.transitsystem.AbstractStation;
import backend.transitsystem.TrainStation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Writes daily report of profit and cost incurred by the transit system.
 */
public final class Auditor implements PropertyChangeListener {

    /**
     * Creates a new singleton Auditor.
     */
    private static final Auditor AUDITOR = new Auditor();

    /**
     * Returns this Auditor as a singleton.
     *
     * @return This Auditor.
     */
    public static Auditor getAuditor() {
        return AUDITOR;
    }

    /**
     * Total profit earned today.
     */
    private double profit;

    /**
     * Total number of stations passed by users today.
     */
    private int traffic;

    private HashMap<AbstractStation, Integer> trafficPerStation;

    /**
     * The current date.
     */
    private LocalDate currentDate;

    /**
     * Path to the backend.audit report text file.
     */
    private final String auditReportDirPath = "audit" + File.separator;

    /**
     * Constructs a new Auditor.
     */
    private Auditor() {
        profit = 0;
        traffic = 0;
        trafficPerStation = new HashMap<>();
    }

    /**
     * Resets the date of this Auditor and saves the audit report if the current day has passed.
     * @param newDate The new date to be updated for this Auditor.
     */
    public void reset(LocalDate newDate) {
        if (currentDate == null) {
            setDate(newDate);
        } else if (newDate.getDayOfYear() != (currentDate.getDayOfYear())) {
            writeAuditReport();
            profit = 0;
            traffic = 0;
            trafficPerStation = new HashMap<>();
            setDate(newDate);
        }
    }

    /**
     * Set the current day to the specified day.
     *
     * @param currentDate The specified day.
     */
    private void setDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    /**
     * Returns the total profit generated on this day.
     * @return The total profit.
     */
    public double getProfit() {
        return profit;
    }

    /**
     * Returns the total traffic generated on this day.
     * @return The total traffic.
     */
    public int getTraffic() {
        return traffic;
    }

    /**
     * Returns a list of all stations sorted by the traffic they generate in descending order.
     * @return The list of sorted stations.
     */
    private List<Map.Entry<AbstractStation, Integer>> sortStationsByTraffic() {
        List<Map.Entry<AbstractStation, Integer>> sortedList = new LinkedList<>(trafficPerStation.entrySet());
        sortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return sortedList;
    }

    /**
     * Returns a list of stations that generate the most traffic of specified length.
     * @param num The specified number of stations to be returned.
     * @return The sorted list of stations.
     */
    public List<Map.Entry<AbstractStation, Integer>> getMaxTrafficStations(int num) {
        List<Map.Entry<AbstractStation, Integer>> subList = new ArrayList<>();
        List<Map.Entry<AbstractStation, Integer>> sortedList = sortStationsByTraffic();
        int iterations = Math.min(num, sortedList.size());
        for (int i = 0; i < iterations; i++) {
            subList.add(sortedList.get(i));
        }
        return subList;
    }

    /**
     * Returns the current date of this Auditor.
     * @return The current date.
     */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /**
     * Increments total profit earned.
     *
     * @param profit Profit earned from a backend.trip.
     */
    private void addProfit(double profit) {
        this.profit += profit;
    }

    /**
     * Increments total traffic generated.
     *
     * @param traffic Number of stations passed in one backend.trip.
     */
    private void addTraffic(AbstractStation station, int traffic) {
        this.traffic += traffic;
        if (trafficPerStation.containsKey(station)) {
            int oldTraffic = trafficPerStation.get(station);
            trafficPerStation.put(station, oldTraffic + 1);
        } else {
            trafficPerStation.put(station, 1);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("balance")) {
            addProfit((Double) evt.getNewValue());
        } else if (evt.getPropertyName().equals("travelled")) {
            addTraffic((AbstractStation) evt.getOldValue(), (Integer) evt.getNewValue());
        }
    }

    /**
     * Returns the average profit by traffic generated.
     *
     * @return The average profit.
     */
    private double getProfitPerStation() {
        if (traffic > 0) {
            double average = profit / traffic;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            return Double.valueOf(decimalFormat.format(average));
        }
        return 0;
    }


    /**
     * Returns a summary of profit and cost generated today.
     *
     * @return The daily summary.
     */
    private String generateDailySummary() {
        StringBuilder dailySummary = new StringBuilder(currentDate.toString() + " - Audit Summary:" + System.lineSeparator());
        dailySummary.append("- Total profit earned: $").append(profit).append(System.lineSeparator());
        dailySummary.append("- Total traffic generated: ").append(traffic).append(" (stations passed)").append(System.lineSeparator());
        dailySummary.append("- Average profit earned per station: $").append(getProfitPerStation()).append(System.lineSeparator());
        dailySummary.append("- Stations with most people flow: ").append(System.lineSeparator());
        int stationsNum = 1;
        for (Map.Entry<AbstractStation, Integer> entry : getMaxTrafficStations(3)) {
            dailySummary.append("\t").append(stationsNum).append(". ").append(entry.getKey().toString()).append(": ")
                    .append(entry.getValue()).append(" (entries/exits)").append(System.lineSeparator());
            stationsNum++;
        }
        dailySummary.append(System.lineSeparator());
        return dailySummary.toString();
    }

    /**
     * Generates the file name that stores the audit report of this current day.
     * @return The file name of the audit report.
     */
    private String generateFileName() {
        return "audit_" + currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
    }

    /**
     * Generates the file path to the audit report of this current day.
     * @param date The day of the audit report.
     * @return The file path to the stored audit report.
     */
    public String generateFilePath(String date) {
        return auditReportDirPath + "audit_" + date + ".txt";
    }

    /**
     * Writes a daily summary to the backend.audit report file at the end of every day.
     */
    public void writeAuditReport() {
        if (traffic == 0) return;
        try {
            String auditReportFilePath = auditReportDirPath + generateFileName();
            File reportFile = new File(auditReportFilePath);
            BufferedWriter bufferedWriter;
            if (!reportFile.exists()) {
                reportFile.createNewFile();
                bufferedWriter = new BufferedWriter(new FileWriter(new File(auditReportFilePath)));

            } else {
                bufferedWriter = new BufferedWriter(new FileWriter(new File(auditReportFilePath), true));
            }
            bufferedWriter.append(generateDailySummary());
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
