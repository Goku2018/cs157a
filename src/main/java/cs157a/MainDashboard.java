package cs157a;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    private String userRole;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    //Backend Database Operations

    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BorrowRecordDAO borrowRecordDAO;
    private PaymentDAO paymentDAO;

    public MainDashboard(String role) {
        this.userRole = role;

        //Instantiate DAOs(Placeholder implementations)
        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        borrowRecordDAO = new BorrowRecordDAO();
        paymentDAO = new PaymentDAO();

        setTitle("Library Management System - Dashboard (" + role + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();

        //Books Menu
        JMenu booksMenu = new JMenu("Books");
        addMenuItem(booksMenu, "View All Books", "ViewBooks");
        addMenuItem(booksMenu, "Search Books", "SearchBooks");
        if(role.equalsIgnoreCase("staff")){
            booksMenu.addSeparator();
            addMenuItem(booksMenu, "Add New Book", "AddBook");
            addMenuItem(booksMenu, "Update Book", "UpdateBook");
            addMenuItem(booksMenu, "Delete Book", "DeleteBook");
        }
        menuBar.add(booksMenu);

        //Members Menu
        JMenu membersMenu = new JMenu("Members");
        if(role.equalsIgnoreCase("staff")){
            addMenuItem(membersMenu, "View All Members", "ViewMembers");
            addMenuItem(membersMenu, "Register Member", "RegisterMember");
            addMenuItem(membersMenu, "Update Member", "UpdateMember");
            addMenuItem(membersMenu, "Delete Member", "DeleteMember");
        }else{
            addMenuItem(membersMenu, "My Profile", "MyProfile");
        }
        menuBar.add(membersMenu);

        //Borrowing Menu
        JMenu borrowMenu = new JMenu("Borrowing");
        if(role.equalsIgnoreCase("staff")){
            addMenuItem(borrowMenu, "Check Out Book", "Checkout");
            addMenuItem(borrowMenu, "Return Book", "Return");
            addMenuItem(borrowMenu, "Active Borrowings", "ActiveBorrowings");

        }else{
            addMenuItem(borrowMenu, "My Borrowings", "MyBorrowings");
        }
        menuBar.add(borrowMenu);

        //Payments Menu
        JMenu paymentsMenu = new JMenu("Payments");
        if(role.equalsIgnoreCase("staff")){
            addMenuItem(paymentsMenu, "Process Fine Payment", "ProcessPayment");
            addMenuItem(paymentsMenu, "View Unpaid Fines", "UnpaidFines");

        }else{
            addMenuItem(paymentsMenu, "My Fines", "MyFines");
        }
        menuBar.add(paymentsMenu);

        //Account Menu
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");

        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);

        //Card Layout for Content Panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createWelcomePanel(), "Welcome");

        // Add real panels (pass DAOs they need)
        contentPanel.add(new ViewBooksPanel(bookDAO), "ViewBooks");
        contentPanel.add(new SearchBooksPanel(bookDAO), "SearchBooks");
        contentPanel.add(new AddBookPanel(bookDAO), "AddBook");
        contentPanel.add(new UpdateBookPanel(bookDAO), "UpdateBook");
        contentPanel.add(new DeleteBookPanel(bookDAO), "DeleteBook");
        contentPanel.add(new RegisterMemberPanel(userDAO), "RegisterMember");
        contentPanel.add(new ViewMembersPanel(userDAO),"ViewMembers");

        // Placeholder panels for other features
        // Placeholder panels for other features
        //contentPanel.add(createPlaceholderPanel("Update Book - Coming Soon"), "UpdateBook");
        //contentPanel.add(createPlaceholderPanel("Delete Book - Coming Soon"), "DeleteBook");
        //contentPanel.add(createPlaceholderPanel("View All Members - Coming Soon"), "ViewMembers");
        contentPanel.add(createPlaceholderPanel("Update Member - Coming Soon"), "UpdateMember");
        contentPanel.add(createPlaceholderPanel("Delete Member - Coming Soon"), "DeleteMember");
        contentPanel.add(createPlaceholderPanel("My Profile - Coming Soon"), "MyProfile");
        contentPanel.add(createPlaceholderPanel("Check Out Book - Coming Soon"), "Checkout");
        contentPanel.add(createPlaceholderPanel("Return Book - Coming Soon"), "Return");
        contentPanel.add(createPlaceholderPanel("Active Borrowings - Coming Soon"), "ActiveBorrowings");
        contentPanel.add(createPlaceholderPanel("My Borrowings - Coming Soon"), "MyBorrowings");
        contentPanel.add(createPlaceholderPanel("Process Fine Payment - Coming Soon"), "ProcessPayment");
        contentPanel.add(createPlaceholderPanel("View Unpaid Fines - Coming Soon"), "UnpaidFines");
        contentPanel.add(createPlaceholderPanel("My Fines - Coming Soon"), "MyFines");

        add(contentPanel);

        // Show default panel
        cardLayout.show(contentPanel, "Welcome");
    }

    private void addMenuItem(JMenu menu, String title, String panelName) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        menu.add(item);
    }

    private JPanel createPlaceholderPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    //Blank page
    private JPanel createWelcomePanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 150, 243));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Select an option from the MENU to begin");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        return panel;
    }
}