package cs157a;

import cs157a.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame{
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;


    //Just for testing

    public LoginFrame(){

        setTitle("Library Management System - Login");
        setSize(400, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8,8,8);

        //Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(18);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        //Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(18);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        //Login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        //Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 3;
        panel.add(messageLabel, gbc);

        add(panel);

        //Login action
        loginButton.addActionListener(this::authenticate);
        getRootPane().setDefaultButton(loginButton);

    }
    //Authenticate Method
    private void authenticate(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(email.isEmpty() || password.isEmpty()){
            messageLabel.setText("Please fill in both fields.");
            return;
        }

        //Accessing database
        String role = DatabaseConnection.authenticate(email, password);
        if(role != null) {
            String dashboardRole = role.equalsIgnoreCase("Staff") ? "staff" : "member";
            messageLabel.setText("Login Successful" + role + ")");
            new MainDashboard(dashboardRole).setVisible(true);
            dispose();
        }
        else {
            messageLabel.setText("Invalid email or password.  Try again");
            passwordField.setText("");
        }

    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()-> new LoginFrame().setVisible(true));

    }
}
