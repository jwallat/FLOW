package algorithm;

import java.util.ArrayList;
import java.util.List;

import model.Edge;
import model.Vertex;

public class Path {

	private double pathValue;
	private int pathLenght;
	private List<Vertex> intersections = new ArrayList<Vertex>();
	private List<List<Edge>> containedPaths = new ArrayList<List<Edge>>();
	
	public Path() {
		
	}

	public void addPath(List<Edge> path) {
		if (!containedPaths.contains(path)) {
			containedPaths.add(path);
		}
	}
	
	public List<List<Edge>> getContainedPaths() {
		return this.containedPaths;
	}
	
	public double getPathValue() {
		return pathValue;
	}

	public void setPathValue(double pathValue) {
		this.pathValue = pathValue;
	}

	public int getPathLenght() {
		return pathLenght;
	}

	public void setPathLenght(int pathLenght) {
		this.pathLenght = pathLenght;
	}
	
	public boolean containsIntersection(Vertex v) {
		return intersections.contains(v);
	}
	
	public void addIntersection(Vertex v) {
		if (!intersections.contains(v)) {
			this.intersections.add(v);
		}
	}
	
	public List<Vertex> getIntersections() {
		return this.intersections;
	}
}
