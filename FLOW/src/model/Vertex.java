package model;


import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public abstract class Vertex {

	private String name;
	private int ID;
	private Circle shape;
	private Label nameLabel;
	private boolean visited;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Vertex(int id) {
		this.setID(id);
	}
	
	public Vertex(String name, int id, int x, int y) {
		this.name = name;
		this.setID(id);
		this.setX(x);
		this.setY(y);
		this.height = 10;
		this.width = 10;
		this.setVisited(false);
		
		this.shape = new Circle(x, y, 20);
		this.nameLabel = new Label(name);
		this.nameLabel.setId("name-label");
		//this.nameLabel.setStyle("-fx-background-color: #24d24d24d");
	}
	
	public String getName() {
		return name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Shape getShape() { 
		return this.shape;
	}
	
	public Label getNameLabel() {
		return this.nameLabel;
	}
	
	public String toString() {
		return "Vertex(" + name + ", " + ID + ", " + x + ", " + y +")";
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
