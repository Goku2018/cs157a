package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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
        String[] columns = {"Book ID", "Titkle", "Author", "Genre", "ISBN", "Status"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false; // read-only
            }
        }
        }
    }



