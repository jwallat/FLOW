package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class Edge {

	private Vertex origin;
	private Vertex destination;
	private int id;
	private double capacity;
	private double flow;
	
	public Edge(int id, Vertex origin, Vertex destination, double capacity) {
		this.setOrigin(origin);
		this.setDestination(destination);
		this.setCapacity(capacity);
		this.setId(id);
	}

	public Vertex getOrigin() {
		return origin;
	}

	public void setOrigin(Vertex origin) {
		this.origin = origin;
	}

	public Vertex getDestination() {
		return destination;
	}

	public void setDestination(Vertex destination) {
		this.destination = destination;
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getFlow() {
		return flow;
	}

	public void setFlow(double flow) {
		this.flow = flow;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void draw(GraphicsContext gc) {
		drawArrow(gc, (int) origin.getCenterX(), (int) origin.getCenterY(), (int) destination.getCenterX(), (int) destination.getCenterY());
	}

	/**
	 * Hilfsfunktion zum Zeichnen der Pfeile. 
	 * 
	 * @param gc - graphical Context
	 * @param x1 - X-Position des Senders
	 * @param y1 - Y-Position des Senders
	 * @param x2 - X-Position des Empfängers
	 * @param y2 - y-Position des Empfängers
	 */
	private void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
	    gc.setFill(Color.BLACK);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    ////////////////////////// -5 hier fuer die halbe breite des knotens ////////////////////////////////
	    int len = (int) Math.sqrt(dx * dx + dy * dy) - 9;
	    int ARR_SIZE = 5;

	    Transform transform2 = gc.getTransform();
	    
	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    Paint old = gc.getStroke();
	    gc.setStroke(Color.BLACK);	    
	    gc.strokeLine(0, 0, len, 0);
	    //////////////////////////////////////////////////////////////////////////////////////////////////
	    if (Math.PI/2 > angle  && angle >= -Math.PI/2) {
	    	gc.strokeText("0/0", (len/2) - 2, 15);
	    }
	    //////////////////////////////////////////////////////////////////////////////////////////////////
	    gc.setStroke(old);
	    
	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
	            4);
	    
	    // Transofrmation rückgängig machen:
	    gc.setTransform(new Affine(transform2));
	}

	public String toString() {
		return "Edge(" + this.id + ", " + this.origin.getName() + " --> " + this.destination.getName() + ", " + this.capacity + ")";
	}
}
