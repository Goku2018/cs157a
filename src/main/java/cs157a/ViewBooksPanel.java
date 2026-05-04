package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewBooksPanel extends JPanel {
    private BookDAO bookDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewBooksPanel(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
        setLayout(new BorderLayout());

        // Column names
        String[] columns = {"Book ID", "Title", "Author", "Genre", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        refreshTable();
    }
    private void refreshTable(){
        tableModel.setRowCount(0); // clear existing rows
        List<Book> books = bookDAO.getAllBooks();  // calls backend

        try {
            for (Book b : books) {
                Object[] row = {
                        b.getBookId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getGenre(),
                        b.getIsbn(),
                        b.getStatus()
                };
                tableModel.addRow(row);
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

