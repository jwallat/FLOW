package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * Implementation der Vertex-Klasse für Dokumente.
 * 
 * @author jwall
 *
 */
public class Document extends Vertex {

	private String name;
	private int ID;
	private Rectangle shape;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Document(String name, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.ID = id;
		this.x = x;
		this.y = y;
		this.width = 15;
		this.height = 20;
		
		this.shape = new Rectangle(x - (width / 2), y - (height / 2), width, height);
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
