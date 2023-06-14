package gui;

import utils.databaseAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import utils.user;

public class findWindow extends JDialog {
    public List<Integer> ids = null;
    public findWindow(ArrayList<String> columns, String databaseName, user current_user, databaseAccess access) {
        super((JDialog) null);
        setModal(true);
        setResizable(false);
        setTitle("FIND");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 190);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        String userName = current_user.getUsername();
        String userPassword = current_user.getPassword();

        JLabel featureName = new JLabel("Field name");
        featureName.setBounds(10, 20, 80, 25);
        getContentPane().add(featureName);
        JComboBox<String> featuresComboBox = new JComboBox<>();
        for (String element : columns) {
            featuresComboBox.addItem(element);
        }
        featuresComboBox.setBounds(100, 20, 310, 25);
        getContentPane().add(featuresComboBox);

        JTextField fieldValueText = new JTextField();
        fieldValueText.setBounds(100, 60, 310, 25);
        getContentPane().add(fieldValueText);
        JLabel filedValueLabel = new JLabel("Field value");
        filedValueLabel.setBounds(10, 60, 80, 25);
        getContentPane().add(filedValueLabel);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        exitButton.setBounds(150, 100,120, 25);
        getContentPane().add(exitButton);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 100,120, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fieldValue = fieldValueText.getText();
                String fieldName = (String) featuresComboBox.getSelectedItem();
                if (fieldValue.length() == 0){
                    JOptionPane.showMessageDialog(findWindow.this,
                            "Fields must be filled!", "Find error",
                            JOptionPane.ERROR_MESSAGE);
                }
                try {
                    ids = access.getId(fieldName, fieldValue);
                    if (ids == null) {
                        JOptionPane.showMessageDialog(findWindow.this,
                                "Observations were not found!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                dispose();
            }
        });
        getContentPane().add(submitButton);
    }

    public List<Integer> getIds(){
        return ids;
    }
    public void showWindow(){
        this.setVisible(true);
    }
}
