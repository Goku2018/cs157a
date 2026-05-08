package cs157a;

import javax.swing.*;
//import javax.swing.border.BorderFactory;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class ProcessPaymentPanel extends JPanel{
    private PaymentDAO paymentDAO;
    private BorrowRecordDAO borrowRecordDAO;

    //Form fields
    private JTextField userIdField;
    private JTextField amountField;
    private JLabel statusLabel;

    public ProcessPaymentPanel(PaymentDAO paymentDAO, BorrowRecordDAO borrowRecordDAO){
        this.paymentDAO = paymentDAO;
        this.borrowRecordDAO = borrowRecordDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        //Row 0 User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID: "), gbc);
        userIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);

        JButton lookupButton = new JButton("Check Balance");
        gbc.gridx = 2;
        formPanel.add(lookupButton, gbc);

        //Row 1 Payment Amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Payment Amount: "), gbc);
        amountField = new JTextField(20);
        amountField.setEditable(true);
        amountField.setBackground(Color.WHITE);
        gbc.gridx = 1;
        formPanel.add(amountField, gbc);

        //Row 2 Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        JButton payButton = new JButton("Process Payment");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(payButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        //Row 3 Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridy = 3;
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        //Event handlers
        lookupButton.addActionListener(e-> checkFine());
        payButton.addActionListener(e-> processPayment());
        clearButton.addActionListener(e-> clearForm());

    }
    private void checkFine() {
        String userIdStr = userIdField.getText().trim();
        if (userIdStr.isEmpty()) {
            statusLabel.setText("Please enter a User ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }
        try {
            int userId = Integer.parseInt(userIdStr);
            double amountOwed = getAmountOwedForUser(userId);
            if (amountOwed > 0) {
                amountField.setText(String.format("$%.2f", amountOwed));
                statusLabel.setText("Amount owed: $" + String.format("%.2f", amountOwed) + ". Edit amount for a partial payment.");
                statusLabel.setForeground(Color.BLUE);
            } else {
                amountField.setText("$0.00");
                statusLabel.setText("No outstanding fine.");
                statusLabel.setForeground(Color.GREEN);
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid User ID. Please enter a number");
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
    private void processPayment(){
        String userIdStr = userIdField.getText().trim();
        String amountStr = amountField.getText().trim();

        if(userIdStr.isEmpty()){
            statusLabel.setText("Please enter a User ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }
        try{
            int userId = Integer.parseInt(userIdStr);
            //Remove $ if present
            if(amountStr.startsWith("$")){
                amountStr = amountStr.substring(1);
            }
            if(amountStr.isEmpty()){
                amountStr = String.format("%.2f", getAmountOwedForUser(userId));
            }
            double amount = Double.parseDouble(amountStr);
            double amountOwed = getAmountOwedForUser(userId);
            if(amount <= 0){
                statusLabel.setText("Payment amount must be greater than $0.00.");
                statusLabel.setForeground(Color.RED);
                return;
            }
            if(amountOwed <= 0){
                statusLabel.setText("No outstanding fine.");
                statusLabel.setForeground(Color.GREEN);
                return;
            }
            if(amount > amountOwed){
                statusLabel.setText("Payment cannot exceed amount owed: $" + String.format("%.2f", amountOwed));
                statusLabel.setForeground(Color.RED);
                return;
            }
            boolean success = paymentDAO.recordPaymentForUser(userId, amount, LocalDate.now());
            if(success){
                statusLabel.setText("Payment of $" + String.format("%.2f ", amount) + " recorded successfully..");
                statusLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this, "Payment of $" + String.format("%.2f", amount) + "has been recorded.", "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else{
                statusLabel.setText("Payment failed. Try again.");
                statusLabel.setForeground(Color.RED);
            }
        } catch(Exception ex){
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private double getAmountOwedForUser(int userId) {
        Map<Integer, Double> fineTotalsByUser = borrowRecordDAO.getFineTotalsByUser();
        double totalFines = fineTotalsByUser.getOrDefault(userId, 0.0);
        double totalPaid = paymentDAO.getTotalPaidForUser(userId);
        return totalFines - totalPaid;
    }

    private void clearForm(){
        userIdField.setText("");
        amountField.setText("");
        statusLabel.setText("Form cleared.");
        statusLabel.setForeground(Color.BLUE);
    }
}
