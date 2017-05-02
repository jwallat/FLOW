package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public abstract class Vertex extends Circle {

	private String name;
	private int ID;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Vertex(int id) {
		this.setID(id);
	}
	
	public Vertex(String name, int id, int x, int y) {
		super(x, y, 10);
		this.name = name;
		this.setID(id);
		this.setX(x);
		this.setY(y);
		this.setRadius(10);
		this.height = 10;
		this.width = 10;
		
		this.setStroke(Color.BLACK);
		this.setStrokeWidth(2);
		this.setStrokeType(StrokeType.INSIDE);
		this.setFill(Color.WHITE);
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
	
	public String toString() {
		return "Vertex(" + name + ", " + ID + ", " + x + ", " + y +")";
	}
}
