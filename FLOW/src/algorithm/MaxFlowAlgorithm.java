package algorithm;

import algorithm.util.BreadthFirstSearch;
import model.Network;
import model.Vertex;

/**
 * Abstrakte Klasse fuer die Verwendung von Maximal-Fluss Algorithmen zur
 * berechnung des MaxFLOW. Sollte die Effizienz des Edmonds-Karp Algorithmus
 * nicht mehr ausreichen, koennen andere Algorithmen implementiert werden. In
 * diesem Fall sollten die neuen Implentierungen diese Abstrakte Klasse
 * erweitern.
 *
 * @author jwall
 *
 */
public abstract class MaxFlowAlgorithm {

	@SuppressWarnings("unused")
	private Network network;
	@SuppressWarnings("unused")
	private BreadthFirstSearch bfs;
	@SuppressWarnings("unused")
	private Vertex source;
	@SuppressWarnings("unused")
	private Vertex sink;
	private int maxFlow;

	public MaxFlowAlgorithm(Network network) {
		this.network = network;
	}

	/**
	 * Algorithmus
	 *
	 * @param source
	 *            Quelle
	 * @param sink
	 *            Senke
	 */
	public void run(Vertex source, Vertex sink) {

	}

	/**
	 * Gibt einen boolschen Wert zurueck, der angibt ob die uebergebenen Knoten
	 * verbunden sin.
	 *
	 * @param start
	 *            Start-Knoten
	 * @param goal
	 *            Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConntected(Vertex source, Vertex sink) {
		return false;

	}

	/**
	 * Funktion die den berechneten maximalen Fluss Wert zurueck gibt.
	 *
	 * @return
	 */
	public int getMaxFlow() {
		return this.maxFlow;
	}
}
