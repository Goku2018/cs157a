package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BorrowRecordsPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private JTextField userIdField;
    private JTextField bookIdField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public BorrowRecordsPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        searchButton.addActionListener(e -> loadBorrowRecords());
        clearButton.addActionListener(e -> clearFilters());
        userIdField.addActionListener(e -> loadBorrowRecords());
        bookIdField.addActionListener(e -> loadBorrowRecords());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadBorrowRecords();
            }
        });

        loadBorrowRecords();
    }

    private void loadBorrowRecords() {
        tableModel.setRowCount(0);

        try {
            Integer userId = readOptionalInt(userIdField, "User ID");
            Integer bookId = readOptionalInt(bookIdField, "Book ID");
            List<BorrowRecord> records = borrowRecordDAO.getBorrowRecords(userId, bookId, false);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
