package model;

import java.util.ArrayList;
import java.util.List;

public class Network {

	private List<Vertex> vertices;
	private List<Edge> edges;
	
	public Network() {
		this.vertices = new ArrayList<Vertex>();
		this.edges = new ArrayList<Edge>();
	}

	public List<Vertex> getVertices() {
		return vertices;
	}

	public void setVertices(List<Vertex> vertices) {
		this.vertices = vertices;
	}
	
	public Vertex getVertexByName(String name) {
		for (Vertex v : vertices) {
			if (v.getName().equals(name)) {
				return v;
			}
		}
		return null;
	}
	
	public void addVertex(Vertex v) {
		this.vertices.add(v);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
	
	public void addEdge(Edge e) {
		this.edges.add(e);
	}
	
	public boolean containsVertexID(int id) {
		for (Vertex v : vertices) {
			if (v.getID() == id) {
				return true;
			}
		}
		return false;
	}
	
	public void clearNetwork() {
		for (Edge e : edges) {
			edges.remove(e);
		}
		for (Vertex v : vertices) {
			vertices.remove(v);
		}
	}
	
	/**
	 * Fügt den Kanten des Netzwerks die Kapazitäten hinzu, die aus dem FLOW-Netzwerk ableitbar sind:
	 * Mensch --> Mensch:		1/0,3
	 * Mensch --> Document:		1/0,5
	 * Dokument --> Mensch:		1/0,6
	 * Dokument --> Dokument:	1/1
	 * 
	 * Ist eine Aktivität beteiligt wird: ******************************************************************************************************
	 * 
	 */
	public void prepareNetwork() {
		System.out.println("Preparing:");
		for (Edge e : edges) {
			if (e.getDestination().getClass().toString().contains("Activity") || e.getOrigin().getClass().toString().contains("Activity")) {
				//System.out.println("Edge mit activity: " + e);
				e.setCapacity(100);
			}
			else if (e.getOrigin().getClass().toString().contains("Person") && e.getDestination().getClass().toString().contains("Person")) {
				e.setCapacity(333);
			}
			else if (e.getOrigin().getClass().toString().contains("Person") && e.getDestination().getClass().toString().contains("Document")) {
				e.setCapacity(200);
			}
			else if (e.getOrigin().getClass().toString().contains("Document") && e.getDestination().getClass().toString().contains("Person")) {
				e.setCapacity(166);
			}
			else if (e.getOrigin().getClass().toString().contains("Document") && e.getDestination().getClass().toString().contains("Document")) {
				e.setCapacity(100);
			}
			else {
				System.out.println("Problem beim Vorbereiten des Netzwerks: Capacity konnte nicht gersetzt werden");
			}
		}
		System.out.println("Network prepared\n");
	}
}
