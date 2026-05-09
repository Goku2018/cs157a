package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;

/**
 * Panel for members to view their own borrowing history.
 * Shows book title, borrow date, due date, return date, and fine amount.
 * Auto-refreshes when the panel becomes visible.
 */
public class MyBorrowingsPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private int userId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;


    public MyBorrowingsPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO, int userId) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userId = userId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Borrowing History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table columns (reused from BorrowRecordsPanel)
        String[] columns = {"Record ID", "Book Title", "Borrow Date", "Due Date", "Return Date", "Fine Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make read-only
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with refresh button (reused from ViewBooksPanel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadBorrowings());
        bottomPanel.add(refreshBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        bottomPanel.add(statusLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Auto-refresh when panel becomes visible
        addHierarchyListener(e -> {
            if (isShowing()) {
                loadBorrowings();
            }
        });

        // Load data initially
        loadBorrowings();
    }

    /**
     * Loads and displays the current member's borrowing history.
     * Retrieves data from the database and populates the table.
     */
    private void loadBorrowings() {
        tableModel.setRowCount(0);
        statusLabel.setText("Loading your borrowing history...");

        try {
            List<BorrowRecord> borrowings = borrowRecordDAO.getBorrowingsByUser(userId);

            if (borrowings == null || borrowings.isEmpty()) {
                statusLabel.setText("You have no borrowing history.");
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            int count = 0;

            for (BorrowRecord record : borrowings) {
                // Get book title (reused from ActiveBorrowingsPanel)
                Book book = bookDAO.getBookById(record.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "Unknown Book";

                String borrowDate = record.getBorrowDate() != null ?
                        record.getBorrowDate().format(dateFormatter) : "N/A";
                String dueDate = record.getDueDate() != null ?
                        record.getDueDate().format(dateFormatter) : "N/A";
                String returnDate = record.getReturnDate() != null ?
                        record.getReturnDate().format(dateFormatter) : "Not Returned";

                Object[] row = {
                        record.getRecordId(),
                        bookTitle,
                        borrowDate,
                        dueDate,
                        returnDate,
                        String.format("$%.2f", record.getFineAmount())
                };
                tableModel.addRow(row);
                count++;
            }

            statusLabel.setText("Found " + count + " borrowing record(s).");
            statusLabel.setForeground(Color.BLUE);

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Error loading your borrowings: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}