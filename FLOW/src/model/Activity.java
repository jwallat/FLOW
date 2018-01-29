package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * Implementation der Vertex-Klasse für Aktivitäten.
 * 
 * @author jwall
 *
 */
public class Activity extends Vertex {

	private String name;
	private int ID;
	private Rectangle shape;
	private int x;
	private int y;
	private int width;
	private int height;

	public Activity(String name, String type, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.setType(type);
		this.ID = id;
		this.x = x;
		this.y = y;
		this.width = 30;
		this.height = 30;

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
