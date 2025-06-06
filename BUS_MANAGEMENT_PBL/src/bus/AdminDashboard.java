package bus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        setTitle("Admin Dashboard - Bus Management System");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Routes Tab
        JPanel routePanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton viewRoutesBtn = new JButton("View All Routes");
        JButton addRouteBtn = new JButton("Add New Route");
        JButton manageStopsBtn = new JButton("Manage Stops");
        routePanel.add(viewRoutesBtn);
        routePanel.add(addRouteBtn);
        routePanel.add(manageStopsBtn);
        tabbedPane.add("Routes", routePanel);

        // Bookings Tab
        JPanel bookingsPanel = new JPanel(new BorderLayout());
        JButton viewBookingsBtn = new JButton("View All Bookings");
        bookingsPanel.add(viewBookingsBtn, BorderLayout.CENTER);
        tabbedPane.add("Bookings", bookingsPanel);

        // Logout Tab
        JPanel logoutPanel = new JPanel(new FlowLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutPanel.add(logoutBtn);
        tabbedPane.add("Logout", logoutPanel);

        add(tabbedPane);

        viewRoutesBtn.addActionListener(e -> showRoutes());
        addRouteBtn.addActionListener(e -> addNewRoute());
        manageStopsBtn.addActionListener(e -> manageStops());
        viewBookingsBtn.addActionListener(e -> viewAllBookings());

        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }

    private void showRoutes() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/bus/stop.txt"))) {
            StringBuilder routes = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                routes.append(line).append("\n");
            }
            JOptionPane.showMessageDialog(this, "All Routes:\n" + routes.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading stop.txt: " + ex.getMessage());
        }
    }

    private void addNewRoute() {
        JTextField routeField = new JTextField();

        Object[] message = {
                "Enter new route (e.g., Delhi -> Jaipur | Bus No: RJ123):", routeField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Route", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String route = routeField.getText().trim();
            if (!route.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bus/stop.txt", true))) {
                    writer.write(route);
                    writer.newLine();
                    JOptionPane.showMessageDialog(this, "Route added successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error writing to stop.txt: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Route cannot be empty.");
            }
        }
    }

    private void manageStops() {
        File stopFile = new File("src/bus/stop.txt");
        if (!stopFile.exists()) {
            JOptionPane.showMessageDialog(this, "No stop.txt file found.");
            return;
        }

        try {
            DefaultListModel<String> stopListModel = new DefaultListModel<>();
            BufferedReader reader = new BufferedReader(new FileReader(stopFile));
            String line;
            while ((line = reader.readLine()) != null) {
                stopListModel.addElement(line);
            }
            reader.close();

            JList<String> stopList = new JList<>(stopListModel);
            stopList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane scrollPane = new JScrollPane(stopList);
            JButton deleteBtn = new JButton("Delete Selected Stop");
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(deleteBtn, BorderLayout.SOUTH);

            JFrame manageFrame = new JFrame("Manage Stops");
            manageFrame.setSize(400, 300);
            manageFrame.setLocationRelativeTo(null);
            manageFrame.add(panel);
            manageFrame.setVisible(true);

            deleteBtn.addActionListener(e -> {
                int selectedIdx = stopList.getSelectedIndex();
                if (selectedIdx >= 0) {
                    stopListModel.remove(selectedIdx);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(stopFile))) {
                        for (int i = 0; i < stopListModel.size(); i++) {
                            writer.write(stopListModel.get(i));
                            writer.newLine();
                        }
                        JOptionPane.showMessageDialog(manageFrame, "Stop deleted successfully.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(manageFrame, "Error updating stop.txt: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(manageFrame, "Please select a stop to delete.");
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading stop.txt: " + ex.getMessage());
        }
    }

    private void viewAllBookings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/bus/bookings.txt"))) {
            StringBuilder bookings = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                bookings.append(line).append("\n");
            }
            if (bookings.length() == 0) {
                JOptionPane.showMessageDialog(this, "No bookings found.");
            } else {
                JOptionPane.showMessageDialog(this, "All Bookings:\n" + bookings.toString());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading bookings.txt: " + ex.getMessage());
        }
    }
}
