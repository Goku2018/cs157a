package cs157a;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyProfilePanel extends JPanel {
    private UserDAO userDAO;
    private String userEmail;
    private int userId;

    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JTextField addressField;
    private JLabel statusLabel;
    private JLabel userIdLabel;
    private JLabel memberStatusLabel;
    private JLabel registrationDateLabel;

    public MyProfilePanel(UserDAO userDAO, String userEmail) {
        System.out.println("========== MY PROFILE PANEL CREATED ==========");
        System.out.println("userEmail received: " + userEmail);

        this.userDAO = userDAO;
        this.userEmail = userEmail;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create UI components FIRST
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // User ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("User ID:"), gbc);
        userIdLabel = new JLabel(" ");
        userIdLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(userIdLabel, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        memberStatusLabel = new JLabel(" ");
        memberStatusLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(memberStatusLabel, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Registration Date
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Registered:"), gbc);
        registrationDateLabel = new JLabel(" ");
        registrationDateLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(registrationDateLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update Profile");
        updateButton.setBackground(new Color(40, 167, 69));
        updateButton.setForeground(Color.WHITE);
        JButton resetButton = new JButton("Reset");
        buttonPanel.add(updateButton);
        buttonPanel.add(resetButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Status message - CREATE THIS BEFORE loadUserData
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridy = 10;
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        updateButton.addActionListener(e -> updateProfile());
        resetButton.addActionListener(e -> resetForm());

        // NOW load user data AFTER all UI components are created
        System.out.println("Loading user data for email: " + userEmail);
        loadUserData();

        System.out.println("MyProfilePanel initialization complete");
        System.out.println("====================================\n");
    }

    private void loadUserData() {
        System.out.println("loadUserData() called for email: " + userEmail);
        try {
            User user = userDAO.getUserByEmail(userEmail);
            System.out.println("User from database: " + (user != null ? "FOUND" : "NULL"));

            if (user != null) {
                userId = user.getUserId();
                userIdLabel.setText(String.valueOf(user.getUserId()));
                memberStatusLabel.setText(user.getStatus());
                fullNameField.setText(user.getFullName());
                emailField.setText(user.getEmail());
                passwordField.setText(user.getPassword());
                phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
                addressField.setText(user.getAddress() != null ? user.getAddress() : "");

                if (user.getRegistrationDate() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    registrationDateLabel.setText(user.getRegistrationDate().format(formatter));
                }
                statusLabel.setText("Profile loaded.");
                statusLabel.setForeground(Color.BLUE);
                System.out.println("User data loaded successfully for: " + user.getFullName());
            } else {
                System.out.println("ERROR: User not found with email: " + userEmail);
                statusLabel.setText("Error: User not found.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            System.out.println("EXCEPTION in loadUserData():");
            ex.printStackTrace();
            statusLabel.setText("Error loading profile: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    private void updateProfile() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill in all required fields.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            User original = userDAO.getUserByEmail(userEmail);
            if (original == null) {
                statusLabel.setText("User not found.");
                statusLabel.setForeground(Color.RED);
                return;
            }

            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setStatus(original.getStatus());
            user.setPhone(phone);
            user.setAddress(address);
            user.setRegistrationDate(original.getRegistrationDate());

            boolean success = userDAO.updateUser(user);

            if (success) {
                statusLabel.setText("Profile updated successfully!");
                statusLabel.setForeground(Color.GREEN);
                this.userEmail = email;
                JOptionPane.showMessageDialog(this,
                        "Your profile has been updated.",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                statusLabel.setText("Update failed. Please try again.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        loadUserData();
        statusLabel.setText("Form reset to saved values.");
        statusLabel.setForeground(Color.BLUE);
    }
}