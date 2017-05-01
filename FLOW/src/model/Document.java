package model;

public class Document extends Vertex {

	private String name;
	private int id;
	private int x;
	private int y;
	
	public Document(String name, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}
