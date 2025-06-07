package bus;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private JTable bookingsTable;

    public AdminDashboard() {
        setTitle("Admin Dashboard - Bus Management System");
        setSize(700, 500);
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

        // Bookings Tab with JTable inside tab directly
        String[] columnNames = {"Username", "Trip ID", "Source", "Destination", "Bus No", "Fare"};
        bookingsTable = new JTable();
        JScrollPane bookingScrollPane = new JScrollPane(bookingsTable);
        JPanel bookingsPanel = new JPanel(new BorderLayout());
        bookingsPanel.add(bookingScrollPane, BorderLayout.CENTER);

        tabbedPane.add("Bookings", bookingsPanel);

        // Logout Tab
        JPanel logoutPanel = new JPanel(new FlowLayout());
        JButton logoutBtn = new JButton("Logout");
        logoutPanel.add(logoutBtn);
        tabbedPane.add("Logout", logoutPanel);

        add(tabbedPane);

        // Button listeners
        viewRoutesBtn.addActionListener(e -> showRoutes());
        addRouteBtn.addActionListener(e -> addNewRoute());
        manageStopsBtn.addActionListener(e -> manageStops());

        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        // Load bookings data into the table on startup
        loadBookings();

        setVisible(true);
    }

    private void loadBookings() {
        File bookingFile = new File("src/bus/bookings.txt");
        if (!bookingFile.exists()) {
            JOptionPane.showMessageDialog(this, "No bookings.txt file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(bookingFile))) {
            ArrayList<String[]> rows = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 6);
                if (parts.length >= 6) {
                    String userName = parts[0].trim();
                    String tripId = parts[1].trim();
                    String busNo = parts[2].trim();
                    String source = parts[3].trim();
                    String destination = parts[4].trim();
                    String fare = parts[5].trim();
                    String[] row = {userName, tripId,busNo, source, destination, fare};
                    rows.add(row);
                }
            }

            String[] columnNames = {"Username", "Trip ID","Bus No", "Source", "Destination", "Fare"};
            String[][] data = rows.toArray(new String[0][]);

            bookingsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            bookingsTable.setAutoCreateRowSorter(true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading bookings.txt: " + ex.getMessage());
        }
    }

    private void showRoutes() {
        File stopFile = new File("src/bus/stop.txt");
        if (!stopFile.exists()) {
            JOptionPane.showMessageDialog(this, "No stop.txt file found.");
            return;
        }

        try {
            DefaultListModel<String> routeListModel = new DefaultListModel<>();
            BufferedReader reader = new BufferedReader(new FileReader(stopFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 4);
                if (parts.length >= 3) {
                    // Format: "stop_id  stop_code  stop_name"
                    String formatted = String.format("%-10s %-10s %s", parts[0].trim(), parts[1].trim(), parts[2].trim());
                    routeListModel.addElement(formatted);
                }
            }
            reader.close();

            JList<String> routeList = new JList<>(routeListModel);
            routeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(routeList);

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            JFrame routeFrame = new JFrame("View Routes");
            routeFrame.setSize(500, 400);
            routeFrame.setLocationRelativeTo(null);
            routeFrame.add(panel);
            routeFrame.setVisible(true);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading stop.txt: " + ex.getMessage());
        }
    }


    private void addNewRoute() {
        JTextField stopIdField = new JTextField();
        JTextField stopCodeField = new JTextField();
        JTextField stopNameField = new JTextField();

        Object[] message = {
                "Enter Stop ID:", stopIdField,
                "Enter Stop Code:", stopCodeField,
                "Enter Stop Name:", stopNameField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Route", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String stopId = stopIdField.getText().trim();
            String stopCode = stopCodeField.getText().trim();
            String stopName = stopNameField.getText().trim();

            if (!stopId.isEmpty() && !stopCode.isEmpty() && !stopName.isEmpty()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bus/stop.txt", true))) {
                    String formattedLine = stopId + "," + stopCode + "," + stopName;
                    writer.write(formattedLine);
                    writer.newLine();
                    JOptionPane.showMessageDialog(this, "Route added successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error writing to stop.txt: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "All fields are required.");
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
            java.util.List<String> fullLines = new java.util.ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(stopFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                fullLines.add(line); // save original line

                String[] parts = line.split(",", 4);
                if (parts.length >= 3) {
                    String formatted = String.format("%-10s %-10s %-30s", parts[0].trim(), parts[1].trim(), parts[2].trim());
                    stopListModel.addElement(formatted);
                }
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
            manageFrame.setSize(500, 350);
            manageFrame.setLocationRelativeTo(null);
            manageFrame.add(panel);
            manageFrame.setVisible(true);

            deleteBtn.addActionListener(e -> {
                int selectedIdx = stopList.getSelectedIndex();
                if (selectedIdx >= 0) {
                    // Confirm deletion
                    int confirm = JOptionPane.showConfirmDialog(manageFrame, "Are you sure you want to delete this stop?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        stopListModel.remove(selectedIdx);
                        fullLines.remove(selectedIdx);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(stopFile))) {
                            for (String remainingLine : fullLines) {
                                writer.write(remainingLine);
                                writer.newLine();
                            }
                            JOptionPane.showMessageDialog(manageFrame, "Stop deleted successfully.");
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(manageFrame, "Error updating stop.txt: " + ex.getMessage());
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(manageFrame, "Please select a stop to delete.");
                }
            });
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading stop.txt: " + ex.getMessage());
        }
    }
}
