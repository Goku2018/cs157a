package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ActiveBorrowingsPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public ActiveBorrowingsPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout());

        // Column names (reused from ViewBooksPanel pattern)
        String[] columns = {"Record ID", "Book Title", "Member Name", "Borrow Date", "Due Date", "Days Overdue", "Fine Amount"};
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

        // Bottom panel with refresh button (reused from ViewBooksPanel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadActiveBorrowings());
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        loadActiveBorrowings();
    }

    private void loadActiveBorrowings() {
        tableModel.setRowCount(0); // Clear existing rows (reused from ViewBooksPanel)

        try {
            List<BorrowRecord> activeBorrowings = borrowRecordDAO.getActiveBorrowings();

            if (activeBorrowings == null || activeBorrowings.isEmpty()) {
                //JOptionPane.showMessageDialog(this,
                        //"No active borrowings found.",
                        //"Information",
                        //JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (BorrowRecord record : activeBorrowings) {
                // Get book title (reused pattern from ReturnPanel)
                Book book = bookDAO.getBookById(record.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "Unknown Book";

                // Get member name (reused pattern from ReturnPanel)
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

                // Format dates (reused from ReturnPanel)
                String borrowDateStr = record.getBorrowDate() != null ?
                        record.getBorrowDate().format(dateFormatter) : "N/A";
                String dueDateStr = record.getDueDate() != null ?
                        record.getDueDate().format(dateFormatter) : "N/A";

                // Fine amount (reused from ProcessPaymentPanel)
                String fineAmountStr = String.format("$%.2f", record.getFineAmount());

                Object[] row = {
                        record.getRecordId(),
                        bookTitle,
                        memberName,
                        borrowDateStr,
                        dueDateStr,
                        daysOverdue,
                        fineAmountStr
                };
                tableModel.addRow(row);
            }

        } catch (Exception ex) {
            // Error handling (reused from all panels)
            JOptionPane.showMessageDialog(this,
                    "Error loading active borrowings: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}