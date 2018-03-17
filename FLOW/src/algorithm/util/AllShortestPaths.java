package algorithm.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Klasse die verwendet wird um alle kürzesten Pfade zwischen zwei Knoten zu
 * berechnen. Dies ist notwendig für die Berechnung der Betweenness
 * Zentralität.
 *
 * @author jwall
 *
 */
public class AllShortestPaths {

	private Network network;
	private HashSet<List<Edge>> allPaths = new HashSet<List<Edge>>();
	private int maxDepth;

	public AllShortestPaths(Network network) {
		this.network = network;

		maxDepth = network.getVertices().size();
	}

	/**
	 * Rekursive Methode, die alle zykel-freien Pfade zwischen 2 Knoten berechnet.
	 *
	 * @param start
	 *            - Startknoten
	 * @param goal
	 *            - Zielknoten
	 * @param current
	 *            - derzeitiger Knoten
	 * @param depth
	 *            - Rekursionstiefe
	 * @param path
	 *            - aktueller Pfad zwischen start und goal, bei Erstaufruf null
	 */
	// Loop durch bidirektionale Kanten
	private void findAllPaths(Vertex start, Vertex goal, Vertex current, int depth, ArrayList<Edge> path) {
		if (depth >= maxDepth - 1) {
			return;
		}

		if (current != goal) {
			for (Edge e : network.getEdges()) {
				if (e.getOrigin() == current) {
					if (!path.stream().anyMatch(edge -> (e.getDestination().getID() == edge.getOrigin().getID())
							|| (e.getDestination().getID() == edge.getDestination().getID()))) {
						ArrayList<Edge> newPath = new ArrayList<Edge>();
						newPath.addAll(path);
						newPath.add(e);
						findAllPaths(start, goal, e.getDestination(), depth + 1, newPath);
					}
				}
			}
		} else {
			List<Edge> persistentPath = new ArrayList<Edge>();
			persistentPath.addAll(path);
			allPaths.add(persistentPath);

			path.clear();
		}
	}

	public List<List<Edge>> findShortestPaths(Vertex start, Vertex goal) {

		allPaths.clear();

		findAllPaths(start, goal, start, 0, new ArrayList<Edge>());

		if (allPaths.size() == 0) {
			return null;
		}

		// order by length
		// take X shortest
		List<List<Edge>> allPathsAsList = new ArrayList<List<Edge>>();
		for (List<Edge> l : allPaths) {
			allPathsAsList.add(l);
		}
		allPathsAsList.stream().sorted(Comparator.comparing(p -> p.size()));

		Integer minLength = allPathsAsList.get(0).size();
		List<List<Edge>> shortestPaths = new ArrayList<List<Edge>>();

		for (int i = 0; i < allPathsAsList.size(); i++) {
			if (allPathsAsList.get(i).size() == minLength) {
				shortestPaths.add(allPathsAsList.get(i));
			}
		}

		return shortestPaths;
	}
}
