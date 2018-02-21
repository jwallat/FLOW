package algorithm;

import java.text.DecimalFormat;
import java.util.List;

import algorithm.util.AllShortestPaths;
import algorithm.util.BreadthFirstSearch;
import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Klasse die die Betweenness Zentralität für alle Knoten berechnet und setzt.
 *
 * @author jwall
 *
 */
public class ClosenessCentrality {

	private Network network;
	private BreadthFirstSearch bfs;
	private AllShortestPaths asp;
	private DecimalFormat df = new DecimalFormat("#0.00");

	public ClosenessCentrality(Network network) {
		this.network = network;

		this.bfs = new BreadthFirstSearch(network);
		this.asp = new AllShortestPaths(network);

	}

	/**
	 * Berechnet für alle Knoten die Closeness und setzt diesen Wert.
	 */
	public void run() {
		for (Vertex v : network.getVertices()) {
			double closeness = computeCloseness(v);
			v.getClosenessLabel().setText(df.format(closeness) + "");
		}
	}

	/**
	 * Berechnet die Closeness für den Knoten v und gibt sie zurück.
	 *
	 * @param v
	 * @return
	 */
	private double computeCloseness(Vertex v) {
		int numPaths = 0;
		double sumPathLengths = 0;
		for (Vertex s : network.getVertices()) {
			if (v != s) {
				if (bfs.areConntected(network, v, s)) {
					numPaths++;
					List<List<Edge>> shortestPaths = asp.findShortestPaths(v, s);
					if (shortestPaths != null) {
						sumPathLengths += shortestPaths.size() * shortestPaths.get(0).size();
					}

				}
			}
		}
		double avgPathLength = sumPathLengths / numPaths;
		return (1 / avgPathLength);
	}
}
