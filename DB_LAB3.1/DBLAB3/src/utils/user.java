package utils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class user {
    public static String username;
    public static String password;
    public static String permission;
    public String csvFilePath = "userData/data.csv";

    public user(JTextField inputUsername, JPasswordField inputPassword) {
        username = inputUsername.getText();
        password = new String(inputPassword.getPassword());
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Read the header row
            String headerRow = br.readLine();
            String[] headers = headerRow.split(",");

            // Find the row with the given username
            String dataRow;
            while ((dataRow = br.readLine()) != null) {
                String[] values = dataRow.split(",");
                if (values[0].equals(username)) {
                    // Extract the password field
                    permission = values[2];
                    break;
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(permission);
    }

    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }

    public static String getPermission(){
        return permission;
    }
}
