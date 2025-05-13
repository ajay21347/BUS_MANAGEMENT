package bus;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class part1 {

	static Map<Integer, String> stopIdToName = new HashMap<>();
	static Map<Integer, String[]> stopIdToCoordinates = new HashMap<>();
	static ArrayList<ArrayList<int[]>> graph = new ArrayList<>();

	public static void main(String[] args) {
		part2 p2Instance = new part2();
		part2.TernarySearchTree tst = p2Instance.new TernarySearchTree();

		try {
			loadStops("src\\bus\\stop.txt", tst);
			loadGraph("src\\bus\\stop_times.txt", "src\\bus\\transfers.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Bus Management System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().setBackground(new Color(135, 206, 235));

		JLabel tableHeading = new JLabel("Bus Management System", SwingConstants.CENTER);
		tableHeading.setFont(new Font("Arial", Font.BOLD, 20));

		String[] columnNames = { "Order", "Stop ID", "Stop Name", "Time Taken" };
		JTable table = new JTable(new String[0][4], columnNames);
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel.setOpaque(false);
		topPanel.add(Box.createVerticalStrut(10));
		topPanel.add(tableHeading);
		topPanel.add(Box.createVerticalStrut(10));
		topPanel.add(scrollPane);

		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		inputPanel.setOpaque(false);

		JTextField sourceField = new JTextField(10);
		JTextField destField = new JTextField(10);
		JButton showButton = new JButton("Show");
		JLabel totalTimeLabel = new JLabel("Total Time: N/A");

		inputPanel.add(new JLabel("Start:"));
		inputPanel.add(sourceField);
		inputPanel.add(new JLabel("Destination:"));
		inputPanel.add(destField);
		inputPanel.add(showButton);
		inputPanel.add(totalTimeLabel); // Add label beside Show button

		frame.add(topPanel, BorderLayout.CENTER);
		frame.add(inputPanel, BorderLayout.SOUTH);

		showButton.addActionListener((ActionEvent e) -> {
			String fromInput = sourceField.getText().trim();
			String toInput = destField.getText().trim();

			try {
				int from = parseStopInput(fromInput, frame);
				int to = parseStopInput(toInput, frame);

				int[] prev = new int[graph.size()];
				Arrays.fill(prev, -1);

				int[] dist = part3.dijkstra(graph, from, prev);

				if (to >= dist.length || dist[to] == Integer.MAX_VALUE) {
					table.setModel(new javax.swing.table.DefaultTableModel(
							new String[][] { { "No path found", "", "", "" } }, columnNames));
					totalTimeLabel.setText("Total Time: N/A");
					return;
				}

				List<Integer> path = part3.reconstructPath(to, prev);
				String[][] tableData = new String[path.size()][4];

				for (int i = 0; i < path.size(); i++) {
					int id = path.get(i);
					String timeTaken = dist[id] + " minutes";
					tableData[i][0] = String.valueOf(i + 1);
					tableData[i][1] = String.valueOf(id);
					tableData[i][2] = stopIdToName.getOrDefault(id, "Unknown");
					tableData[i][3] = timeTaken;
				}

				table.setModel(new javax.swing.table.DefaultTableModel(tableData, columnNames));
				totalTimeLabel.setText("Total Time: " + dist[to] + " minutes");

			} catch (Exception ex) {
				table.setModel(new javax.swing.table.DefaultTableModel(
						new String[][] { { "Error: " + ex.getMessage(), "", "", "" } }, columnNames));
				totalTimeLabel.setText("Total Time: N/A");
			}
		});

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static void loadGraph(String stopTimesFile, String transfersFile) {
		int maxStopId = Collections.max(stopIdToName.keySet());
		for (int i = 0; i <= maxStopId; i++) {
			graph.add(new ArrayList<>());
		}

		try (BufferedReader br = new BufferedReader(new FileReader(stopTimesFile))) {
			br.readLine();
			String line;
			String prevTripId = "";
			int prevStopId = -1;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length < 4)
					continue;
				String tripId = parts[0];
				int stopId = Integer.parseInt(parts[3]);

				if (tripId.equals(prevTripId) && prevStopId != -1) {
					graph.get(prevStopId).add(new int[] { stopId, 1 });
				}

				prevTripId = tripId;
				prevStopId = stopId;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedReader br = new BufferedReader(new FileReader(transfersFile))) {
			br.readLine();
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length < 3)
					continue;
				int fromStopId = Integer.parseInt(parts[0]);
				int toStopId = Integer.parseInt(parts[1]);
				int transferType = Integer.parseInt(parts[2]);
				int weight = 2;

				if (transferType == 0) {
					weight = 2;
				} else if (transferType == 2 && parts.length >= 4) {
					weight = Integer.parseInt(parts[3]) / 100;
				}

				graph.get(fromStopId).add(new int[] { toStopId, weight });
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void loadStops(String path, part2.TernarySearchTree tst) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		br.readLine(); // Skip header
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length < 3)
				continue;
			int id = Integer.parseInt(parts[0]);
			String name = parts[2];
			String lat = parts.length > 3 ? parts[3] : "0";
			String lng = parts.length > 4 ? parts[4] : "0";
			stopIdToName.put(id, name);
			stopIdToCoordinates.put(id, new String[] { lat, lng });
			tst.insert(name, name, id);
		}
		br.close();
	}

	static int parseStopInput(String input, JFrame frame) throws Exception {
		List<String> matches = new ArrayList<>();

		if (input.matches("\\d+")) {
			int id = Integer.parseInt(input);
			if (!stopIdToName.containsKey(id))
				throw new Exception("Stop ID not found: " + id);
			return id;
		}

		for (Map.Entry<Integer, String> entry : stopIdToName.entrySet()) {
			if (entry.getValue().toLowerCase().contains(input.toLowerCase())) {
				matches.add(entry.getValue());
			}
		}

		if (matches.isEmpty()) {
			throw new Exception("No matching stops found.");
		} else if (matches.size() == 1) {
			return stopIdToName.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(matches.get(0)))
					.findFirst().get().getKey();
		} else {
			String selected = (String) JOptionPane.showInputDialog(frame, "Multiple stops found. Please select one:",
					"Select Stop", JOptionPane.PLAIN_MESSAGE, null, matches.toArray(), matches.get(0));

			if (selected == null)
				throw new Exception("No stop selected.");

			return stopIdToName.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(selected))
					.findFirst().get().getKey();
		}
	}
}
