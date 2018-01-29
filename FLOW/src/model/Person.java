package model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * Implementation der Vertex-Klasse f�r Personen.
 *
 * @author jwall
 *
 */
public class Person extends Vertex {

	private String name;
	private Circle shape;
	private Image img;
	private int ID;
	private int x;
	private int y;
	private int radius;

	public Person(String name, String type, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.setType(type);
		this.ID = id;
		this.x = x;
		this.y = y;
		this.radius = 15;

		this.shape = new Circle(x, y, radius);
		this.shape.setStroke(Color.BLACK);
		this.shape.setStrokeWidth(2);
		this.shape.setStrokeType(StrokeType.INSIDE);
		this.shape.setFill(Color.WHITE);

		this.img = new Image("file:///" + System.getProperty("user.dir") + "/resource/icons/person_4.png");
		// this.shape.setFill(new ImagePattern(this.img));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public Shape getShape() {
		return this.shape;
	}

	@Override
	public Image getImg() {
		return this.img;
	}
}
