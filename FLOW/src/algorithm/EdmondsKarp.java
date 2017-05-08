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
public class EdmondsKarp extends MaxFlowAlgorithm {

	private Network network;
	private Network residualNetwork;
	private BreadthFirstSearch bfs;
	@SuppressWarnings("unused")
	private Vertex source;
	@SuppressWarnings("unused")
	private Vertex  sink;
	private int maxFlow;
	
	/**
	 * Konstruktor
	 * 
	 * @param network Netzwerk auf dem der maximale Fluss berechnet werden soll
	 */
	public EdmondsKarp(Network network) {
		super(network);
		this.network = network;
		this.maxFlow = -1;
		
		this.bfs = new BreadthFirstSearch(network);
		createResidualNetwork();
	}

	/**
	 * Diese Methode enthält den eigentlichen Algorithmus.
	 * 
	 * @param source Quelle
	 * @param sink Senke
	 */
	public void run(Vertex source, Vertex sink) {
		//Algorithmus
		this.source = source;
		this.sink = sink;
		
		if (bfs.areConntected(source, sink)) {
			System.out.println("Algorithm!");
		}
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
	
	/** 
	 * Gibt einen boolschen Wert zurück, der angibt ob die übergebenen Knoten verbunden sin.
	 * 
	 * @param source Start-Knoten
	 * @param sink Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConnected(Vertex source, Vertex sink) {
		if (bfs.areConntected(source, sink)) {
			return true;
		}
		return false;
	}

	public int getMaxFlow() {
		return this.maxFlow;
	}
}
