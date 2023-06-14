package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class idRequire extends JDialog{
    public int id;
    public idRequire(){
        super((JDialog) null);
        setModal(true);
        setResizable(false);
        setTitle("DROP");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 300, 150);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        JLabel featureName = new JLabel("Id");
        featureName.setBounds(10, 20, 80, 25);
        getContentPane().add(featureName);
        JTextField fieldValueText = new JTextField();
        fieldValueText.setBounds(60, 20, 210, 25);
        getContentPane().add(fieldValueText);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 60,120, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = Integer.parseInt(fieldValueText.getText());
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

    public void showWindow(){
        this.setVisible(true);
    }

    public int getId(){
        return id;
    }

}
