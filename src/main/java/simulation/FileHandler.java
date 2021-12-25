package simulation;

import simulation.gui.DataTracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileHandler {
    private final DataTracking dataTracking;

    public FileHandler(DataTracking newDataTracking){
        dataTracking = newDataTracking;
    }

    /* Save data about map int csv file. */
    public void saveToFile(String filename) throws FileNotFoundException {
        File outputFile = new File(filename);
        ArrayList<String[]> data = dataTracking.generateReport();
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
    }

    /* Convert data about a day into CSV format. */
    public String convertToCSV(String[] data) {
        return String.join(",", data);
    }
}
