package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

        // Column names (reused from ViewBooksPanel pattern)
        String[] columns = {"Record ID", "Book Title", "Member Name", "Due Date", "Days Overdue", "Fine Owed", "Paid Status"};
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

        JButton payButton = new JButton("Pay Selected Fine");
        payButton.addActionListener(e -> paySelectedFine());
        bottomPanel.add(payButton);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        loadUnpaidFines();
    }

    private void loadUnpaidFines() {
        tableModel.setRowCount(0); // Clear existing rows (reused from ViewBooksPanel)
        statusLabel.setText("Loading unpaid fines...");

        try {
            List<BorrowRecord> unpaidRecords = borrowRecordDAO.getUnpaidFines();

            if (unpaidRecords == null || unpaidRecords.isEmpty()) {
                statusLabel.setText("No unpaid fines found.");
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            int count = 0;

            for (BorrowRecord record : unpaidRecords) {
                // Get book title (reused from ReturnPanel pattern)
                Book book = bookDAO.getBookById(record.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "Unknown Book";

                // Get member name (reused from ReturnPanel pattern)
                User user = userDAO.getUserById(record.getUserId());
                String memberName = (user != null) ? user.getFullName() : "Unknown Member";

                // Calculate days overdue (reused from ReturnPanel.calculateFineDisplay)
                long daysOverdue = 0;
                if (record.getDueDate() != null) {
                    LocalDate today = LocalDate.now();
                    if (today.isAfter(record.getDueDate())) {
                        daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), today);
                    }
                }

                // Format due date
                String dueDateStr = record.getDueDate() != null ?
                        record.getDueDate().format(dateFormatter) : "N/A";

                // Calculate remaining fine after payments (reused from ProcessPaymentPanel)
                double totalPaid = paymentDAO.getTotalPaidForBorrowRecord(record.getRecordId());
                double amountOwed = record.getFineAmount() - totalPaid;
                String paidStatus = (amountOwed <= 0) ? "Paid" : "Unpaid";

                // Only show if still owed (reused condition)
                if (amountOwed > 0) {
                    Object[] row = {
                            record.getRecordId(),
                            bookTitle,
                            memberName,
                            dueDateStr,
                            daysOverdue,
                            String.format("$%.2f", amountOwed),
                            paidStatus
                    };
                    tableModel.addRow(row);
                    count++;
                }
            }

            statusLabel.setText("Found " + count + " unpaid fine(s).");

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading unpaid fines: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void paySelectedFine() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a fine to pay.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        long borrowRecordId = (long) tableModel.getValueAt(selectedRow, 0);
        String fineAmountStr = (String) tableModel.getValueAt(selectedRow, 5);
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
                boolean success = paymentDAO.recordPayment(borrowRecordId, fineAmount, LocalDate.now());
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