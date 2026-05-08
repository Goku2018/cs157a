package cs157a;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    private String userRole;
    private String userEmail;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BorrowRecordDAO borrowRecordDAO;
    private PaymentDAO paymentDAO;

    public MainDashboard(String role, String email) {
        System.out.println("========== MAIN DASHBOARD CREATED ==========");
        System.out.println("Role: " + role);
        System.out.println("Email: " + email);

        this.userRole = role;
        this.userEmail = email;

        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        borrowRecordDAO = new BorrowRecordDAO();
        paymentDAO = new PaymentDAO();

        System.out.println("DAOs instantiated successfully");

        setTitle("Library Management System - Dashboard (" + role + ")");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();

        // Books Menu
        JMenu booksMenu = new JMenu("Books");
        addMenuItem(booksMenu, "View All Books", "ViewBooks");
        addMenuItem(booksMenu, "Search Books", "SearchBooks");
        if (role.equalsIgnoreCase("staff")) {
            booksMenu.addSeparator();
            addMenuItem(booksMenu, "Add New Book", "AddBook");
            addMenuItem(booksMenu, "Update Book", "UpdateBook");
            addMenuItem(booksMenu, "Delete Book", "DeleteBook");
        }
        menuBar.add(booksMenu);

        // Members Menu
        JMenu membersMenu = new JMenu("Members");
        if (role.equalsIgnoreCase("staff")) {
            addMenuItem(membersMenu, "View All Members", "ViewMembers");
            addMenuItem(membersMenu, "Register Member", "RegisterMember");
            addMenuItem(membersMenu, "Update Member", "UpdateMember");
            addMenuItem(membersMenu, "Delete Member", "DeleteMember");
        } else {
            addMenuItem(membersMenu, "My Profile", "MyProfile");
        }
        menuBar.add(membersMenu);

        // Borrowing Menu
        JMenu borrowMenu = new JMenu("Borrowing");
        if (role.equalsIgnoreCase("staff")) {
            addMenuItem(borrowMenu, "Check Out Book", "Checkout");
            addMenuItem(borrowMenu, "Return Book", "Return");
            addMenuItem(borrowMenu, "Active Borrowings", "ActiveBorrowings");
            addMenuItem(borrowMenu, "Borrow Records", "BorrowRecords");
        } else {
            addMenuItem(borrowMenu, "My Borrowings", "MyBorrowings");
        }
        menuBar.add(borrowMenu);

        // Payments Menu
        JMenu paymentsMenu = new JMenu("Payments");
        if (role.equalsIgnoreCase("staff")) {
            addMenuItem(paymentsMenu, "Process Fine Payment", "ProcessPayment");
            addMenuItem(paymentsMenu, "View Unpaid Fines", "UnpaidFines");
            addMenuItem(paymentsMenu, "Payment History", "PaymentHistory");
        } else {
            addMenuItem(paymentsMenu, "My Fines", "MyFines");
        }
        menuBar.add(paymentsMenu);

        // Account Menu
        JMenu accountMenu = new JMenu("Account");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        accountMenu.add(logoutItem);
        menuBar.add(accountMenu);

        setJMenuBar(menuBar);

        // Card Layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createWelcomePanel(), "Welcome");

        // Add real panels
        contentPanel.add(new ViewBooksPanel(bookDAO), "ViewBooks");
        contentPanel.add(new SearchBooksPanel(bookDAO), "SearchBooks");
        contentPanel.add(new AddBookPanel(bookDAO), "AddBook");
        contentPanel.add(new UpdateBookPanel(bookDAO), "UpdateBook");
        contentPanel.add(new DeleteBookPanel(bookDAO), "DeleteBook");
        contentPanel.add(new RegisterMemberPanel(userDAO), "RegisterMember");
        contentPanel.add(new ViewMembersPanel(userDAO), "ViewMembers");
        contentPanel.add(new UpdateMemberPanel(userDAO), "UpdateMember");
        contentPanel.add(new DeleteMemberPanel(userDAO), "DeleteMember");
        contentPanel.add(new CheckoutPanel(borrowRecordDAO, bookDAO, userDAO), "Checkout");
        contentPanel.add(new ReturnPanel(borrowRecordDAO, bookDAO), "Return");
        contentPanel.add(new ActiveBorrowingsPanel(borrowRecordDAO, bookDAO, userDAO), "ActiveBorrowings");
        contentPanel.add(new BorrowRecordsPanel(borrowRecordDAO, bookDAO, userDAO), "BorrowRecords");
        contentPanel.add(new ProcessPaymentPanel(paymentDAO, borrowRecordDAO), "ProcessPayment");
        contentPanel.add(new ViewUnpaidFinesPanel(borrowRecordDAO, bookDAO, userDAO, paymentDAO), "UnpaidFines");
        contentPanel.add(new PaymentHistoryPanel(paymentDAO, userDAO), "PaymentHistory");

        // MyProfile - only for members
        if (role.equalsIgnoreCase("member")) {
            System.out.println("Creating MyProfilePanel for member: " + userEmail);
            try {
                contentPanel.add(new MyProfilePanel(userDAO, userEmail), "MyProfile");
                System.out.println("MyProfilePanel created successfully");
            } catch (Exception ex) {
                System.out.println("ERROR creating MyProfilePanel:");
                ex.printStackTrace();
                contentPanel.add(createPlaceholderPanel("My Profile - Error Loading"), "MyProfile");
            }
        } else {
            contentPanel.add(createPlaceholderPanel("My Profile - Staff View (Coming Soon)"), "MyProfile");
        }

        // Placeholder panels
        contentPanel.add(createPlaceholderPanel("My Borrowings - Coming Soon"), "MyBorrowings");
        contentPanel.add(createPlaceholderPanel("My Fines - Coming Soon"), "MyFines");

        add(contentPanel);
        cardLayout.show(contentPanel, "Welcome");

        System.out.println("MainDashboard initialization complete");
        System.out.println("=======================================\n");
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

    private JPanel createWelcomePanel() {
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