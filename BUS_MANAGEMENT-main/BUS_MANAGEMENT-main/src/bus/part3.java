package bus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class part3 {

	public static int[] dijkstra(List<ArrayList<int[]>> graph, int start, int[] prev) {
		int n = graph.size();
		int[] dist = new int[n];
		Arrays.fill(dist, Integer.MAX_VALUE);
		dist[start] = 0;

		PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
		pq.add(new int[]{start, 0});

		while (!pq.isEmpty()) {
			int[] current = pq.poll();
			int u = current[0];
			int d = current[1];

			if (d > dist[u]) continue;

			for (int[] neighbor : graph.get(u)) {
				int v = neighbor[0];
				int weight = neighbor[1];
				if (dist[u] + weight < dist[v]) {
					dist[v] = dist[u] + weight;
					prev[v] = u;
					pq.add(new int[]{v, dist[v]});
				}
			}
		}
		return dist;
	}

	public static List<Integer> reconstructPath(int target, int[] prev) {
		List<Integer> path = new ArrayList<>();
		for (int at = target; at != -1; at = prev[at]) {
			path.add(at);
		}
		Collections.reverse(path);
		return path;
	}

	public static int getTimeDifference(String departure, String arrival) {
		String[] dep = departure.split(":");
		String[] arr = arrival.split(":");
		int depMinutes = Integer.parseInt(dep[0]) * 60 + Integer.parseInt(dep[1]);
		int arrMinutes = Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
		return arrMinutes - depMinutes;
	}

	public static Integer getShapeDistance(String tripId, int stopId) {
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\onepl\\Downloads\\BUS_MANAGEMENT-main\\BUS_MANAGEMENT-main\\src\\bus\\stop_times.txt"))) {
			br.readLine(); // skip header
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 9 && parts[0].equals(tripId) && Integer.parseInt(parts[3]) == stopId) {
					return Integer.parseInt(parts[8]); // shape_dist_traveled
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static int getTimeDifference(String tripId, int fromStopId, int toStopId) {
		String stopTimesFile;
		try {
			File file = new File(part3.class.getResource("/bus/stop_times.txt").toURI());
			stopTimesFile = file.getAbsolutePath();
		} catch (URISyntaxException | NullPointerException e) {
			System.err.println("Error locating stop_times.txt: " + e.getMessage());
			return -1;
		}

		String departureTime = null;
		String arrivalTime = null;
		boolean foundFrom = false;

		try (BufferedReader br = new BufferedReader(new FileReader(stopTimesFile))) {
			br.readLine(); // Skip header
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length < 4) continue;

				String tId = parts[0];
				String arrTime = parts[1];
				String depTime = parts[2];
				int stopId = Integer.parseInt(parts[3]);

				if (!tId.equals(tripId)) continue;

				if (stopId == fromStopId) {
					departureTime = depTime;
					foundFrom = true;
				}

				if (stopId == toStopId && foundFrom) {
					arrivalTime = arrTime;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (departureTime != null && arrivalTime != null) {
			return getTimeDifference(departureTime, arrivalTime);
		}

		return -1;
	}
}
