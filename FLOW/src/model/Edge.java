package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class Edge {

	private Vertex origin;
	private Vertex destination;
	private Line line;
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
	
	public Line getShape() {
		return this.line;
	}

	public void draw(GraphicsContext gc) {
		drawArrow(gc, (int) origin.getX(), (int) origin.getY(), (int) destination.getX(), (int) destination.getY());
		drawWeighting(gc, (int) origin.getX(), (int) origin.getY(), (int) destination.getX(), (int) destination.getY());
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
	    gc.setStroke(old);
	    
	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
	            4);
	    
	    // Transofrmation rückgängig machen:
	    gc.setTransform(new Affine(transform2));
	}
	
	/**
	 * Hilfsfunktion zum Zeichnen der Kantengewichte. 
	 * 
	 * @param gc - graphical Context
	 * @param x1 - X-Position des Senders
	 * @param y1 - Y-Position des Senders
	 * @param x2 - X-Position des Empfängers
	 * @param y2 - y-Position des Empfängers
	 */
	private void drawWeighting(GraphicsContext gc, int x1, int y1, int x2, int y2) {
		gc.setFill(Color.BLACK);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx * dx + dy * dy) - 25;

	    Transform transform2 = gc.getTransform();
	    
	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    Paint old = gc.getStroke();
	    gc.setStroke(Color.BLACK);	    
	    //gc.strokeLine(0, 0, len, 0);
	    if (Math.PI/2 > angle  && angle >= -Math.PI/2) {	
	    	gc.strokeText((int) flow + "/" + (int) capacity, (len/2) - 2, 13);
	    }
	    else {
	    	drawWeighting(gc, x2, y2, x1, y1);
	    }
	    gc.setStroke(old);
	    
	    // Transofrmation rückgängig machen:
	    gc.setTransform(new Affine(transform2));
	}
	
	/**
	 * Hilfsfunktion zum Zeichnen der Kantengewichte. 
	 * 
	 * @param gc - graphical Context
	 * @param x1 - X-Position des Senders
	 * @param y1 - Y-Position des Senders
	 * @param x2 - X-Position des Empfängers
	 * @param y2 - y-Position des Empfängers
	 */
	private void drawBidirectionalWeighting(GraphicsContext gc, int x1, int y1, int flow1, int capacity1, int x2, int y2, int flow2, int capacity2) {
		boolean bidirectional = false;
		gc.setFill(Color.BLACK);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx * dx + dy * dy) - 25;

	    Transform transform2 = gc.getTransform();
	    
	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    Paint old = gc.getStroke();
	    gc.setStroke(Color.BLACK);	    
	    //gc.strokeLine(0, 0, len, 0);
	    if (Math.PI/2 > angle  && angle >= -Math.PI/2) {
	    	bidirectional = true;
	    	if (bidirectional == true) {
	    		gc.strokeText("<-1 " + (int) flow + "/" + (int) capacity + "\t " + (int) flow + "/" + (int) capacity + " 2->", (len/2) - 24, 13);
	    	}
	    	else {
	    		gc.strokeText((int) flow + "/" + (int) capacity, (len/2) - 2, 13);
	    	}
	    }
	    else {
	    	drawBidirectionalWeighting(gc, x2, y2, flow2, capacity2, x1, y1, flow1, capacity1);
	    }
	    gc.setStroke(old);
	    
	    // Transofrmation rückgängig machen:
	    gc.setTransform(new Affine(transform2));
	}

	public String toString() {
		return "Edge(" + this.id + ", " + this.origin.getName() + " --> " + this.destination.getName() + ", " + this.capacity + ")";
	}
}
