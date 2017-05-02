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
	
	public boolean containsVertexId(int id) {
		for (Vertex v : vertices) {
			if (v.getId() == id) {
				return true;
			}
		}
		return false;
	}
}
