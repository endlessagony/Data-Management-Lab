package gui;

import utils.databaseAccess;
import utils.user;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class editWindow extends JDialog {
    int id;
    public String userName;
    public String userPassword;
    public String currentDatabaseName;
    public ResultSet updatedData;
    boolean isOpendOnce = false;
    public user static_user;
    public databaseAccess current_access;
    public editWindow(ArrayList<String> columns, String databaseName, user current_user, databaseAccess access) {
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

        static_user = current_user;
        userName = current_user.getUsername();
        userPassword = current_user.getPassword();
        currentDatabaseName = databaseName;
        current_access = access;

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
        isOpendOnce = true;

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 20+addition,120, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> data = getData();
                int currentId = Integer.parseInt(data.get(0));
                try {
                    access.editData(data, currentId);
                    updatedData = access.getData();
                } catch (SQLException | IOException ex) {
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
        exitButton.setBounds(150, 20+addition,120, 25);
        getContentPane().add(exitButton);

    }

    public void showWindow() throws SQLException, IOException {
        idRequire idRequest = new idRequire();
        idRequest.showWindow();
        if (!idRequest.isDisplayable()) {
            id = idRequest.id;

            // TO-DO: get values by id and paste it in jtextfield
            ResultSet data = current_access.getDataById(id);
            ArrayList<String> values = new ArrayList<>();

            if (data.next()) {
                ResultSetMetaData metaData = data.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    values.add(data.getString(i));
                }
            }

            int counter = 0;
            Component[] components = getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JTextField) {
                    ((JTextField) component).setText(values.get(counter));
                    counter++;
                }
            }

            values.clear();
            this.setVisible(true);
        }
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
