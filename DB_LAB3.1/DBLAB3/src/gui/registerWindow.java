package gui;

import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class registerWindow extends JDialog{
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel password1Label;
    private JPasswordField password1Text;
    private JLabel password2Label;
    private JPasswordField password2Text;
    private JLabel userPermissionLabel;
    public int userChoice = 0;
    public static String csvFilePath = "userData/data.csv";

    public registerWindow() {
        super((JDialog)null);
        setModal(true);
        setResizable(false);
        setTitle("LOGIN");
        setAlwaysOnTop(true);

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        setBounds(400, 300, 450, 250);
        setLocation(x, y);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        usernameText = new JTextField();
        usernameText.setBounds(100, 20, 310, 25);
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

        password2Text = new JPasswordField();
        password2Text.setBounds(100, 100, 310, 25);
        getContentPane().add(password2Text);
        password2Label = new JLabel("Confirm");
        password2Label.setBounds(10, 100, 80, 25);
        getContentPane().add(password2Label);

        userPermissionLabel = new JLabel("Permission");
        userPermissionLabel.setBounds(10, 140, 80, 25);
        getContentPane().add(userPermissionLabel);
        JComboBox<String> permissionComboBox = new JComboBox<>();
        permissionComboBox.addItem("guest");
        permissionComboBox.addItem("admin");
        permissionComboBox.setBounds(100, 140, 310, 25);
        getContentPane().add(permissionComboBox);

        JButton registerButton = new JButton("Sign-up");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userUsername = usernameText.getText();
                char[] password1 = password1Text.getPassword();
                String password1String = new String(password1);
                char[] password2 = password2Text.getPassword();
                String password2String = new String(password2);
                String userPermission = (String) permissionComboBox.getSelectedItem();

                if (!password1String.equals(password2String)) {
                    JOptionPane.showMessageDialog(registerWindow.this,
                            "Passwords should be the same!", "Register error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String[] userInfo = {userUsername, password1String, userPermission};
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath, true))) {
                        bw.write(String.join(",", userInfo) + "\n");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                openLoginWindow();
            }
        });
        registerButton.setBounds(10, 180,120, 25);
        getContentPane().add(registerButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginWindow();
            }
        });
        exitButton.setBounds(150, 180,120, 25);
        getContentPane().add(exitButton);
    }

    public void showWindow(){
        this.setVisible(true);
    }

    public void openLoginWindow(){
        this.dispose();
        loginWindow login = new loginWindow();
        login.showWindow();
    }
}
