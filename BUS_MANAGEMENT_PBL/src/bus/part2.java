package bus;

import java.util.ArrayList;
import java.util.List;

public class part2 {

	class TSTNode {
		char data;
		boolean isEnd;
		TSTNode left, mid, right;
		String fullStopName;
		Integer stopId;

		public TSTNode(char data) {
			this.data = data;
		}
	}

	public class TernarySearchTree {
		private TSTNode root;

		public void insert(String word, String fullStopName, int stopId) {
			word = word.toLowerCase();
			root = insert(root, word.toCharArray(), 0, fullStopName, stopId);
		}

		private TSTNode insert(TSTNode node, char[] word, int index, String fullStopName, int stopId) {
			if (node == null)
				node = new TSTNode(word[index]);

			if (word[index] < node.data)
				node.left = insert(node.left, word, index, fullStopName, stopId);
			else if (word[index] > node.data)
				node.right = insert(node.right, word, index, fullStopName, stopId);
			else {
				if (index + 1 < word.length)
					node.mid = insert(node.mid, word, index + 1, fullStopName, stopId);
				else {
					node.isEnd = true;
					node.fullStopName = fullStopName;
					node.stopId = stopId;
				}
			}
			return node;
		}

		// Auto-complete feature
		public List<String> autoComplete(String prefix) {
			List<String> result = new ArrayList<>();
			if (prefix == null || prefix.isEmpty())
				return result;

			TSTNode node = searchNode(root, prefix.toLowerCase().toCharArray(), 0);
			if (node == null)
				return result;

			if (node.isEnd)
				result.add(node.fullStopName);

			collect(node.mid, new StringBuilder(prefix.toLowerCase()), result);
			return result;
		}

		private void collect(TSTNode node, StringBuilder prefix, List<String> result) {
			if (node == null)
				return;

			collect(node.left, prefix, result);

			if (node.isEnd)
				result.add(prefix.toString() + node.data);

			collect(node.mid, new StringBuilder(prefix).append(node.data), result);
			collect(node.right, prefix, result);
		}

		public Integer getStopId(String name) {
			if (name == null)
				return null;
			TSTNode node = searchNode(root, name.toLowerCase().toCharArray(), 0);
			return (node != null && node.isEnd) ? node.stopId : null;
		}

		private TSTNode searchNode(TSTNode node, char[] word, int index) {
			if (node == null || word.length == 0)
				return null;

			if (word[index] < node.data)
				return searchNode(node.left, word, index);
			else if (word[index] > node.data)
				return searchNode(node.right, word, index);
			else {
				if (index + 1 == word.length)
					return node;
				return searchNode(node.mid, word, index + 1);
			}
		}

		public List<String> getMatchingStops(String prefix) {
			return autoComplete(prefix);
		}

	}
}
