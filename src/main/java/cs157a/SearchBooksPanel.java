package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
