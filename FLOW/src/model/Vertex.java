package model;

public abstract class Vertex {

	private String name;
	private int id;
	private int x;
	private int y;
	
	public Vertex(int id) {
		this.setId(id);
	}
	
	public Vertex(String name, int id, int x, int y) {
		this.name = name;
		this.setId(id);
		this.setX(x);
		this.setY(y);
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
	
	public String toString() {
		return "Vertex(" + name + ", " + id + ", " + x + ", " + y +")";
	}
}
