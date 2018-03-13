package algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import algorithm.util.BreadthFirstSearch;
import algorithm.util.Path;
import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Implementation zur Berechnung des neuen FLOW-Space. Weitere Informationen
 * sind der Bachelorarbeit "Algorithmen zur Analyse von Informationflussen in
 * der Software Entwicklung" von Jonas Wallat zu finden.
 *
 * @author jwall
 *
 */
public class FlowSpace {

	private Network network;
	private BreadthFirstSearch bfs;
	private double flowDistance;
	private int maxDepth;
	private HashSet<List<Edge>> allPaths;
	private double minValue = 1;

	/**
	 * Konstruktor
	 *
	 * @param network
	 *            Netzwerk auf dem die FLOW-Distanz berechnet werden soll
	 */
	public FlowSpace(Network network) {
		this.network = network;
		this.flowDistance = -1;

		this.bfs = new BreadthFirstSearch(network);
	}

	/**
	 * Diese Methode enth�lt den eigentlichen Algorithmus.
	 *
	 * @param source
	 *            Sender
	 * @param sink
	 *            Empf�nger
	 */
	public void run(Vertex sender, Vertex reciever) {
		// to-do
		System.out.println("\n\nFLOW Distance:");
		System.out.println("Found paths: ");

		maxDepth = network.getVertices().size();

		allPaths = new HashSet<List<Edge>>();
		findAllPaths(sender, reciever, sender, 0, new ArrayList<Edge>());

		System.out.println("Num allPaths: " + allPaths.size());
		allPaths.stream().forEach(System.out::println);

		// Algorithms
		List<Path> pathElements = new ArrayList<Path>();

		for (List<Edge> p : allPaths) {
			Path pathElement = new Path();
			pathElement.addPath(p);
			double pathValue = 0;
			for (Edge e : p) {
				pathValue += e.getFlowDistance();
			}
			pathElement.setPathValue(pathValue);
			pathElement.setPathLenght(p.size());

			pathElements.add(pathElement);
		}

		// sortieren:
		for (int i = 0; i < pathElements.size() - 1; i++) {
			Path cur = pathElements.get(i);
			Path next = pathElements.get(i + 1);
			if (cur.getPathValue() > next.getPathValue()) {
				Path tmp = next;
				pathElements.set(i + 1, cur);
				pathElements.set(i, tmp);
				i = -1;
			}
		}
		System.out.println("\nPfade mit Pfadwerten: ");
		pathElements.stream().forEach(e -> System.out.println(e.getPathValue()));

		Path mainPath = pathElements.get(0);
		flowDistance = Math.max(mainPath.getPathValue(), minValue);
		System.out.println("\nFD0: " + flowDistance);

		for (int i = 1; i < pathElements.size(); i++) {
			double quotient = mainPath.getPathValue() / pathElements.get(i).getPathValue();
			double inverseLength = Math.pow(pathElements.get(i).getPathLenght(), -1);
			double exponent = -(quotient * inverseLength);
			double e = Math.pow(Math.E, exponent);

			double newValue = flowDistance * e;
			flowDistance = Math.max(newValue, minValue);
			System.out.println("FD" + i + ": " + flowDistance);
		}

		// runden
		flowDistance = Math.round(flowDistance * 100.0) / 100.0;
	}

	/**
	 * Rekursive Methode, die alle zykel-freien Pfade zwischen 2 Knoten
	 * berechnet.
	 *
	 * @param start
	 *            - Startknoten
	 * @param goal
	 *            - Zielknoten
	 * @param current
	 *            - derzeitiger Knoten
	 * @param depth
	 *            - Rekursionstiefe
	 * @param path
	 *            - aktueller Pfad zwischen start und goal, bei Erstaufruf null
	 */
	// Loop durch bidirektionale Kanten
	public void findAllPaths(Vertex start, Vertex goal, Vertex current, int depth, ArrayList<Edge> path) {
		if (depth >= maxDepth - 1) {
			return;
		}

		if (current != goal) {
			for (Edge e : network.getEdges()) {
				if (e.getOrigin() == current) {
					if (!path.stream().anyMatch(edge -> (e.getDestination().getName().equals(edge.getOrigin().getName())
							|| (e.getDestination().getName().equals(edge.getDestination().getName()))))) {
						ArrayList<Edge> newPath = new ArrayList<Edge>();
						newPath.addAll(path);
						newPath.add(e);
						findAllPaths(start, goal, e.getDestination(), depth + 1, newPath);
					}
				}
			}
		} else {
			List<Edge> persistentPath = new ArrayList<Edge>();
			persistentPath.addAll(path);
			allPaths.add(persistentPath);

			path.clear();
		}
	}

	/**
	 * Gibt die berechnete FLOW Distanz zwischen Sender und Empf�nger zur�ck.
	 *
	 * @return
	 */
	public double getFlowDistance() {
		return this.flowDistance;
	}

	/**
	 * Gibt einen boolschen Wert zur�ck, der angibt ob die �bergebenen Knoten
	 * verbunden sin.
	 *
	 * @param source
	 *            Start-Knoten
	 * @param sink
	 *            Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConnected(Vertex sender, Vertex reciever) {
		if (bfs.areConntected(network, sender, reciever)) {
			return true;
		}
		return false;
	}
}
