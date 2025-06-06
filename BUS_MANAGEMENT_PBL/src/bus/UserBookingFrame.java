package bus;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class UserBookingFrame extends JFrame {
    private JTextField sourceField, destinationField, passengersField, tripIdField;
    private JLabel priceLabel;
    private int lastCalculatedFare = -1;

    public UserBookingFrame(String username) {
        setTitle("Book a Ticket");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Trip ID:"));
        tripIdField = new JTextField();
        add(tripIdField);

        add(new JLabel("Source Stop ID:"));
        sourceField = new JTextField();
        add(sourceField);

        add(new JLabel("Destination Stop ID:"));
        destinationField = new JTextField();
        add(destinationField);

        add(new JLabel("Number of Passengers:"));
        passengersField = new JTextField();
        add(passengersField);

        JButton calculateButton = new JButton("Calculate Price");
        add(calculateButton);

        priceLabel = new JLabel("Total Price: ");
        add(priceLabel);

        JButton bookButton = new JButton("Book Ticket");
        add(bookButton);

        calculateButton.addActionListener(e -> calculatePrice());
        bookButton.addActionListener(e -> bookTicket(username));

        setVisible(true);
    }

    private void calculatePrice() {
        try {
            String tripId = tripIdField.getText().trim();
            int source = Integer.parseInt(sourceField.getText().trim());
            int dest = Integer.parseInt(destinationField.getText().trim());
            int passengers = Integer.parseInt(passengersField.getText().trim());

            int farePerPassenger = FareCalculator.calculateFare(tripId, source, dest);

            if (farePerPassenger == -1) {
                priceLabel.setText("No valid trip/stop combination.");
                lastCalculatedFare = -1;
            } else {
                lastCalculatedFare = farePerPassenger * passengers;
                priceLabel.setText("Total Price: " + lastCalculatedFare + " units");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter correct values.");
        }
    }

    private void bookTicket(String username) {
        try {
            String tripId = tripIdField.getText().trim();
            int source = Integer.parseInt(sourceField.getText().trim());
            int dest = Integer.parseInt(destinationField.getText().trim());
            int passengers = Integer.parseInt(passengersField.getText().trim());

            int farePerPassenger;
            if (lastCalculatedFare != -1) {
                farePerPassenger = lastCalculatedFare / passengers;
            } else {
                farePerPassenger = FareCalculator.calculateFare(tripId, source, dest);
                if (farePerPassenger == -1) {
                    JOptionPane.showMessageDialog(this, "Cannot book: invalid trip or stops.");
                    return;
                }
            }

            int totalFare = farePerPassenger * passengers;

            // Save booking
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/bus/bookings.txt", true));
            writer.write(username + "," + tripId + "," + source + "," + dest + "," + passengers + "," + totalFare + ",pending\n");
            writer.close();

            JOptionPane.showMessageDialog(this, "Booking submitted! Awaiting admin confirmation.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during booking. Please check your inputs.");
        }
    }
}
