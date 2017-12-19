package algorithm;

import java.util.List;

import algorithm.util.BreadthFirstSearch;
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
	private int maxFlow;
	
	/**
	 * Konstruktor
	 * 
	 * @param network Netzwerk auf dem der maximale Fluss berechnet werden soll
	 */
	public EdmondsKarp(Network network) {
		super(network);
		this.network = network;
		this.maxFlow = 0;
		
		
		for (Edge edge : network.getEdges()) {
			edge.setFlow(0.0);
		}
		
		this.bfs = new BreadthFirstSearch(network);
		createResidualNetwork();
	}

	/**
	 * Diese Methode enth�lt den eigentlichen Algorithmus.
	 * 
	 * @param source Quelle
	 * @param sink Senke
	 */
	public void run(Vertex source, Vertex sink) {
		
		///////////////////////////////// Antiparallel edges!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		if (bfs.areConntected(network, source, sink)) {
			System.out.println("Algorithm!");
		}
		
		List<Edge> edges = network.getEdges();
		List<Edge> path;
		
		// Algorithmus
		for (Edge edge : edges) {
			edge.setFlow(0.0);
		}
		maxFlow = 0;
		int i = 0;
		while (bfs.areConntected(network, source, sink) && (i < 100)) {
			
			System.out.println("Loop " + i + " **********************************************************");
			i++;
			
			bfs.run(network, source, sink);
			path = bfs.getPath();
			
			edges = network.getEdges();
			
			System.out.println("Pfad gefunden: ");
			for (Edge e : path) {
				System.out.println(e.toString());
			}
			System.out.println("Ende des Pfades\n");
			
			//finde minimale freie path-capacity
			double minCapacity = path.get(0).getCapacity() - path.get(0).getFlow();
			for (Edge e : path) {
				if ((e.getCapacity() - e.getFlow()) < minCapacity) {
					minCapacity = e.getCapacity() - e.getFlow();
				}
			}
			System.out.println("MinCapacity: " + minCapacity);
			
			// setze flow im richtigen Netzwerk
 			for (Edge e : path) {
 				for (Edge networkEdge : edges) {
 					if (networkEdge.equals(e)) {
 						if (networkEdge.getCapacity() >= networkEdge.getFlow() + minCapacity) {
 							networkEdge.setFlow(networkEdge.getFlow() + minCapacity);
 							System.out.println("added flow of: " + minCapacity + " to Edge: " + networkEdge);
 						}
 					}
 				}
 			}
			
			// setze kapazit�ten im residualNetwork
 			for (Edge e : path) {
 				for (Edge e2 : residualNetwork.getEdges()) {
 					if ((e.getOrigin() == e2.getDestination()) && (e.getDestination() == e2.getOrigin())) {
 						e2.setCapacity(e.getCapacity() - minCapacity);
 					}
 				}
 				for (Edge residualEdge : residualNetwork.getEdges()) {
 					if ((e.getOrigin() == residualEdge.getOrigin()) && (e.getDestination() == residualEdge.getDestination())) {
 						residualEdge.setCapacity(minCapacity);
 					}
 				}
 			}
 			System.out.println("\nLoop ende ******************************************************");
		}
		System.out.println("\nEdges zur Sink: ");
		for (Edge e : edges) {
			if (e.getDestination() == sink) {
				System.out.println(e.getOrigin().getName() + " --> " + e.getDestination().getName() + ": " + e.getFlow() + "/" + e.getCapacity());
				maxFlow += e.getFlow();
			}
		}
		System.out.println("MaxFLOW: " + maxFlow);
	}

	/**
	 * Erzeugt das residual network, welches f�r den Algorithmus ben�tigt wird.
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
			Edge i2 = new Edge(e.getId(), e.getDestination(), e.getOrigin(), e.getCapacity());
			residualNetwork.addEdge(i2);
			residualNetwork.addEdge(i);
		}
	}
	
	/** 
	 * Gibt einen boolschen Wert zur�ck, der angibt ob die �bergebenen Knoten verbunden sin.
	 * 
	 * @param source Start-Knoten
	 * @param sink Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConnected(Vertex source, Vertex sink) {
		if (bfs.areConntected(network, source, sink)) {
			return true;
		}
		return false;
	}

	public int getMaxFlow() {
		return this.maxFlow;
	}
}
