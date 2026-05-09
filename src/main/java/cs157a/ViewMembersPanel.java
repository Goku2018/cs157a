package cs157a;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying all members in the library system.
 * Shows user ID, name, email, phone, address, status, and registration date.
 * Auto-refreshes when the panel becomes visible.
 */
public class ViewMembersPanel extends JPanel {
    private UserDAO userDAO;
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewMembersPanel(UserDAO userDAO) {
        this.userDAO = userDAO;
        setLayout(new BorderLayout());

        // Column names
        String[] columns = {"User ID", "Full Name", "Email", "Phone", "Address", "Status", "Registration Date"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        addHierarchyListener(e -> {
            if(isShowing()) {
                refreshTable();
            }
        });

        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        refreshTable();
    }

    /**
     * Refreshes the member table by loading all members from the database.
     * Clears existing rows and repopulates with current data.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // clear existing rows
        List<User> members = userDAO.getAllMembers();  // calls backend

        try {
            for (User u : members) {
                Object[] row = {
                        u.getUserId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone() != null ? u.getPhone() : "",
                        u.getAddress() != null ? u.getAddress() : "",
                        u.getStatus(),
                        u.getRegistrationDate() != null ? u.getRegistrationDate().toString() : ""
                };
                tableModel.addRow(row);
            }
        }catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

