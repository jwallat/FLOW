package model;

public abstract class Vertex {

	private String name;
	private int id;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Vertex(int id) {
		this.setId(id);
	}
	
	public Vertex(String name, int id, int x, int y) {
		this.name = name;
		this.setId(id);
		this.setX(x);
		this.setY(y);
		this.width = 10;
		this.height = 10;
	}
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return "Vertex(" + name + ", " + id + ", " + x + ", " + y +")";
	}
}
