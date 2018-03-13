package algorithm;

import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Klasse, die genutzt wird um die Degree Zentralität aller Knoten zu berechnen
 * und zu setzen.
 *
 * @author jwall
 *
 */
public class DegreeCentrality {

	private Network network;

	public DegreeCentrality(Network network) {
		this.network = network;
	}

	/**
	 * Berechnet für alle Knoten den ein und ausgehenden Grad und setzt diesen
	 * Wert.
	 */
	public void compute() {
		for (Vertex v : network.getVertices()) {
			int incomingEdges = 0;
			int outgoingEdges = 0;

			for (Edge e : network.getEdges()) {
				if (e.getOrigin() == v) {
					outgoingEdges++;
				}
				if (e.getDestination() == v) {
					incomingEdges++;
				}
			}
			v.getDegreeLabel().setText("(" + incomingEdges + "," + outgoingEdges + ")");
		}
	}
}
