package cs157a;

import javax.swing.*;
import javax.swing.border.BorderFactory;
import java.awt.*;
import java.time.LocalDateTime;

public class RegisterMemberPanel extends JPanel {
    private UserDAO userDAO;

    //Form Fields
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public RegisterMemberPanel(UserDAO userDAO){
        this.userDAO = userDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8,8 );
        gbc.anchor = GridBagConstraints.WEST;

        //Row 0 Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        fullNameField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(fullNameField, gbc);

        //Row 1 Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        //Row 2 Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(25);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        //Row 3 Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        //Row 4 Address
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        //Row 5 Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton registerButton = new JButton("Register Member");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(registerButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        //Row 6 Message label
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.BLUE);
        gbc.gridy = 6;
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        //Event handlers
        registerButton.addActionListener(e-> registerMember());
        clearButton.addActionListener(e -> clearForm());

    }
    private void registerMember(){
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Vaildate input
        if(fullName.isEmpty() || email.isEmpty() || password.isEmpty()){
            messageLabel.setText("Please fill in all required fields (*).");
            messageLabel.setForeground(Color.RED);
            return;
        }
        //Create User object, member default
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus("Member");
        user.setPhone(phone);
        user.setAddress(address);
        user.setRegistrationDate(LocalDateTime.now());

        //Try catch
        try{
                boolean success = userDAO.registerUser(user);
                if(success) {
                    messageLabel.setText("Member registered successfully...");
                    messageLabel.setForeground(Color.GREEN);
                    clearForm();
                    JOptionPane.showMessageDialog(this, "Member " + fullName + " has been registered.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else{
                    messageLabel.setText("Registration failed. Email may already exist.");
                    messageLabel.setForeground(Color.RED);
                }
        }catch (Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearForm(){
        fullNameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        addressField.setText("");
        messageLabel.setText("Form cleared.");
        messageLabel.setForeground(Color.BLUE);
    }
}
