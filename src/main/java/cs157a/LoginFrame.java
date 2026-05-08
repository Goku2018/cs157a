package cs157a;

import cs157a.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginFrame() {
        setTitle("Library Management System - Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(emailLabel, gbc);

        emailField = new JTextField(25);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        panel.add(messageLabel, gbc);

        add(panel);

        loginButton.addActionListener(this::authenticate);
        getRootPane().setDefaultButton(loginButton);
    }

    private void authenticate(ActionEvent e) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        System.out.println("========== LOGIN ATTEMPT ==========");
        System.out.println("Email entered: " + email);
        System.out.println("Password entered: " + password);

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in both fields.");
            System.out.println("Login failed: Empty fields");
            return;
        }

        try {
            System.out.println("Calling DatabaseConnection.authenticate...");
            User loggedInUser = DatabaseConnection.authenticate(email, password);

            System.out.println("User found: " + (loggedInUser != null ? "YES" : "NO"));

            if (loggedInUser != null) {
                System.out.println("User Details:");
                System.out.println("  - UserID: " + loggedInUser.getUserId());
                System.out.println("  - FullName: " + loggedInUser.getFullName());
                System.out.println("  - Status: " + loggedInUser.getStatus());
                System.out.println("  - Email: " + loggedInUser.getEmail());

                String dashboardRole = loggedInUser.getStatus().equalsIgnoreCase("Staff") ? "staff" : "member";
                System.out.println("Dashboard role: " + dashboardRole);
                System.out.println("Creating MainDashboard...");

                messageLabel.setText("Login Successful (" + loggedInUser.getStatus() + ")");
                new MainDashboard(dashboardRole, loggedInUser.getEmail()).setVisible(true);
                System.out.println("MainDashboard created, disposing LoginFrame...");
                dispose();
            } else {
                System.out.println("Login failed: User not found or password incorrect");
                messageLabel.setText("Invalid email or password. Try again");
                passwordField.setText("");
            }
        } catch (Exception ex) {
            System.out.println("EXCEPTION during login:");
            ex.printStackTrace();
            messageLabel.setText("Database error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Unable to connect to database.\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("========== END LOGIN ATTEMPT ==========\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}