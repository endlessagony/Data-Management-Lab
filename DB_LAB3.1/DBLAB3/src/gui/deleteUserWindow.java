package gui;

import utils.databaseAccess;
import utils.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class deleteUserWindow extends JDialog{
    private JLabel usernameLabel;
    private JLabel password1Label;
    private JPasswordField password1Text;
    public static String csvFilePath = "userData/data.csv";
    private JLabel password2Label;
    private JPasswordField password2Text;
    private JLabel userPermissionLabel;
    private user staticUser;
    private ArrayList<String> usersName = new ArrayList<>();
    public String userName;
    public String userPassword;
    public String currentDatabaseName;

    public deleteUserWindow(String databaseName, user current_user, databaseAccess access) throws SQLException {
        super((JDialog) null);
        setModal(true);
        setResizable(false);
        setTitle("DROP");
        setAlwaysOnTop(true);

        userName = current_user.getUsername();
        userPassword = current_user.getPassword();
        staticUser = current_user;
        currentDatabaseName = databaseName;
        getRegisteredUsers();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 150);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JComboBox<String> usernameText = new JComboBox<>();
        usernameText.setBounds(100, 20, 310, 25);
        for (String username: usersName){
            usernameText.addItem(username);
        }
        getContentPane().add(usernameText);
        usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 20, 80, 25);
        getContentPane().add(usernameLabel);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 60,120, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userUsername = (String) usernameText.getSelectedItem();
                try {
                    access.deleteUser(userUsername);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
            }
        });
        getContentPane().add(submitButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        exitButton.setBounds(150, 60,120, 25);
        getContentPane().add(exitButton);
    }

    public void getRegisteredUsers() throws SQLException {
        databaseAccess access = new databaseAccess(userName, userPassword, currentDatabaseName, "open",
                deleteUserWindow.this, staticUser);
        usersName = access.getUsers();
    }

    public void showWindow(){
        this.setVisible(true);
    }
}
