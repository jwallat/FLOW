package algorithm;

import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Implementation des Edmonds-Karp Algorithmus zur Berechnung des maximalen Flusses.
 * 
 * @author Jonas Wallat
 *
 */
public class EdmondsKarp {

	private Network network;
	private Network residualNetwork;
	private Vertex source;
	private Vertex  sink;
	private int maxFlow;
	
	/**
	 * Konstruktor
	 * 
	 * @param network Netzwerk auf dem der maximale Fluss berechnet werden soll
	 * @param source Die Quelle
	 * @param sink Die Senke
	 */
	public EdmondsKarp(Network network, Vertex source, Vertex sink) {
		this.network = network;
		this.source = source;
		this.sink = sink;
		this.maxFlow = -1;
		
		createResidualNetwork();
		
		calculateMaxFlow();
	}
	
	/**
	 * Diese Methode enthält den eigentlichen Algorithmus.
	 * 
	 */
	private void calculateMaxFlow() {
		//Algorithmus
	}
	
	/**
	 * Erzeugt das residual network, welches für den Algorithmus benötigt wird.
	 * Dazu wird zu jeder Kante eine entgegenlaufende Kante erzeugt.
	 * 
	 */
	private void createResidualNetwork() {
		residualNetwork = new Network();
		for (Vertex v : network.getVertices()) {
			residualNetwork.addVertex(v);
		}
		for (Edge e: network.getEdges()) {
			Edge i = new Edge(-e.getId(), e.getDestination(), e.getOrigin(), e.getCapacity());
			residualNetwork.addEdge(e);
			residualNetwork.addEdge(i);
		}
	}
	
	public int getMaxFlow() {
		return this.maxFlow;
	}
}
