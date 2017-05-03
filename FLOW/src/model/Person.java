package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

public class Person extends Vertex {

	private String name;
	private Circle shape;
	private int ID;
	private int x;
	private int y;
	private int radius;
	
	public Person(String name, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.ID = id;
		this.x = x;
		this.y = y;
		this.radius = 10;
		
		this.shape = new Circle(x, y, radius);
		this.shape.setStroke(Color.BLACK);
		this.shape.setStrokeWidth(2);
		this.shape.setStrokeType(StrokeType.INSIDE);
		this.shape.setFill(Color.WHITE);
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Shape getShape() {
		return this.shape;
	}
}
