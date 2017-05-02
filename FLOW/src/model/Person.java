package model;


public class Person extends Vertex {

	private String name;
	private int ID;
	private int x;
	private int y;
	
	public Person(String name, int id, int x, int y) {
		super(name, id, x, y);
		this.name = name;
		this.ID = id;
		this.x = x;
		this.y = y;
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
}
