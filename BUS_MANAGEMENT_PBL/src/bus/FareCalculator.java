package bus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FareCalculator {
    private static final String STOP_TIMES_FILE = "src/bus/stop_times.txt";
    private static final int PRICE_PER_KM = 5;

    public static int calculateFare(String tripId, int sourceStopId, int destinationStopId) {
        double sourceDistance = -1;
        double destinationDistance = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(STOP_TIMES_FILE))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String currentTripId = parts[0].trim();
                int stopId = Integer.parseInt(parts[3].trim());
                double distance = Double.parseDouble(parts[9].trim());

                if (currentTripId.equals(tripId)) {
                    if (stopId == sourceStopId) {
                        sourceDistance = distance;
                    } else if (stopId == destinationStopId) {
                        destinationDistance = distance;
                    }
                }

                if (sourceDistance != -1 && destinationDistance != -1) {
                    break;
                }
            }

            if (sourceDistance == -1 || destinationDistance == -1) {
                System.out.println("Stop ID(s) not found for trip: " + tripId);
                return -1;
            }

            double fare = Math.abs(destinationDistance - sourceDistance) * PRICE_PER_KM;
            return (int) fare;

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading stop_times.txt: " + e.getMessage());
            return -1;
        }
    }
}
