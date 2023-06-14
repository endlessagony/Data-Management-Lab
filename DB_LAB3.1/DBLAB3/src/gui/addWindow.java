package gui;

import utils.databaseAccess;
import utils.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class addWindow extends JDialog {
    public String userName;
    public String userPassword;
    public String currentDatabaseName;
    public ResultSet updatedData;
    public ArrayList<String> _columnDTypes;
    public addWindow(ArrayList<String> columns, String databaseName,
                     user current_user, databaseAccess access) {
        super((JDialog)null);
        setModal(true);
        setResizable(false);
        setTitle("LOGIN");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, (columns.size()+3)*40);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        userName = current_user.getUsername();
        userPassword = current_user.getPassword();
        currentDatabaseName = databaseName;

        int addition = 0;
        for (String columnName: columns){
            JLabel featureLabel = new JLabel(columnName);
            featureLabel.setBounds(10, 20+addition, 80, 25);
            getContentPane().add(featureLabel);

            JTextField featureValue = new JTextField();
            featureValue.setBounds(100, 20+addition, 310, 25);
            getContentPane().add(featureValue);
            addition += 40;
        }

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 20+addition,120, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> data = getData();
//                databaseAccess access = new databaseAccess(userName, userPassword,
//                        currentDatabaseName, "open", addWindow.this, current_user);
                try {
                    access.addData(data);
                    updatedData = access.getData();
                } catch (SQLException | IOException ex) {
                    JOptionPane.showMessageDialog(addWindow.this,
                            ex, "Error",
                            JOptionPane.ERROR_MESSAGE);
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
        exitButton.setBounds(150, 20+addition,120, 25);
        getContentPane().add(exitButton);

    }

    public void showWindow() throws SQLException, IOException {
        this.setVisible(true);
    }

    public ArrayList<String> getData(){
        ArrayList<String> data = new ArrayList<>();
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JTextField) {
                data.add(((JTextField) component).getText());
            }
        }

        return data;
    }
}
