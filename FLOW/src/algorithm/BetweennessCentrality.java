package algorithm;

import java.text.DecimalFormat;
import java.util.List;

import algorithm.util.AllShortestPaths;
import algorithm.util.BreadthFirstSearch;
import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Klasse die die Betweenness Zentralit√§t fuer alle Knoten berechnet und setzt.
 *
 * @author jwall
 *
 */
public class BetweennessCentrality {

	private Network network;
	private BreadthFirstSearch bfs;
	private AllShortestPaths asp;
	private DecimalFormat df = new DecimalFormat("#0.00");

	public BetweennessCentrality(Network network) {
		this.network = network;

		this.bfs = new BreadthFirstSearch(network);
		this.asp = new AllShortestPaths(network);
	}

	/**
	 * Berechnet fuer alle Knoten die Closeness und setzt diesen Wert.
	 */
	public void compute() {
		int i = 0;
		System.out.println("\n\n");
		for (Vertex v : network.getVertices()) {
			double betweenness = computeBetweenness(v);
			v.getBetweennessLabel().setText(df.format(betweenness) + "");
			i++;
			System.out.println("Berechne Betweenness Zentralit‰t... " + i + "/" + network.getVertices().size());
		}
	}

	/**
	 * Berechnet die Closeness fuer den Knoten v und gibt sie zurueck.
	 *
	 * @param v
	 * @return
	 */
	private double computeBetweenness(Vertex v) {
		int numPaths = 0;
		int numPathsContainingV = 0;
		double sum = 0;
		for (Vertex s : network.getVertices()) {
			for (Vertex t : network.getVertices()) {
				numPaths = 0;
				numPathsContainingV = 0;
				if (v != s && v != t) {
					if (bfs.areConntected(network, s, t)) {
						List<List<Edge>> shortestPaths = asp.findShortestPaths(s, t);
						for (List<Edge> path : shortestPaths) {
							if (contains(path, v)) {
								numPathsContainingV++;
								numPaths++;
							} else {
								numPaths++;
							}
						}
					}
				}
				if (numPaths != 0) {
					sum += numPathsContainingV / numPaths;
				}
			}
		}
		// normalize to get a value between 0 and 1:
		double betweenness = sum / ((network.getVertices().size() - 1) * (network.getVertices().size() - 2));
		return betweenness;

	}

	private boolean contains(List<Edge> path, Vertex v) {
		for (Edge e : path) {
			if (e.getOrigin() == v || e.getDestination() == v) {
				return true;
			}
		}
		return false;
	}

}
