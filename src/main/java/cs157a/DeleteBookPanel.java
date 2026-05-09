package cs157a;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for staff to delete books from the library system.
 * Requires lookup by Book ID first, displays book details,
 * and requires confirmation before deletion.
 * Only allows deletion if book is not currently borrowed.
 */
public class DeleteBookPanel extends JPanel {
    private BookDAO bookDAO;
    private JTextField bookIdField;
    private JLabel titleValueLabel;
    private JLabel authorValueLabel;
    private JLabel genreValueLabel;
    private JLabel isbnValueLabel;
    private JLabel statusValueLabel;
    private JLabel messageLabel;
    private Book loadedBook;

    public DeleteBookPanel(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Book ID:"), gbc);
        bookIdField = new JTextField(10);
        gbc.gridx = 1;
        formPanel.add(bookIdField, gbc);

        JButton loadButton = new JButton("Load Book");
        gbc.gridx = 2;
        formPanel.add(loadButton, gbc);

        titleValueLabel = new JLabel(" ");
        authorValueLabel = new JLabel(" ");
        genreValueLabel = new JLabel(" ");
        isbnValueLabel = new JLabel(" ");
        statusValueLabel = new JLabel(" ");

        addReadOnlyRow(formPanel, gbc, 1, "Title:", titleValueLabel);
        addReadOnlyRow(formPanel, gbc, 2, "Author:", authorValueLabel);
        addReadOnlyRow(formPanel, gbc, 3, "Genre:", genreValueLabel);
        addReadOnlyRow(formPanel, gbc, 4, "ISBN:", isbnValueLabel);
        addReadOnlyRow(formPanel, gbc, 5, "Status:", statusValueLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton deleteButton = new JButton("Delete Book");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        messageLabel = new JLabel("Enter a Book ID and click Load Book.");
        messageLabel.setForeground(Color.BLUE);
        gbc.gridy = 7;
        formPanel.add(messageLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearForm());
        bookIdField.addActionListener(e -> loadBook());
    }

    /**
     * Helper method to add a read-only row to the form.
     * @param formPanel The panel to add the row to
     * @param gbc GridBagConstraints for layout
     * @param row Row index
     * @param label Label text
     * @param valueLabel Label to display the value
     */
    private void addReadOnlyRow(JPanel formPanel, GridBagConstraints gbc, int row, String label, JLabel valueLabel) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(valueLabel, gbc);
        gbc.gridwidth = 1;
    }

    /**
     * Loads a book by ID and displays its details.
     */
    private void loadBook() {
        Integer bookId = readBookId();
        if(bookId == null){
            return;
        }

        try{
            loadedBook = bookDAO.getBookById(bookId);
            if(loadedBook == null){
                clearBookDetails();
                messageLabel.setText("No book found with ID " + bookId + ".");
                messageLabel.setForeground(Color.RED);
                return;
            }

            titleValueLabel.setText(loadedBook.getTitle());
            authorValueLabel.setText(loadedBook.getAuthor());
            genreValueLabel.setText(loadedBook.getGenre());
            isbnValueLabel.setText(loadedBook.getIsbn());
            statusValueLabel.setText(loadedBook.getStatus());
            messageLabel.setText("Book loaded. Review details before deleting.");
            messageLabel.setForeground(Color.BLUE);
        } catch(Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a book after confirmation.
     * Checks if the book is borrowed before deletion.
     */
    private void deleteBook() {
        Integer bookId = readBookId();
        if(bookId == null){
            return;
        }

        if(loadedBook == null || loadedBook.getBookId() != bookId){
            loadBook();
            if(loadedBook == null || loadedBook.getBookId() != bookId){
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete book ID " + bookId + " (" + loadedBook.getTitle() + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if(confirm != JOptionPane.YES_OPTION){
            return;
        }

        try{
            boolean success = bookDAO.deleteBook(bookId);
            if(success){
                messageLabel.setText("Book deleted successfully.");
                messageLabel.setForeground(Color.GREEN);
                clearBookDetails();
                loadedBook = null;
                JOptionPane.showMessageDialog(this, "Book " + bookId + " has been deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else{
                messageLabel.setText("Delete failed. Book may not exist or may be borrowed.");
                messageLabel.setForeground(Color.RED);
            }
        } catch(Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reads and validates the Book ID from the text field.
     * @return Book ID as Integer, or null if invalid
     */
    private Integer readBookId() {
        String value = bookIdField.getText().trim();
        if(value.isEmpty()){
            messageLabel.setText("Please enter a Book ID.");
            messageLabel.setForeground(Color.RED);
            return null;
        }

        try{
            return Integer.parseInt(value);
        } catch(NumberFormatException ex){
            messageLabel.setText("Book ID must be a number.");
            messageLabel.setForeground(Color.RED);
            return null;
        }
    }

    /**
     * Clears the displayed book detail labels.
     */
    private void clearBookDetails() {
        titleValueLabel.setText(" ");
        authorValueLabel.setText(" ");
        genreValueLabel.setText(" ");
        isbnValueLabel.setText(" ");
        statusValueLabel.setText(" ");
    }

    /**
     * Clears all input fields and resets the form.
     */
    private void clearForm() {
        bookIdField.setText("");
        loadedBook = null;
        clearBookDetails();
        messageLabel.setText("Form cleared.");
        messageLabel.setForeground(Color.BLUE);
    }
}
