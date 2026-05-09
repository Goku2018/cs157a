package cs157a;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard window for the Library Management System.
 * Provides a menu-driven interface with CardLayout for switching between panels.
 * Accessible features depend on user role (staff or member).
 */
public class MainDashboard extends JFrame {
    private String userRole;
    private String userEmail;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private int loggedInUserId;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private BorrowRecordDAO borrowRecordDAO;
    private PaymentDAO paymentDAO;

    /**
     * Constructor - initializes the dashboard with role-specific panels.
     * @param role User role ("staff" or "member")
     * @param email User's email address
     * @param loggedInUserId User's unique identifier
     */
    public MainDashboard(String role, String email, int loggedInUserId) {
        this.userRole = role;
        this.userEmail = email;
        this.loggedInUserId = loggedInUserId;

        // Initialize Data Access Objects
        bookDAO = new BookDAO();
        userDAO = new UserDAO();
        borrowRecordDAO = new BorrowRecordDAO();
        paymentDAO = new PaymentDAO();

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

        //Staff panels
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

        // Member-specific panels (only visible to members)
        if (role.equalsIgnoreCase("member")) {
            contentPanel.add(new MyProfilePanel(userDAO, userEmail), "MyProfile");
            contentPanel.add(new MyBorrowingsPanel(borrowRecordDAO, bookDAO, userDAO, loggedInUserId), "MyBorrowings");
            contentPanel.add(new MyFinesPanel(borrowRecordDAO, bookDAO, paymentDAO, loggedInUserId), "MyFines");
        } else {
            // Placeholder panels for staff (these features are for members only)
            contentPanel.add(createPlaceholderPanel("My Profile - Staff View (Coming Soon)"), "MyProfile");
            contentPanel.add(createPlaceholderPanel("My Borrowings - Coming Soon"), "MyBorrowings");
            contentPanel.add(createPlaceholderPanel("My Fines - Coming Soon"), "MyFines");
        }
        add(contentPanel);
        cardLayout.show(contentPanel, "Welcome");
    }

    /**
     * Helper method to add a menu item that switches to a specific panel.
     * @param menu The menu to add the item to
     * @param title The menu item text
     * @param panelName The name of the panel in CardLayout
     */
    private void addMenuItem(JMenu menu, String title, String panelName) {
        JMenuItem item = new JMenuItem(title);
        item.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        menu.add(item);
    }

    /**
     * Creates a placeholder panel for features not yet implemented.
     * @param message The message to display
     * @return A JPanel with a centered gray message
     */
    private JPanel createPlaceholderPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.GRAY);
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Logs out the current user and returns to the login screen.
     * Shows a confirmation dialog before logging out.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    /**
     * Creates the welcome panel displayed when the dashboard first loads.
     * @return A JPanel with title and subtitle
     */
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