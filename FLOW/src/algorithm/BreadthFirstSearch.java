package algorithm;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Implementation der Breitensuche.
 * 
 * @author Jonas
 *
 */
public class BreadthFirstSearch {

	private Network network;
	private Queue<Vertex> queue;
	private Vertex start;
	private Vertex goal;
	private Network visitedNetwork;
	private Stack<Edge> path;
	
	/**
	 * Konstruktor
	 * 
	 * @param network Das Netzwerk auf dem die Breitensuche durchgehführt werden soll
	 */
	public BreadthFirstSearch(Network network) {
		this.network = network;
		
		this.visitedNetwork = new Network();
		queue = new LinkedList<Vertex>();
		this.path = new Stack<Edge>();
	}
	
	/**
	 * Der eigentliche Algorithmus der Breitensuche.
	 * Konstruiert gleichzeitig das visitedNetwork, ein Graph in dem alle besuchten Knoten/Kanten
	 * vorhanden sind. Dieses wird benötigt um später den genauen Pfad zu finden.
	 * 
	 * @param start Knoten von dem die Breitensuche starten soll
	 * @param goal Knoten, zu dem die Breitensuche einen Pfad finden soll
	 */
	public void run(Vertex start, Vertex goal) {
		this.start = start;
		this.goal = goal;
		
		//reset Values
		this.queue.clear();
		this.path.clear();
		this.visitedNetwork.clearNetwork();
				
		queue.add(start);
		visitedNetwork.addVertex(start);
		
		while (!queue.isEmpty()) {
			Vertex v = queue.poll();
			
			for (Edge e : network.getEdges()) {
				if (v == e.getOrigin()) {
					if (!visitedNetwork.containsVertexID(e.getDestination().getID())) {

						visitedNetwork.addEdge(e);
						queue.add(e.getDestination());
						visitedNetwork.addVertex(e.getDestination());
						
						if (e.getDestination() == goal) {
							calculatePath();
							return;
						}
					}
				}
			}
		}
		System.out.println("Kein Pfad gefunden!");
	}
	
	/**
	 * Berechnet nach Erreichen des Zielknotens einen Pfad, der von den MaxFlowAlgorithmen verwendet werden kann.
	 * 
	 */
	private void calculatePath() {
		
		Vertex currentVertex = goal;
		while (true) {
			for (Edge e : visitedNetwork.getEdges()) {
				if (e.getDestination() == currentVertex) {
					path.push(e);
					currentVertex = e.getOrigin();
				}
				if (currentVertex == start) {
					return;
				}
			}
		}
	}
	
	/** 
	 * Gibt einen boolschen Wert zurück, der angibt ob die übergebenen Knoten verbunden sin.
	 * 
	 * @param start Start-Knoten
	 * @param goal Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConntected(Vertex start, Vertex goal) {
		
		run(start, goal);
		if (!visitedNetwork.containsVertexID(goal.getID())) {
			return false;
		}
		return true;
	}
	
	public Stack<Edge> getPath() {
		return this.path;
	}
	
	public void printPath() {
		
		System.out.println("Path:");
		while (!path.isEmpty()) {
			System.out.println(path.pop().toString());
		}
	}
}
