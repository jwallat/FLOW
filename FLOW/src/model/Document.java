package model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;

/**
 * Implementation der Vertex-Klasse f�r Dokumente.
 *
 * @author jwall
 *
 */
public class Document extends Vertex {

	private String name;
	private int ID;
	private Rectangle shape;
	private Image img;
	private int x;
	private int y;
	private int width;
	private int height;

	public Document(String name, String type, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.setType(type);
		this.ID = id;
		this.x = x;
		this.y = y;
		this.width = 20;// 15
		this.height = 25;// 20

		this.shape = new Rectangle(x - (width / 2), y - (height / 2), width, height);
		this.shape.setStroke(Color.BLACK);
		this.shape.setStrokeWidth(2);
		this.shape.setStrokeType(StrokeType.INSIDE);
		this.shape.setFill(Color.WHITE);

		this.img = new Image("file:///" + System.getProperty("user.dir") + "/resource/icons/document_2.png");
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
		return img;
	}

	@Override
	public void setImg(Image img) {
		this.img = img;
	}
}
