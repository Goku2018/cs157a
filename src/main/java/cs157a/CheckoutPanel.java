package cs157a;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.time.LocalDate;

public class CheckoutPanel extends JPanel {
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;

    // Form fields
    private JTextField memberEmailField;
    private JTextField bookIdField;
    private JLabel memberNameLabel;
    private JLabel bookTitleLabel;
    private JLabel bookStatusLabel;
    private JLabel statusLabel;

    public CheckoutPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO, UserDAO userDAO) {
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel (reused from AddBookPanel)
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Member Email (reused lookup pattern from ReturnPanel)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Member Email:"), gbc);
        memberEmailField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(memberEmailField, gbc);

        JButton lookupMemberBtn = new JButton("Lookup Member");
        gbc.gridx = 2;
        formPanel.add(lookupMemberBtn, gbc);

        // Row 1: Member Name (display)
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Member Name:"), gbc);
        memberNameLabel = new JLabel(" ");
        memberNameLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(memberNameLabel, gbc);
        gbc.gridwidth = 1;

        // Row 2: Book ID
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Book ID:"), gbc);
        bookIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(bookIdField, gbc);

        JButton lookupBookBtn = new JButton("Lookup Book");
        gbc.gridx = 2;
        formPanel.add(lookupBookBtn, gbc);

        // Row 3: Book Title (display)
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Book Title:"), gbc);
        bookTitleLabel = new JLabel(" ");
        bookTitleLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(bookTitleLabel, gbc);
        gbc.gridwidth = 1;

        // Row 4: Book Status (display)
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Book Status:"), gbc);
        bookStatusLabel = new JLabel(" ");
        bookStatusLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(bookStatusLabel, gbc);

        // Row 5: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton checkoutBtn = new JButton("Check Out Book");
        JButton clearBtn = new JButton("Clear");
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Row 6: Status Label (reused from ProcessPaymentPanel)
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridy = 6;
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Event handlers
        lookupMemberBtn.addActionListener(e -> lookupMember());
        lookupBookBtn.addActionListener(e -> lookupBook());
        checkoutBtn.addActionListener(e -> checkoutBook());
        clearBtn.addActionListener(e -> clearForm());
    }

    // Reused pattern from ReturnPanel
    private void lookupMember() {
        String email = memberEmailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Please enter a member email.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            User user = userDAO.getUserByEmail(email);
            if (user != null && user.getStatus().equalsIgnoreCase("Member")) {
                memberNameLabel.setText(user.getFullName());
                statusLabel.setText("Member found: " + user.getFullName());
                statusLabel.setForeground(Color.GREEN);
            } else {
                memberNameLabel.setText(" ");
                statusLabel.setText("Member not found.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    // Reused pattern from ReturnPanel
    private void lookupBook() {
        String bookIdStr = bookIdField.getText().trim();
        if (bookIdStr.isEmpty()) {
            statusLabel.setText("Please enter a Book ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookDAO.getBookById(bookId);

            if (book != null) {
                bookTitleLabel.setText(book.getTitle());
                bookStatusLabel.setText(book.getStatus());

                if (book.getStatus().equalsIgnoreCase("Available")) {
                    statusLabel.setText("Book is available for checkout.");
                    statusLabel.setForeground(Color.GREEN);
                } else {
                    statusLabel.setText("Book is " + book.getStatus() + " - not available.");
                    statusLabel.setForeground(Color.RED);
                }
            } else {
                bookTitleLabel.setText(" ");
                bookStatusLabel.setText(" ");
                statusLabel.setText("Book not found.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Book ID.");
            statusLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    private void checkoutBook() {
        String email = memberEmailField.getText().trim();
        String bookIdStr = bookIdField.getText().trim();

        if (email.isEmpty() || bookIdStr.isEmpty()) {
            statusLabel.setText("Please lookup both member and book first.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (!bookStatusLabel.getText().equalsIgnoreCase("Available")) {
            statusLabel.setText("Book is not available for checkout.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        if (memberNameLabel.getText().trim().isEmpty()) {
            statusLabel.setText("Please lookup a valid member first.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            User user = userDAO.getUserByEmail(email);
            int bookId = Integer.parseInt(bookIdStr);
            LocalDate dueDate = LocalDate.now().plusDays(14);

            boolean success = borrowRecordDAO.checkoutBook(bookId, user.getUserId());

            if (success) {
                statusLabel.setText("Book checked out successfully!");
                statusLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this,
                        "Book checked out to " + user.getFullName() + "\nDue date: " + dueDate,
                        "Checkout Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                statusLabel.setText("Checkout failed. Please try again.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            statusLabel.setText("Invalid Book ID.");
            statusLabel.setForeground(Color.RED);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        memberEmailField.setText("");
        bookIdField.setText("");
        memberNameLabel.setText(" ");
        bookTitleLabel.setText(" ");
        bookStatusLabel.setText(" ");
        statusLabel.setText("Form cleared.");
        statusLabel.setForeground(Color.BLUE);
    }
}
