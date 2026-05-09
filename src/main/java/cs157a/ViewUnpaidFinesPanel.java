package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

/**
 * Panel for staff to view all members with unpaid fines.
 * Shows member ID, name, total fines, total paid, and amount owed.
 * Allows staff to pay the full outstanding balance for a selected member.
 */
public class ViewUnpaidFinesPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private PaymentDAO paymentDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public ViewUnpaidFinesPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO, PaymentDAO paymentDAO) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
        this.paymentDAO = paymentDAO;

        setLayout(new BorderLayout());

        String[] columns = {"Member ID", "Member Name", "Total Fines", "Total Paid", "Amount Owed"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make read-only (reused from ViewBooksPanel)
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons (reused from ActiveBorrowingsPanel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadUnpaidFines());
        bottomPanel.add(refreshBtn);

        JButton payButton = new JButton("Pay Selected Balance");
        payButton.addActionListener(e -> paySelectedFine());
        bottomPanel.add(payButton);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        loadUnpaidFines();
    }

    /**
     * Loads all members with unpaid fines from the database.
     * Calculates amount owed by subtracting total paid from total fines.
     * Displays only members with amount owed > 0.
     */
    private void loadUnpaidFines() {
        tableModel.setRowCount(0); // Clear existing rows (reused from ViewBooksPanel)
        statusLabel.setText("Loading unpaid fines...");

        try {
            Map<Integer, Double> fineTotalsByUser = borrowRecordDAO.getFineTotalsByUser();

            if (fineTotalsByUser.isEmpty()) {
                statusLabel.setText("No unpaid fines found.");
                return;
            }

            int count = 0;

            for (Map.Entry<Integer, Double> entry : fineTotalsByUser.entrySet()) {
                int userId = entry.getKey();
                double totalFines = entry.getValue();
                double totalPaid = paymentDAO.getTotalPaidForUser(userId);
                double amountOwed = totalFines - totalPaid;

                User user = userDAO.getUserById(userId);
                String memberName = (user != null) ? user.getFullName() : "Unknown Member";

                if (amountOwed > 0) {
                    Object[] row = {
                            userId,
                            memberName,
                            String.format("$%.2f", totalFines),
                            String.format("$%.2f", totalPaid),
                            String.format("$%.2f", amountOwed)
                    };
                    tableModel.addRow(row);
                    count++;
                }
            }

            statusLabel.setText("Found " + count + " member(s) with unpaid fines.");

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading unpaid fines: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Pays the full outstanding fine for the selected member.
     * Requires confirmation before recording the payment.
     */
    private void paySelectedFine() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a fine to pay.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String fineAmountStr = (String) tableModel.getValueAt(selectedRow, 4);
        double fineAmount = Double.parseDouble(fineAmountStr.replace("$", ""));

        if (fineAmount <= 0) {
            JOptionPane.showMessageDialog(this,
                    "This fine has already been paid.",
                    "Already Paid",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Pay fine of " + fineAmountStr + "?",
                "Confirm Payment",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = paymentDAO.recordPaymentForUser(userId, fineAmount, LocalDate.now());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Payment recorded successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadUnpaidFines(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Payment failed. Please try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error processing payment: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
