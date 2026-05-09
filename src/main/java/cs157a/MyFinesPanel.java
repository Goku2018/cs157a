package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyFinesPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private PaymentDAO paymentDAO;
    private int userId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public MyFinesPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, PaymentDAO paymentDAO, int userId) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.paymentDAO = paymentDAO;
        this.userId = userId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Unpaid Fines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table columns (reused from ViewUnpaidFinesPanel)
        String[] columns = {"Record ID", "Book Title", "Borrow Date", "Due Date", "Fine Amount", "Paid", "Amount Owed"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadFines());
        bottomPanel.add(refreshBtn);
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Auto-refresh when panel becomes visible
        addHierarchyListener(e -> {
            if (isShowing()) {
                loadFines();
            }
        });

        loadFines();
    }

    private void loadFines() {
        tableModel.setRowCount(0);
        statusLabel.setText("Loading your unpaid fines...");

        try {
            List<BorrowRecord> borrowings = borrowRecordDAO.getBorrowingsByUser(userId);

            if (borrowings == null || borrowings.isEmpty()) {
                statusLabel.setText("You have no borrowing history.");
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            int unpaidCount = 0;
            double totalOwed = 0.0;

            for (BorrowRecord record : borrowings) {
                // Get total paid for this borrow record
                double totalPaid = paymentDAO.getTotalPaidForBorrowRecord(record.getRecordId());
                double amountOwed = record.getFineAmount() - totalPaid;

                if (amountOwed > 0.01) { // only show if still owed
                    Book book = bookDAO.getBookById(record.getBookId());
                    String bookTitle = (book != null) ? book.getTitle() : "Unknown Book";

                    String borrowDate = record.getBorrowDate() != null ?
                            record.getBorrowDate().format(dateFormatter) : "N/A";
                    String dueDate = record.getDueDate() != null ?
                            record.getDueDate().format(dateFormatter) : "N/A";

                    Object[] row = {
                            record.getRecordId(),
                            bookTitle,
                            borrowDate,
                            dueDate,
                            String.format("$%.2f", record.getFineAmount()),
                            String.format("$%.2f", totalPaid),
                            String.format("$%.2f", amountOwed)
                    };
                    tableModel.addRow(row);
                    unpaidCount++;
                    totalOwed += amountOwed;
                }
            }

            if (unpaidCount == 0) {
                statusLabel.setText("You have no unpaid fines.");
            } else {
                statusLabel.setText("You have " + unpaidCount + " unpaid fine(s). Total owed: $" + String.format("%.2f", totalOwed));
                statusLabel.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Error loading your fines: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}