package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;
import utils.user;

public class loginWindow extends JDialog{
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    public int userChoice = 0;
    public static String csvFilePath = "userData/data.csv";
    public static user currentUser;

    public loginWindow() {
        super((JDialog)null);
        setModal(true);
        setResizable(false);
        setTitle("LOGIN");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 190);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        usernameText = new JTextField();
        usernameText.setBounds(100, 20, 310, 25);
        getContentPane().add(usernameText);
        usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 20, 80, 25);
        getContentPane().add(usernameLabel);

        passwordText = new JPasswordField();
        passwordText.setBounds(100, 60, 310, 25);
        getContentPane().add(passwordText);
        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10, 60, 80, 25);
        getContentPane().add(passwordLabel);

        JButton loginButton = new JButton("Sign-in");
        loginButton.setBounds(10, 100,120, 25);
        getContentPane().add(loginButton);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userUsername = usernameText.getText();
                char[] password = passwordText.getPassword();
                String passwordString = new String(password);

                if (isCorrectPassword(userUsername, passwordString).equals("Password Mismatched")) {
                    JOptionPane.showMessageDialog(loginWindow.this,
                            "Incorrect password!", "Login error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (isCorrectPassword(userUsername, passwordString).equals("User Not Found")){
                    JOptionPane.showMessageDialog(loginWindow.this,
                            "User not found!", "Login error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    currentUser = new user(usernameText, passwordText);
                    try {
                        openMainWindow();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        JButton registerButton = new JButton("Sign-up");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                openRegisterWindow();
            }
        });
        registerButton.setBounds(150, 100,120, 25);
        getContentPane().add(registerButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(ABORT);
            }
        });
        exitButton.setBounds(290, 100,120, 25);
        getContentPane().add(exitButton);
    }

    private void openRegisterWindow() {
        registerWindow register = new registerWindow();
        register.showWindow();
        this.dispose();
    }

    public String isCorrectPassword(String username, String userPassword) {
        String password = "";
        String output = "";
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
                    password = values[1];
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Objects.equals(userPassword, password)){
            output = "Password Matched";
        } else if (!Objects.equals(userPassword, password) || password.equals("")) {
            output = "Password Mismatched";
        }
        return output;
    }

    public void showWindow(){
        this.setVisible(true);
    }
    public void openMainWindow() throws SQLException {
        this.dispose();
        mainWindow main = new mainWindow(currentUser);
        main.showWindow();
    }

    public void getAvailableDatabases() throws SQLException {
        String userName = currentUser.getUsername();
        String userPassword = currentUser.getPassword();
        String url = "jdbc:postgresql://127.0.0.1:5432/";

        Connection connection = DriverManager.getConnection(url, userName, userPassword);

        DatabaseMetaData metaData = connection.getMetaData();
        //Retrieving the list of database names
        ResultSet tables = metaData.getCatalogs();
        while (tables.next()) {
            System.out.println(tables.getString("TABLE_CAT"));
        }
    }
}
