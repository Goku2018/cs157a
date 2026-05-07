package cs157a;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.time.LocalDateTime;

public class UpdateMemberPanel extends JPanel {
    private UserDAO userDAO;

    // Form fields
    private JTextField userIdField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> statusCombo;
    private JTextField phoneField;
    private JTextField addressField;
    private JLabel messageLabel;

    public UpdateMemberPanel(UserDAO userDAO) {
        this.userDAO = userDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: User ID (to lookup)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);
        userIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);

        JButton lookupButton = new JButton("Lookup Member");
        gbc.gridx = 2;
        formPanel.add(lookupButton, gbc);

        // Row 1: Full Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        // Row 2: Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Row 3: Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Row 4: Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Status:"), gbc);
        String[] statuses = {"Member", "Staff"};
        statusCombo = new JComboBox<>(statuses);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        // Row 5: Phone
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Row 6: Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Row 7: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update Member");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(updateButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Row 8: Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
        gbc.gridy = 8;
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Event handlers
        lookupButton.addActionListener(e -> lookupMember());
        updateButton.addActionListener(e -> updateMember());
        clearButton.addActionListener(e -> clearForm());
    }

    private void lookupMember() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            messageLabel.setText("Please enter a User ID.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userDAO.getUserById(userId);

            if (user == null) {
                messageLabel.setText("Member not found with ID: " + userId);
                messageLabel.setForeground(Color.RED);
                clearForm();
                return;
            }

            // Populate fields with existing data
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            passwordField.setText(user.getPassword());
            statusCombo.setSelectedItem(user.getStatus());
            phoneField.setText(user.getPhone());
            addressField.setText(user.getAddress());

            messageLabel.setText("Member found. Update fields as needed.");
            messageLabel.setForeground(Color.GREEN);

        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid User ID. Please enter a number.");
            messageLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void updateMember() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            messageLabel.setText("Please enter a User ID first.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String status = (String) statusCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Validate input
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all required fields.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            // Create User object with updated values
            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(password);
            user.setStatus(status);
            user.setPhone(phone);
            user.setAddress(address);
            user.setRegistrationDate(LocalDateTime.now());

            boolean success = userDAO.updateUser(user);

            if (success) {
                messageLabel.setText("Member updated successfully!");
                messageLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this,
                        "Member " + fullName + " has been updated.",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                messageLabel.setText("Update failed. Please try again.");
                messageLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid User ID. Please enter a number.");
            messageLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        userIdField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        statusCombo.setSelectedIndex(0);
        phoneField.setText("");
        addressField.setText("");
        messageLabel.setText("Form cleared.");
        messageLabel.setForeground(Color.BLUE);
    }
}