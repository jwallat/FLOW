package algorithm;

import model.Network;
import model.Vertex;

public class EdmondsKarp {

	private Network network;
	private Network residualNetwork;
	private Vertex source;
	private Vertex  sink;
	private int maxFlow;
	
	//erzeuge residual network
	public EdmondsKarp(Network network, Vertex source, Vertex sink) {
		this.network = network;
		this.source = source;
		this.sink = sink;
		this.maxFlow = -1;
		
		createResidualNetwork();
		
		calculateMaxFlow();
	}
	
	private void calculateMaxFlow() {
		//Algorithmus
	}
	
	private void createResidualNetwork() {
		
	}
	
	public int getMaxFlow() {
		return this.maxFlow;
	}
}
