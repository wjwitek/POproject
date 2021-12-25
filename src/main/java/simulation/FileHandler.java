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
        System.out.println(filename);
        ArrayList<String[]> data = dataTracking.generateReport();
        System.out.println(1);
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            data.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        }
        System.out.println(2);
    }

    /* Convert data about a day into CSV format. */
    public String convertToCSV(String[] data) {
        return String.join(",", data);
    }
}
