package gui;

import utils.databaseAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import utils.user;

public class databaseCreationWindow extends JDialog{

    private JLabel databaseNameLabel;
    private JTextField databaseNameText;
    private JLabel tableNameLabel;
    private JTextField tableNameText;
    private JLabel numberOfFeaturesLabel;
    private String mode = "create";
    public databaseCreationWindow(user current_user) {
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
        databaseNameLabel.setBounds(10, 20, 150, 25);
        getContentPane().add(databaseNameLabel);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String databaseName = databaseNameText.getText();
                if (databaseName.length()  == 0){
                    JOptionPane.showMessageDialog(databaseCreationWindow.this,
                            "Input database name!", "Open error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    databaseAccess access = new databaseAccess(userName, userPassword,
                            databaseName, mode, databaseCreationWindow.this, current_user);
                    dispose();
                }
            }

        });
        submitButton.setBounds(10, 60,120, 25);
        getContentPane().add(submitButton);
    }

    public void showWindow(){
        this.setVisible(true);
    }
}
