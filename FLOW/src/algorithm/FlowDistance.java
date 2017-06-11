package algorithm;

import model.Network;
import model.Vertex;

public class FlowDistance {

	private Network network;
	private BreadthFirstSearch bfs;
	private int flowDistance;
	
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
