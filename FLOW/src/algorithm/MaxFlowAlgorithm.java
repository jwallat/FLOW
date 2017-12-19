package algorithm;

import algorithm.util.BreadthFirstSearch;
import model.Network;
import model.Vertex;

public abstract class MaxFlowAlgorithm {

	@SuppressWarnings("unused")
	private Network network;
	@SuppressWarnings("unused")
	private BreadthFirstSearch bfs;
	@SuppressWarnings("unused")
	private Vertex source;
	@SuppressWarnings("unused")
	private Vertex  sink;
	private int maxFlow;
	
	public MaxFlowAlgorithm(Network network) {
		this.network = network;
	}
	
	/**
	 * Algorithmus 
	 * 
	 * @param source Quelle
	 * @param sink Senke
	 */
	public void run(Vertex source, Vertex sink) {
		
	}
	
	/** 
	 * Gibt einen boolschen Wert zur�ck, der angibt ob die �bergebenen Knoten verbunden sin.
	 * 
	 * @param start Start-Knoten
	 * @param goal Ziel-Knoten
	 * @return true/false wenn es Pfad existiert/nicht existiert
	 */
	public boolean areConntected(Vertex source, Vertex sink) {
		return false;
		
	}
	
	/**
	 * Funktion die den berechneten maximalen Fluss Wert zur�ck gibt.
	 * 
	 * @return
	 */
	public int getMaxFlow() {
		return this.maxFlow;
	}
}
