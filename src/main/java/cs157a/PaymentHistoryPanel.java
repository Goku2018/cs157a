package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;

/**
 * Panel for staff to view payment history for a specific user.
 * Allows lookup by User ID, displays all payments with total amount paid.
 */
public class PaymentHistoryPanel extends JPanel {
    private PaymentDAO paymentDAO;
    private UserDAO userDAO;
    private JTextField userIdField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel memberLabel;
    private JLabel statusLabel;

    public PaymentHistoryPanel(PaymentDAO paymentDAO, UserDAO userDAO) {
        this.paymentDAO = paymentDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField(10);
        filterPanel.add(userIdField);

        JButton searchButton = new JButton("View Payments");
        JButton clearButton = new JButton("Clear");
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        memberLabel = new JLabel(" ");
        memberLabel.setForeground(Color.BLUE);
        filterPanel.add(memberLabel);

        add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"Payment ID", "User ID", "Member Name", "Payment Amount", "Payment Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel = new JLabel("Enter a User ID to view payment history.");
        statusLabel.setForeground(Color.BLUE);
        add(statusLabel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> loadPayments());
        clearButton.addActionListener(e -> clearForm());
        userIdField.addActionListener(e -> loadPayments());
    }

    /**
     * Loads and displays payment history for the entered User ID.
     * Shows payment ID, user ID, member name, amount, and date.
     */
    private void loadPayments() {
        tableModel.setRowCount(0);
        String userIdText = userIdField.getText().trim();
        if (userIdText.isEmpty()) {
            statusLabel.setText("Please enter a User ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            User user = userDAO.getUserById(userId);
            String memberName = user == null ? "Unknown Member" : user.getFullName();
            memberLabel.setText("Member: " + memberName);

            List<Payment> payments = paymentDAO.getPaymentsByUser(userId);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            double totalPaid = 0.0;

            for (Payment payment : payments) {
                totalPaid += payment.getPaymentAmount();
                String paymentDate = payment.getPaymentDate() == null ? "N/A" : payment.getPaymentDate().format(dateFormatter);
                tableModel.addRow(new Object[]{
                        payment.getPaymentID(),
                        payment.getUserId(),
                        memberName,
                        String.format("$%.2f", payment.getPaymentAmount()),
                        paymentDate
                });
            }

            statusLabel.setText("Found " + payments.size() + " payment(s). Total paid: " + String.format("$%.2f", totalPaid));
            statusLabel.setForeground(Color.BLUE);
        } catch (NumberFormatException e) {
            statusLabel.setText("User ID must be a number.");
            statusLabel.setForeground(Color.RED);
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Clears all input fields and resets the form.
     */
    private void clearForm() {
        userIdField.setText("");
        memberLabel.setText(" ");
        tableModel.setRowCount(0);
        statusLabel.setText("Enter a User ID to view payment history.");
        statusLabel.setForeground(Color.BLUE);
    }
}
