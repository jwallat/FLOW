package algorithm;

import java.util.LinkedList;
import java.util.Queue;

import model.Edge;
import model.Network;
import model.Vertex;

public class BreadthFirstSearch {

	private Network network;
	private Queue<Vertex> queue;
	private Vertex start;
	private Vertex goal;
	
	public BreadthFirstSearch(Network network, Vertex start, Vertex goal) {
		this.network = network;
		this.start = start;
		this.goal = goal;
		
		queue = new LinkedList<Vertex>();
	}
	
	public void run() {
		
		start.setVisited(true);
		queue.add(start);
		
		while (!queue.isEmpty()) {
			Vertex v = queue.poll();
			System.out.println(v.getName() + "\n");
			
			for (Edge e : network.getEdges()) {
				if (v == e.getOrigin()) {
					if (!e.getDestination().isVisited()) {
						queue.add(e.getDestination());
						e.getDestination().setVisited(true);
					}
				}
			}
		}
	}
}
