package cs157a;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;

/**
 * Panel for staff to delete members from the library system.
 * Requires lookup by User ID first, displays member info,
 * and requires confirmation before deletion.
 */
public class DeleteMemberPanel extends JPanel {
    private UserDAO userDAO;

    // Form fields
    private JTextField userIdField;
    private JLabel memberNameLabel;
    private JLabel memberEmailLabel;
    private JLabel memberStatusLabel;
    private JLabel messageLabel;

    public DeleteMemberPanel(UserDAO userDAO) {
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

        // Row 1: Member Name (display)
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Member Name:"), gbc);
        memberNameLabel = new JLabel(" ");
        memberNameLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(memberNameLabel, gbc);
        gbc.gridwidth = 1;

        // Row 2: Member Email (display)
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        memberEmailLabel = new JLabel(" ");
        memberEmailLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(memberEmailLabel, gbc);

        // Row 3: Member Status (display)
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        memberStatusLabel = new JLabel(" ");
        memberStatusLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(memberStatusLabel, gbc);

        // Row 4: Warning message
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JLabel warningLabel = new JLabel("WARNING: This action cannot be undone!");
        warningLabel.setForeground(Color.RED);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(warningLabel, gbc);
        gbc.gridwidth = 1;

        // Row 5: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton deleteButton = new JButton("Delete Member");
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.WHITE);
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Row 6: Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
        gbc.gridy = 6;
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Event handlers
        lookupButton.addActionListener(e -> lookupMember());
        deleteButton.addActionListener(e -> deleteMember());
        clearButton.addActionListener(e -> clearForm());
    }

    /**
     * Looks up a member by User ID and displays their information.
     */
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
                clearDisplay();
                return;
            }

            // Display member info
            memberNameLabel.setText(user.getFullName());
            memberEmailLabel.setText(user.getEmail());
            memberStatusLabel.setText(user.getStatus());

            messageLabel.setText("Member found. Click 'Delete Member' to remove.");
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

    /**
     * Deletes a member after confirmation.
     * Checks for active borrowings before deletion.
     */
    private void deleteMember() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            messageLabel.setText("Please enter a User ID first.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            // Confirm deletion
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this member?\n" +
                            "This action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                messageLabel.setText("Deletion cancelled.");
                messageLabel.setForeground(Color.BLUE);
                return;
            }

            boolean success = userDAO.deleteUser(userId);

            if (success) {
                messageLabel.setText("Member deleted successfully!");
                messageLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this,
                        "Member has been deleted.",
                        "Deletion Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                messageLabel.setText("Cannot delete member with active borrowings.");
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

    /**
     * Clears the displayed member information labels.
     */
    private void clearDisplay() {
        memberNameLabel.setText(" ");
        memberEmailLabel.setText(" ");
        memberStatusLabel.setText(" ");
    }

    /**
     * Clears all input fields and resets the form.
     */
    private void clearForm() {
        userIdField.setText("");
        clearDisplay();
        messageLabel.setText("Form cleared.");
        messageLabel.setForeground(Color.BLUE);
    }
}