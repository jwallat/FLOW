package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import model.Edge;
import model.Network;
import model.Vertex;

public class FlowDistance {

	private Network network;
	private BreadthFirstSearch bfs;
	private double flowDistance;
	private int maxDepth;
	private HashSet<List<Edge>> allPaths;
	private double minValue = 1;
	
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
		
		
		//Algorithms
		List<Path> pathElements = new ArrayList<Path>();
		
		for (List<Edge> p : allPaths) {
			Path pathElement = new Path();
			pathElement.addPath(p);
			double pathValue = 0;
			for (Edge e: p) {
				pathValue += e.getFlowDistance();
			}
			pathElement.setPathValue(pathValue);
			pathElement.setPathLenght(p.size());
			
			pathElements.add(pathElement);
		}
		
		//sortieren:
		for (int i = 0; i < pathElements.size() - 1; i++) {
			Path cur = pathElements.get(i);
			Path next = pathElements.get(i+1);
			if (cur.getPathValue() > next.getPathValue()) {
				Path tmp = next;
				pathElements.set(i+1, cur);
				pathElements.set(i, tmp);
				i = -1;
			}
		}
		System.out.println("\nPfade mit Pfadwerten: ");
		pathElements
			.stream()
			.forEach(e -> System.out.println(e.getPathValue()));
		
		/*pathElements
			.stream()
			.sorted((e1, e2) -> Double.compare(e1.getPathValue(), e2.getPathValue()))
			.forEach(e -> System.out.println(e.getPathValue()));*/
		
		
		Path mainPath = pathElements.get(0);
		flowDistance = Math.max(mainPath.getPathValue(), minValue);
		System.out.println("\nFD0: " + flowDistance);
		
		for (int i = 1; i < pathElements.size(); i++) {
			double quotient = mainPath.getPathValue() / pathElements.get(i).getPathValue();
			double inverseLength = Math.pow(pathElements.get(i).getPathLenght(), -1);
			double exponent = - (quotient * inverseLength);
			double e = Math.pow(Math.E, exponent);

			double newValue = flowDistance * e;
			flowDistance = Math.max(newValue, minValue);
			System.out.println("FD" + i + ": " + flowDistance);
		}
		
		
		//runden
		flowDistance = Math.round(flowDistance * 100.0) / 100.0;
		
		//List<List<Edge>> donePaths = new ArrayList<List<Edge>>();
		
		//Intersections der Pfade finden
		/*for (List<Edge> path : allPaths) {
			Path pathElement = new Path();
			donePaths.add(path);
			pathElement.addPath(path);
			for (Edge e : path) {
				Vertex eSrc = e.getOrigin();
				Vertex eDest = e.getDestination();
				if (eSrc != sender && eDest != reciever) {
					for (List<Edge> path2 : allPaths) {
						if (path2 != path) {
							if (!donePaths.contains(path2)) {
								for (Edge e2 : path2) {
									if (eSrc == e2.getOrigin() || eSrc == e2.getDestination()) {
										pathElement.addPath(path);
										pathElement.addPath(path2);
										pathElement.addIntersection(eSrc);
										//donePaths.add(path2);
									}
									if (eDest == e2.getOrigin() || eDest == e2.getDestination()) {
										pathElement.addPath(path);
										pathElement.addPath(path2);
										pathElement.addIntersection(eSrc);
										//donePaths.add(path2);
									}
								}
							}
						}
					}
				}
			}
			//Filter pfade raus, die schon drin sind
			boolean good = true;
			for (List<Edge> pathElementPath : pathElement.getContainedPaths()) {
				for (Path p : pathElements) {
					if (p.getContainedPaths().contains(pathElementPath)) {
						good = false;
					}
				}
			}
			if (good) {
				pathElements.add(pathElement);
			}	
		}
		
		//Verarbeite PathElements und berechne FD
		
		System.out.println("\nIntersections: ");
		for (Path p : pathElements) {
			System.out.println(p.getIntersections() + ", path: " + p.getContainedPaths());
		}*/
		
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
