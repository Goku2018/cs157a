package cs157a;

import javax.swing.*;
import java.awt.*;

public class UpdateBookPanel extends JPanel {
    private BookDAO bookDAO;
    private JTextField bookIdField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField genreField;
    private JTextField isbnField;
    private JComboBox<String> statusCombo;
    private JLabel messageLabel;

    public UpdateBookPanel(BookDAO bookDAO) {
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

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(titleField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Author:"), gbc);
        authorField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(authorField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Genre:"), gbc);
        genreField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(genreField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("ISBN:"), gbc);
        isbnField = new JTextField(25);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(isbnField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Status:"), gbc);
        String[] statuses = {"Available", "Borrowed", "Damaged", "Lost"};
        statusCombo = new JComboBox<>(statuses);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(statusCombo, gbc);
        gbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton updateButton = new JButton("Update Book");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(updateButton);
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
        updateButton.addActionListener(e -> updateBook());
        clearButton.addActionListener(e -> clearForm());
        bookIdField.addActionListener(e -> loadBook());
    }

    private void loadBook() {
        Integer bookId = readBookId();
        if(bookId == null){
            return;
        }

        try{
            Book book = bookDAO.getBookById(bookId);
            if(book == null){
                messageLabel.setText("No book found with ID " + bookId + ".");
                messageLabel.setForeground(Color.RED);
                clearBookFields();
                return;
            }

            titleField.setText(book.getTitle());
            authorField.setText(book.getAuthor());
            genreField.setText(book.getGenre());
            isbnField.setText(book.getIsbn());
            statusCombo.setSelectedItem(book.getStatus());
            messageLabel.setText("Book loaded. Edit the fields and click Update Book.");
            messageLabel.setForeground(Color.BLUE);
        } catch(Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        Integer bookId = readBookId();
        if(bookId == null){
            return;
        }

        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String isbn = isbnField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if(title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty()){
            messageLabel.setText("Please fill in all fields before updating.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        Book book = new Book(bookId, title, author, genre, isbn, status);

        try{
            boolean success = bookDAO.updateBook(book);
            if(success){
                messageLabel.setText("Book updated successfully.");
                messageLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this, "Book " + bookId + " has been updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else{
                messageLabel.setText("Update failed. Book ID may not exist.");
                messageLabel.setForeground(Color.RED);
            }
        } catch(Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    private void clearBookFields() {
        titleField.setText("");
        authorField.setText("");
        genreField.setText("");
        isbnField.setText("");
        statusCombo.setSelectedIndex(0);
    }

    private void clearForm() {
        bookIdField.setText("");
        clearBookFields();
        messageLabel.setText("Form cleared.");
        messageLabel.setForeground(Color.BLUE);
    }
}