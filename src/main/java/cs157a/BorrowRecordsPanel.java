package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Panel for staff to view and filter all borrow records in the system.
 * Allows filtering by User ID and/or Book ID.
 */
public class BorrowRecordsPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private JTextField userIdField;
    private JTextField bookIdField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    /**
     * Constructor - initializes the UI components and loads initial data.
     * @param borrowRecordDAO Data access object for borrow records
     * @param bookDAO Data access object for books
     * @param userDAO Data access object for users
     */
    public BorrowRecordsPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create filter panel with search inputs
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField(10);
        filterPanel.add(userIdField);

        filterPanel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField(10);
        filterPanel.add(bookIdField);

        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        statusLabel = new JLabel("Enter a User ID, Book ID, or both to filter records.");
        statusLabel.setForeground(Color.BLUE);
        filterPanel.add(statusLabel);

        add(filterPanel, BorderLayout.NORTH);

        // Create table to display borrow records
        String[] columns = {"Record ID", "Book ID", "Book Title", "User ID", "Member Name", "Borrow Date", "Due Date", "Return Date", "Fine Amount"};
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

        // Add event listeners
        searchButton.addActionListener(e -> loadBorrowRecords());
        clearButton.addActionListener(e -> clearFilters());
        userIdField.addActionListener(e -> loadBorrowRecords());
        bookIdField.addActionListener(e -> loadBorrowRecords());

        // Refresh data when panel becomes visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadBorrowRecords();
            }
        });
        loadBorrowRecords();
    }

    /**
     * Loads borrow records from the database based on filter criteria.
     * Displays results in the table with book titles and member names.
     */
    private void loadBorrowRecords() {
        tableModel.setRowCount(0);

        try {
            // Get filter values (null if empty)
            Integer userId = readOptionalInt(userIdField, "User ID");
            Integer bookId = readOptionalInt(bookIdField, "Book ID");

            // Fetch records from database
            List<BorrowRecord> records = borrowRecordDAO.getBorrowRecords(userId, bookId, false);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Populate table with data
            for (BorrowRecord record : records) {
                Book book = bookDAO.getBookById(record.getBookId());
                User user = userDAO.getUserById(record.getUserId());
                String borrowDate = record.getBorrowDate() == null ? "N/A" : record.getBorrowDate().format(dateFormatter);
                String dueDate = record.getDueDate() == null ? "N/A" : record.getDueDate().format(dateFormatter);
                String returnDate = record.getReturnDate() == null ? "Not Returned" : record.getReturnDate().format(dateFormatter);

                tableModel.addRow(new Object[]{
                        record.getRecordId(),
                        record.getBookId(),
                        book == null ? "Unknown Book" : book.getTitle(),
                        record.getUserId(),
                        user == null ? "Unknown Member" : user.getFullName(),
                        borrowDate,
                        dueDate,
                        returnDate,
                        String.format("$%.2f", record.getFineAmount())
                });
            }

            // Update status message
            statusLabel.setText("Found " + records.size() + " borrow record(s).");
            statusLabel.setForeground(Color.BLUE);
        } catch (NumberFormatException e) {
            statusLabel.setText(e.getMessage());
            statusLabel.setForeground(Color.RED);
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Error loading borrow records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads an integer value from a text field.
     * @param field The text field to read from
     * @param label The label for error message
     * @return Integer value, or null if field is empty
     * @throws NumberFormatException if value is not a valid number
     */
    private Integer readOptionalInt(JTextField field, String label) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(label + " must be a number.");
        }
    }

    private void clearFilters() {
        userIdField.setText("");
        bookIdField.setText("");
        loadBorrowRecords();
    }
}
