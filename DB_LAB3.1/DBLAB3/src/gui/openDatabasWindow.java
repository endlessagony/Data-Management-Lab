package gui;

import java.io.IOException;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import utils.user;
import utils.databaseAccess;

public class openDatabasWindow extends JDialog {
    private JLabel databaseNameLabel;
    private JTextField databaseNameText;
    public databaseAccess access;
    public List<String> availableTables;
    public String mode = "open";
    String databaseName = "";
    public openDatabasWindow(mainWindow mainWindow, user current_user) {
        super((JDialog)null);
        setModal(true);
        setResizable(false);
        setTitle("LOGIN");
        setAlwaysOnTop(true);

        String userName = current_user.getUsername();
        String userPassword = current_user.getPassword();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 150);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        databaseNameText = new JTextField();
        databaseNameText.setBounds(130, 20, 290, 25);
        getContentPane().add(databaseNameText);
        databaseNameLabel = new JLabel("Database name");
        databaseNameLabel.setBounds(10, 20, 120, 25);
        getContentPane().add(databaseNameLabel);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                databaseName = databaseNameText.getText();
                if (databaseName.length()  == 0){
                    JOptionPane.showMessageDialog(openDatabasWindow.this,
                            "Input database name!", "Open error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    mainWindow.databaseName = databaseName;
                    access = new databaseAccess(userName, userPassword, databaseName,
                            mode, openDatabasWindow.this, current_user);
                    dispose();
                }
            }

        });
        submitButton.setBounds(10, 60,120, 25);
        getContentPane().add(submitButton);
    }

    public ResultSet getData() throws SQLException, IOException {
        return access.getData();
    }

    public void showWindow(){
        this.setVisible(true);
    }
}
