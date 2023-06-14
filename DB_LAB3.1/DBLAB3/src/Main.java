import gui.loginWindow;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static String csvFilePath;

    public static void main(String[] args) {
        csvFilePath = "userData/data.csv";
        createCSV();
        loginWindow gui = new loginWindow();
        gui.showWindow();
    }

    public static void createCSV() {
        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            String[] headers = {"username", "password", "permission"};
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
                bw.write(String.join(",", headers) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
