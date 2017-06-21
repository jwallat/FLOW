package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import model.Edge;
import model.Network;
import model.Vertex;

public class FlowDistance {

	private Network network;
	private BreadthFirstSearch bfs;
	private int flowDistance;
	private int maxDepth;
	private HashSet<List<Edge>> allPaths;
	
	/**
	 * Konstruktor
	 * 
	 * @param network Netzwerk auf dem die FLOW-Distanz berechnet werden soll
	 */
	public FlowDistance(Network network) {
		this.network = network;
		this.flowDistance = -1;
		
		this.bfs = new BreadthFirstSearch(network);
	}
	
	/**
	 * Diese Methode enthält den eigentlichen Algorithmus.
	 * 
	 * @param source Sender
	 * @param sink Empfänger
	 */
	public void run(Vertex sender, Vertex reciever) {
		//to-do
		System.out.println("\n\nFLOW Distance:");
		System.out.println("Found paths: ");
		
		maxDepth = network.getVertices().size();
		
		allPaths = new HashSet<List<Edge>>();
		findAllPaths(sender, reciever, sender, 0, new ArrayList<Edge>());
		
		System.out.println("Num allPaths: " + allPaths.size());
		allPaths.stream().forEach(System.out::println);
	}
	
	// Loop durch bidirektionale Kanten
	public void findAllPaths(Vertex start, Vertex goal, Vertex current, int depth, ArrayList<Edge> path) {
		if (depth >= maxDepth - 1) {
			return;
		}
 		
		if (current != goal) {
			for (Edge e: network.getEdges()) {
				if (e.getOrigin() == current) {
					if (!path.stream().anyMatch(edge -> (e.getDestination().getName().equals(edge.getOrigin().getName()) || (e.getDestination().getName().equals(edge.getDestination().getName()))))) {
						ArrayList<Edge> newPath = new ArrayList<Edge>();
						newPath.addAll(path);
						newPath.add(e);
						findAllPaths(start, goal, e.getDestination(), depth + 1, newPath);
					}
				}
			}
		}
		else {
			List<Edge> persistentPath = new ArrayList<Edge>();
			persistentPath.addAll(path);
			allPaths.add(persistentPath);

			path.clear();
		}
	}
	
	/**
	 * Gibt die berechnete FLOW Distanz zwischen Sender und Empfänger zurück.
	 * 
	 * @return
	 */
	public double getFlowDistance() {
		return this.flowDistance;
	}
	
	/** 
	 * Gibt einen boolschen Wert zurück, der angibt ob die übergebenen Knoten verbunden sin.
	 * 
	 * @param source Start-Knoten
	 * @param sink Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConnected(Vertex sender, Vertex reciever) {
		if (bfs.areConntected(network, sender, reciever)) {
			return true;
		}
		return false;
	}
}
