package bus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class UserDashboard extends JFrame {
    public UserDashboard() {
        setTitle("User Dashboard - Bus Management System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Welcome, User", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton searchBusBtn = new JButton("Search Buses");
        JButton bookTicketBtn = new JButton("Book Ticket");
        JButton logoutBtn = new JButton("Logout");

        panel.add(searchBusBtn);
        panel.add(bookTicketBtn);
        panel.add(logoutBtn);

        add(panel, BorderLayout.CENTER);

        searchBusBtn.addActionListener(e -> showBusSearchDialog());
        bookTicketBtn.addActionListener(e -> showTicketBookingDialog());

        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }

    private void showBusSearchDialog() {
        JTextField sourceField = new JTextField();
        JTextField destinationField = new JTextField();

        Object[] message = {
                "Source:", sourceField,
                "Destination:", destinationField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Search Buses", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String source = sourceField.getText().trim();
            String destination = destinationField.getText().trim();

            try (BufferedReader reader = new BufferedReader(new FileReader("src/bus/stop.txt"))) {
                String line;
                StringBuilder result = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains(source.toLowerCase()) && line.toLowerCase().contains(destination.toLowerCase())) {
                        result.append(line).append("\n");
                    }
                }
                if (result.length() == 0) {
                    JOptionPane.showMessageDialog(this, "No buses found for given route.");
                } else {
                    JOptionPane.showMessageDialog(this, "Available Buses:\n" + result.toString());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error reading stop data: " + ex.getMessage());
            }
        }
    }


    private void showTicketBookingDialog() {
        JTextField nameField = new JTextField();
        JTextField busRouteField = new JTextField();
        JTextField seatsField = new JTextField();

        Object[] inputs = {
                "Your Name:", nameField,
                "Bus Route:", busRouteField,
                "Number of Seats:", seatsField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "Book Ticket", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String route = busRouteField.getText().trim();
            String seats = seatsField.getText().trim();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/bus/bookings.txt", true))) {
                writer.write(name + "," + route + "," + seats);
                writer.newLine();
                JOptionPane.showMessageDialog(this, "Ticket booked successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving booking: " + e.getMessage());
            }
        }
    }
}