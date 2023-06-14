package gui;

import utils.databaseAccess;
import utils.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class createUserWindow extends JDialog {
    private JLabel usernameLabel;
    private JLabel password1Label;
    private JPasswordField password1Text;
    public static String csvFilePath = "userData/data.csv";
    private JLabel password2Label;
    private JPasswordField password2Text;
    private JLabel userPermissionLabel;
    private user staticUser;
    private ArrayList<String> usersName = new ArrayList<>();
    private ArrayList<String> usersPassword = new ArrayList<>();

    public createUserWindow(user current_user, String databaseName, databaseAccess access){
        super((JDialog)null);
        setModal(true);
        setResizable(false);
        setTitle("CREATE USER");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 210);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        staticUser = current_user;
        getRegisteredUsers();

        JComboBox<String> usernameText = new JComboBox<>();
        usernameText.setBounds(100, 20, 310, 25);
        for (String username: usersName){
            usernameText.addItem(username);
        }
        getContentPane().add(usernameText);
        usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(10, 20, 80, 25);
        getContentPane().add(usernameLabel);


        password1Text = new JPasswordField();
        password1Text.setBounds(100, 60, 310, 25);
        getContentPane().add(password1Text);
        password1Label = new JLabel("Password");
        password1Label.setBounds(10, 60, 80, 25);
        getContentPane().add(password1Label);

        usernameText.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedValue = (String) e.getItem();
                password1Text.setEnabled(false);
                password1Text.setText(usersPassword.get(usersName.indexOf(selectedValue)));
            }
        });
        if (usernameText.getItemCount() == 1) {
            String selectedValue = (String) usernameText.getSelectedItem();
            password1Text.setEnabled(false);
            password1Text.setText(usersPassword.get(usersName.indexOf(selectedValue)));
        }

        userPermissionLabel = new JLabel("Permission");
        userPermissionLabel.setBounds(10, 100, 80, 25);
        getContentPane().add(userPermissionLabel);
        JComboBox<String> permissionComboBox = new JComboBox<>();
        permissionComboBox.addItem("guest");
        permissionComboBox.addItem("admin");
        permissionComboBox.setBounds(100, 100, 310, 25);
        getContentPane().add(permissionComboBox);

        JButton registerButton = new JButton("Submit");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userUsername = (String) usernameText.getSelectedItem();
                char[] password1 = password1Text.getPassword();
                String password1String = new String(password1);
                String userPermission = (String) permissionComboBox.getSelectedItem();
                updatePermission(userPermission, userUsername);

//                databaseAccess access = new databaseAccess(staticUser.getUsername(), staticUser.getPassword(),
//                        databaseName, "open", createUserWindow.this, staticUser);
                try {
                    access.createUserWithPermission(userUsername, password1String, databaseName, userPermission);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
            }
        });
        registerButton.setBounds(10, 140,120, 25);
        getContentPane().add(registerButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               dispose();
            }
        });
        exitButton.setBounds(150, 140,120, 25);
        getContentPane().add(exitButton);
    }

    public void showWindow(){
        this.setVisible(true);
    }

    public void updatePermission(String permission, String userName){
        try {
            // Read the CSV file into a list of string arrays
            List<String[]> data = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                data.add(fields);
            }
            br.close();

            // Find the row with the specified username and modify the permission value
            for (String[] row : data) {
                if (row[0].equals(userName)) {
                    row[2] = permission;
                    break;
                }
            }

            // Write the modified data back to the CSV file
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath));
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void getRegisteredUsers(){
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            // Read the header row
            String headerRow = br.readLine();
            String adminLine = br.readLine();

            // Find the row with the given username
            String dataRow;
            while ((dataRow = br.readLine()) != null) {
                String[] values = dataRow.split(",");
                usersName.add(values[0]);
                usersPassword.add(values[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
