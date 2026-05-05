package cs157a;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

public class AddBookPanel extends JPanel{
    private BookDAO bookDAO;

    //Form fields
    private JTextField titleField;
    private JTextField authorField;
    private JTextField genreField;
    private JTextField isbnField;
    private JComboBox<String> statusCombo;
    private JLabel messageLabel;

    public AddBookPanel(BookDAO bookDAO){
        this.bookDAO = bookDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Title panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8,8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        //Row 0 is Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        titleField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        //Row 1 is Author
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Author:"), gbc);
        authorField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(authorField, gbc);

        //Row 2 is Genre
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Genre:"), gbc);
        genreField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(genreField, gbc);

        //Row 3 is ISBN
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("ISBN:"), gbc);
        isbnField = new JTextField(25);
        gbc.gridx = 1;
        formPanel.add(isbnField, gbc);

        //Row 4 is Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        String[] statuses = {"Available", "Borrowed", "Damaged", "Lost"};

        formPanel.add(new JLabel("Status:"), gbc);
        statusCombo = new JComboBox<>(statuses);
        gbc.gridx = 1;
        formPanel.add(statusCombo, gbc);

        //Row 5 is Buttons
        JPanel buttonPanel = new JPanel( new  FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton("Add Book");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        //Row 6 Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.BLUE);
        gbc.gridy = 6;
        formPanel.add(messageLabel, gbc);

        //Event handlers
        addButton.addActionListener(e->addBook());
        clearButton.addActionListener(e->clearForm("Form cleared."));
        add(formPanel, BorderLayout.CENTER);
    }

    private void addBook(){
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = genreField.getText().trim();
        String isbn = isbnField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if(title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty()){
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        //Create Book object
        Book book = new Book(title, author, genre, isbn, status);

        //Call try catch
        try{
            boolean success = bookDAO.addBook(book);
            if(success){
                messageLabel.setText("Book added successfully...");
                messageLabel.setForeground(Color.GREEN);
                clearForm("Book added successfully...");
            }else {
                messageLabel.setText("Failed to add book.  Try Again.");
                messageLabel.setForeground(Color.RED);
            }
        }catch(Exception ex){
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }

    }
    private void clearForm(String message){
        titleField.setText("");
        authorField.setText("");
        genreField.setText("");
        isbnField.setText("");
        statusCombo.setSelectedIndex(0);
        messageLabel.setText(message);
        messageLabel.setForeground(Color.BLUE);
    }
}
