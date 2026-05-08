package cs157a;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.time.LocalDate;

public class ReturnPanel extends JPanel{
    private BorrowRecordDAO borrowRecordDAO;
    private BookDAO bookDAO;

    //Form fields
    private JTextField recordIdField;
    private JLabel bookTitleLabel;
    private JLabel memberNameLabel;
    private JLabel borrowDateLabel;
    private JLabel dueDateLabel;
    private JLabel fineAmountLabel;
    private JLabel statusLabel;

    public ReturnPanel(BorrowRecordDAO borrowRecordDAO, BookDAO bookDAO){
        this.borrowRecordDAO = borrowRecordDAO;
        this.bookDAO = bookDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        //Row 0 Borrow Record ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Borrow  Record ID:"), gbc);
        recordIdField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(recordIdField, gbc);

        JButton lookupButton = new JButton("Lookup Record");
        gbc.gridx = 2;
        formPanel.add(lookupButton, gbc);

        //Row 1 Book Title display
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Book Title: "), gbc);
        bookTitleLabel = new JLabel(" ");
        bookTitleLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(bookTitleLabel, gbc);
        gbc.gridwidth = 1;

        //Row 2 Member Name display
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Member Name: "), gbc);
        memberNameLabel = new JLabel(" ");
        memberNameLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(memberNameLabel, gbc);

        //Row 3 Borrow Date display
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Borrow Date: "), gbc);
        borrowDateLabel = new JLabel(" ");
        borrowDateLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(borrowDateLabel, gbc);

        //Row 4 Due Date display
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Due Date: "), gbc);
        dueDateLabel = new JLabel(" ");
        dueDateLabel.setForeground(Color.BLUE);
        gbc.gridx = 1;
        formPanel.add(dueDateLabel, gbc);

        //Row 5 Fine Amount display
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Fine Amount: "), gbc);
        fineAmountLabel = new JLabel(" ");
        fineAmountLabel.setFont(new Font("Arial", Font.BOLD, 14)) ;
        gbc.gridx = 1;
        formPanel.add(fineAmountLabel, gbc);

        //Row 6 Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton returnButton = new JButton("Process Return");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(returnButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        //Row 7 Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        gbc.gridy = 7;
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        //Event handlers
        lookupButton.addActionListener(e-> lookupRecord());
        returnButton.addActionListener(e-> processRecord());
        clearButton.addActionListener(e-> clearForm());
    }

    private void lookupRecord(){
        String recordIdStr = recordIdField.getText().trim();
        if(recordIdStr.isEmpty()){
            statusLabel.setText("Please enter Borrow ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }
        try{
            long recordId = Long.parseLong(recordIdStr);

            //Get borrow record details from DAO
            BorrowRecord record = borrowRecordDAO.getBorrowRecordById(recordId);

            if(record == null){
                statusLabel.setText("Borrow record not found.");
                statusLabel.setForeground(Color.RED);
                clearDisplay();
                return;
            }
            if(record.getReturnDate() != null){
                statusLabel.setText("This book has been returned.");
                statusLabel.setForeground(Color.RED);
                return;
            }

            //Get book details
            java.util.List<Book> books = bookDAO.getAllBooks();
            Book book = null;
            for(Book b: books){
                if(b.getBookId() == record.getBookId()){
                    book = b;
                    break;
                }
            }
            if(book != null){
                bookTitleLabel.setText(book.getTitle());
            }

            //Get members details
            memberNameLabel.setText("Member ID: " + record.getUserId());

            //Display dates
            if(record.getBorrowDate() != null){
                borrowDateLabel.setText(record.getBorrowDate().toString());
            }
            if(record.getDueDate() != null){
                dueDateLabel.setText(record.getDueDate().toString());
            }

            // FineAmount is refreshed and capped by BorrowRecordDAO before the record is returned.
            double fine = record.getFineAmount();
            if(fine > 0){
                fineAmountLabel.setText("$" + String.format("%.2f", fine));
                fineAmountLabel.setForeground(Color.RED);
            } else{
                fineAmountLabel.setText("$0.00 (No Fine)");
                fineAmountLabel.setForeground(Color.GREEN);
            }

            statusLabel.setText("Record found. Click 'Process Return' to complete.");
            statusLabel.setForeground(Color.GREEN);

        } catch (NumberFormatException ex){
            statusLabel.setText("Invalid ID.  Please enter a number.");
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        } catch(Exception ex){
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }
    private void processRecord(){
        String recordIdStr = recordIdField.getText().trim();
        if(recordIdStr.isEmpty()){
            statusLabel.setText("Please enter an ID.");
            statusLabel.setForeground(Color.RED);
            return;
        }
        try{
            long recordId = Long.parseLong(recordIdStr);

            //Call backend to process return
            boolean success = borrowRecordDAO.returnBook(recordId);

            if(success){
                statusLabel.setText("Book returned successfully.");
                statusLabel.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(this, "Book has been returned.\nFine amount has been calculated.", "Return Successful", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else{
                statusLabel.setText("Return failed.");
                statusLabel.setForeground(Color.RED);
            }
        } catch (Exception ex){
            statusLabel.setText("Error: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearDisplay(){
        bookTitleLabel.setText(" ");
        memberNameLabel.setText(" ");
        borrowDateLabel.setText(" ");
        dueDateLabel.setText(" ");
        fineAmountLabel.setText(" ");
    }

    private void clearForm(){
        recordIdField.setText("");
        clearDisplay();
        statusLabel.setText("Form cleared.");
        statusLabel.setForeground(Color.BLUE);
    }

}
