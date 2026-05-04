package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.JOptionPane;

public class SearchBooksPanel extends JPanel {
    private BookDAO bookDAO;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public SearchBooksPanel(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel: search controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        topPanel.add(new JLabel("Search by:"));
        String[] searchTypes = {"Title", "Author", "Genre"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        topPanel.add(new JLabel("Keyword:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        topPanel.add(searchButton);

        JButton clearButton = new JButton("Clear");
        topPanel.add(clearButton);

        add(topPanel, BorderLayout.NORTH);

        //Center panel: results table
        String[] columns = {"Book ID", "Title", "Author", "Genre", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false; // read-only
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        //Bottom Panel: Status Label
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Enter a keyword and click Search");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        //Event Handleers
        searchButton.addActionListener(e->performSearch());
        clearButton.addActionListener(e->clearSearch());
        searchField.addActionListener(e->performSearch()); //Trigger search

        }
        private void performSearch(){
            String keyword = searchField.getText().trim();
            if(keyword.isEmpty()){
                statusLabel.setText("Please enter a keyword to search");
                return;
            }
            String searchType = (String) searchTypeCombo.getSelectedItem();
            statusLabel.setText("Searching for " + searchType + " containing '" + keyword + "'...");

            //Clear results
            tableModel.setRowCount(0);

            //Call backend with try catch
            try{
                List<Book> results = bookDAO.searchBooks(keyword, searchType.toLowerCase());

                if(results == null || results.isEmpty()){
                    statusLabel.setText("No books found matching '" + keyword + "' in " + searchType);

                }else{
                    for(Book book : results){
                        Object[] row =  {
                                book.getBookId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getGenre(),
                                book.getIsbn(),
                                book.getStatus()
                        };
                        tableModel.addRow(row);
                    }
                    statusLabel.setText("Found " + results.size() + " book(s).");
                }
            } catch(Exception ex){
                statusLabel.setText("Search failed.");
                JOptionPane.showMessageDialog(this, "Error performing search: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();

            }
        }
        private void clearSearch(){
            searchField.setText("");
            tableModel.setRowCount(0);
            statusLabel.setText("Search cleared. Enter a keyword and click Search.");
        }
    }

