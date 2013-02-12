package vision.edgevision;

import java.util.ArrayList;
import java.util.Stack;

/**
 * A (di)graph of Edge objects representing edge pixel pairs which are adjacent
 * in an image
 * 
 * @author Alex Adams (s1046358)
 */
public class EdgeGraph {
	private final ArrayList<Edge> nodes = new ArrayList<Edge>();
	private final ArrayList<ArrayList<Integer>> arcs = new ArrayList<ArrayList<Integer>>();

	/**
	 * @return The number of nodes in the graph
	 */
	public int nodeCount() {
		return nodes.size();
	}

	/**
	 * Tests if the graph is empty
	 * 
	 * @return true if the graph is empty, false otherwise
	 */
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	/**
	 * Tests if an index is valid
	 * 
	 * @param index
	 * @return true if the index is valid, false otherwise
	 */
	public boolean isValidIndex(int index) {
		if (0 <= index && index < nodes.size())
			return true;
		else
			return false;
	}

	/**
	 * Adds an edge node to the graph
	 * 
	 * @param edge
	 *            The edge to add
	 */
	public void addNode(Edge edge) {
		// Prevent duplicates
		if (!nodes.contains(edge)) {
			// Add the node
			nodes.add(edge);
			// Create an empty arraylist to represent the arcs from that edge
			arcs.add(new ArrayList<Integer>());
		}
	}

	/**
	 * Gets the Edge object with the specified index
	 * 
	 * @param index
	 * @return The Edge object stored at the specified index
	 * @throws IndexOutOfBoundsException
	 *             If the index is not valid
	 */
	public Edge getNode(int index) throws IndexOutOfBoundsException {
		return nodes.get(index);
	}

	/**
	 * Removes an edge node from the graph and corrects the indices stored in
	 * all arcs. Note: this method assumes all of the arcs to the edge being
	 * removed have already been removed, so it is recommended to call
	 * removeAllArcsTo(index) prior to calling this method
	 * 
	 * @param index
	 *            The index of the edge to remove
	 * @throws IndexOutOfBoundsException
	 *             If the index is not valid
	 */
	public void removeNode(int index) throws IndexOutOfBoundsException {
		// First remove the node
		nodes.remove(index);

		// Next decrement the stored indicies that are higher than the index of
		// the edge being removed (since the edges they correspond to now have
		// an index 1 less than before)
		// Remove the arraylist for arcs out of the node
		arcs.remove(index);
		// If the index we just removed was the last one, we don't need to check
		// the array for higher indices
		if (index == arcs.size())
			return;
		for (int i = 0; i < arcs.size(); ++i) {
			ArrayList<Integer> arcList = arcs.get(i);
			for (int j = 0; j < arcList.size(); ++j) {
				int k = arcList.get(j);
				// If the arc list contains a reference to a higher index,
				// decrement it.
				if (k > index)
					arcList.set(j, k - 1);
			}
		}
	}

	/**
	 * Adds a (bidirectional) arc between the edges with the specified indices.
	 * 
	 * @param index1
	 * @param index2
	 * @throws IndexOutOfBoundsException
	 *             If either index is not valid
	 */
	public void addArc(int index1, int index2) throws IndexOutOfBoundsException {
		// Add the arc from edge1 to edge2
		ArrayList<Integer> arcList = arcs.get(index1);
		// Prevent duplicates
		if (!arcList.contains(index2)) {
			arcList.add(index2);
			// Add the arc from edge2 to edge1
			arcList = arcs.get(index2);
			arcList.add(index1);
		}
		// If the first arc already exists, then the second one also exists
		// since it's a bidirectional arc
	}

	/**
	 * Tests for the existance of an arc between the specified indices
	 * 
	 * @param index1
	 * @param index2
	 * @return true if the arc exists, false otherwise
	 * @throws IndexOutOfBoundsException
	 *             If either index is not valid
	 */
	public boolean hasArc(int index1, int index2)
			throws IndexOutOfBoundsException {
		return arcs.get(index1).contains(index2);
	}

	/**
	 * Removes a single (bidirectional) arc from the graph
	 * 
	 * @param index1
	 * @param index2
	 * @throws IndexOutOfBoundsException
	 *             If either index is not valid
	 */
	public void removeArc(int index1, int index2)
			throws IndexOutOfBoundsException {
		arcs.get(index1).remove(new Integer(index2));
		arcs.get(index2).remove(new Integer(index1));
	}

	/**
	 * Removes all inbound and outbound arcs for a node
	 * 
	 * @param index
	 * @throws IndexOutOfBoundsException
	 *             If the index is not valid
	 */
	public void removeAllArcs(int index) throws IndexOutOfBoundsException {
		// Use the bidirectionality of the arcs to efficiently find the inbound
		// arcs that need to be removed.
		ArrayList<Integer> arcsRemoved = arcs.get(index);
		Integer indexInt = new Integer(index);

		for (int i : arcsRemoved) {
			arcs.get(i).remove(indexInt);
		}

		// Remove the outbound arcs
		arcsRemoved.clear();
	}

	/**
	 * Retrieves an ArrayList of indices for the nodes adjacent to the specified
	 * node
	 * 
	 * @param index
	 * @return An ArrayList of indices for the nodes adjacent to the specified
	 *         node
	 * @throws IndexOutOfBoundsException
	 *             If the index is not valid
	 */
	public final ArrayList<Integer> getNeighbours(int index)
			throws IndexOutOfBoundsException {
		return arcs.get(index);
	}

	/**
	 * Determines whether a graph is connected, i.e. whether all nodes can be
	 * reached from any node on the graph. Since this is a digraph, this is
	 * directly equivalent to all nodes being reachable from the first node
	 * 
	 * @return true if it is connected, false otherwise
	 */
	public boolean isConnected() {
		int size = nodes.size();
		// Empty or single-node graph is trivially connected
		if (size < 2)
			return true;

		// visited is created with all false values
		boolean[] visited = new boolean[size];
		Stack<Integer> previousNodes = new Stack<Integer>();
		previousNodes.ensureCapacity(size);

		int current = 0;
		int visitedCount = 1;
		visited[current] = true;
		// If we visit all nodes, then the graph is connected
		while (visitedCount < visited.length) {
			// Find an unvisited neighbour
			ArrayList<Integer> neighbours = getNeighbours(current);
			for (int i : neighbours) {
				if (!visited[i]) {
					previousNodes.push(current);
					++visitedCount;
					current = i;
					break;
				}
			}
			// If we didn't find one, then backtrack if we can; if we can't then
			// the graph is not connected (we have returned to the start node)
			if (visited[current]) {
				if (!previousNodes.isEmpty())
					current = previousNodes.pop();
				else
					return false;
			}
			visited[current] = true;
		}

		return true;
	}

	public EdgeGraph[] getConnectedSubgraphs() {
		int size = nodes.size();
		if (size == 0)
			return null;
		else if (size == 1)
			return new EdgeGraph[] { this };
		
		ArrayList<ArrayList<Integer>> allSearches = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> nodesVisited = new ArrayList<Integer>();
		Stack<Integer> previousNodes = new Stack<Integer>();
		previousNodes.ensureCapacity(size);
		int current = 0;
		nodesVisited.add(0);
		int totalVisited = 0;
		
		boolean[] visited = new boolean[size];;
		while (true) {
			// Depth-first search to mark each subgraph's nodes
			do {
				visited[current] = true;
				// Find an unvisited neighbour
				ArrayList<Integer> neighbours = getNeighbours(current);
				for (int i : neighbours) {
					if (!visited[i]) {
						previousNodes.push(current);
						nodesVisited.add(current);
						current = i;
						break;
					}
				}
				// If we didn't find one, then backtrack if we can; if we can't then
				// we've finished exploring the graph
				if (visited[current] && !previousNodes.isEmpty())
					current = previousNodes.pop();
			} while (!previousNodes.isEmpty());
			
			// Add the 
			allSearches.add(nodesVisited);
			totalVisited += nodesVisited.size();
			
			if (totalVisited >= size) break;

			// Set up for the next subgraph
			nodesVisited = new ArrayList<Integer>();
			for (int i = 0; i < visited.length; ++i)
				visited[i] = false;
		}
		
		return null;
	}
}
