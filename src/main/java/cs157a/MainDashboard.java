package cs157a;

import javax.swing.*;

public class MainDashboard extends JFrame {
    private String userRole;

    public MainDashboard(String role) {
        this.userRole = role;

        setTitle("Library Management System - Dashboard (" + role + ")");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Simple welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Library Management System!", SwingConstants.CENTER);
        welcomeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));

        JLabel roleLabel = new JLabel("You are logged in as: " + role, SwingConstants.CENTER);
        roleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));

        JPanel panel = new JPanel(new java.awt.GridLayout(2, 1));
        panel.add(welcomeLabel);
        panel.add(roleLabel);

        add(panel);
    }
}