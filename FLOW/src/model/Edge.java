package model;

public abstract class Edge {

	private Vertex origin;
	private Vertex destination;
	private double value;
	
	public Edge(Vertex origin, Vertex destination, double value) {
		this.setOrigin(origin);
		this.setDestination(destination);
		this.setValue(value);
	}

	public Vertex getOrigin() {
		return origin;
	}

	public void setOrigin(Vertex origin) {
		this.origin = origin;
	}

	public Vertex getDestination() {
		return destination;
	}

	public void setDestination(Vertex destination) {
		this.destination = destination;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
