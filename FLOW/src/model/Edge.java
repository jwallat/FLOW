package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import util.PannablePane;
import view.FlowSceneController.visualizationType;

/**
 * Klasse die Informationen zu den Kommunikationen kapselt.
 *
 * @author jwall
 *
 */
public class Edge {

	private Vertex origin;
	private Vertex destination;
	private List<Node> shapes;
	private Label weightingLabel = new Label();
	private int id;
	private double capacity;
	private boolean isDashed = false;
	private double flow;
	private double flowDistance;
	private Color edgeColor = Color.BLACK;
	private Color weightingColor = Color.BLACK;

	public Edge(int id, Vertex origin, Vertex destination, double capacity) {
		this.setOrigin(origin);
		this.setDestination(destination);
		this.setCapacity(capacity);
		this.setId(id);

		this.shapes = new ArrayList<Node>();
		// this.line = new Line(origin.getX(), origin.getY(),
		// destination.getX(), destination.getY());
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

	public double getFlowDistance() {
		return flowDistance;
	}

	public void setFlowDistance(double flowDistance) {
		this.flowDistance = flowDistance;
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

	public void setColor(Color c) {
		this.edgeColor = c;
	}

	public Color getColor() {
		return this.edgeColor;
	}

	public List<Node> getShapes() {
		return this.shapes;
	}

	public Label getWeightingLabel() {
		return this.weightingLabel;
	}

	public Color getWeightingColor() {
		return weightingColor;
	}

	public void setWeightingColor(Color weightingColor) {
		this.weightingColor = weightingColor;
	}

	public void show(PannablePane pane) {
		showLineShape(pane);
		// edgeColor = Color.BLACK;
	}

	private void showLineShape(PannablePane pane) {

		int x1 = origin.getX();
		int y1 = origin.getY();

		int x2 = destination.getX();
		int y2 = destination.getY();

		int dx = x2 - x1;
		int dy = y2 - y1;

		int length = (int) Math.sqrt(dx * dx + dy * dy);
		double angle = Math.atan2(dy, dx);

		Line line = new Line(x1, y1, x1 + length - (destination.getWidth() + 5), y1);

		Polygon arrowHead = new Polygon();
		arrowHead.getPoints().addAll(new Double[] { line.getEndX() + 2, line.getEndY(), line.getEndX() - 5,
				line.getEndY() - 5, line.getEndX() - 5, line.getEndY() + 5 });

		// adjust colors
		line.setStroke(edgeColor);
		arrowHead.setFill(edgeColor);

		// transform:
		line.getTransforms().add(new Rotate(Math.toDegrees(angle), x1, y1));
		arrowHead.getTransforms().add(new Rotate(Math.toDegrees(angle), x1, y1));

		// decide if the line should be dashed or not
		if (isDashed) {
			line.getStrokeDashArray().addAll(25d, 10d);
		}

		// add shapes to interal list & pane
		this.shapes.add(line);
		this.shapes.add(arrowHead);

		pane.getChildren().add(line);
		pane.getChildren().add(arrowHead);

		line.toBack();
	}

	/**
	 * Hilfsfunktion zum Anzeigen der Kantengewichte.
	 *
	 * @param pane
	 *            - Parent dem das Label hinzugefügt wird
	 * @param x1
	 *            - X-Position des Senders
	 * @param y1
	 *            - Y-Position des Senders
	 * @param x2
	 *            - X-Position des Empfaengers
	 * @param y2
	 *            - y-Position des Empfaengers
	 */
	public void showWeightingLabel(PannablePane pane, int x1, int y1, int x2, int y2, visualizationType type) {

		weightingLabel = new Label();
		weightingLabel.setId("weighting-label");

		if (flow != 0) {
			weightingColor = Color.BLACK;
		} else {
			weightingColor = Color.GRAY;
		}

		weightingLabel.setTextFill(weightingColor);

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy) - 25;

		Transform transform = Transform.translate(x1, y1);
		transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));

		if (Math.PI / 2 > angle && angle >= -Math.PI / 2) {
			if (type == visualizationType.NETWORKFLOW) {
				this.weightingLabel.setText((int) flow + "/" + (int) capacity);
			} else {
				this.weightingLabel.setText(flowDistance + "");
			}

			if (!pane.getChildren().contains(weightingLabel)) {
				pane.getChildren().add(this.weightingLabel);
			}

			weightingLabel.getTransforms().add(new Affine(transform));

			weightingLabel.relocate(weightingLabel.getLayoutX() + (dx / 2), weightingLabel.getLayoutY() + (dy / 2));

			weightingLabel.relocate(
					weightingLabel.getLayoutX() - ((dx / len) * (weightingLabel.getText().length() / 2) * 8),
					weightingLabel.getLayoutY() - ((dy / len) * (weightingLabel.getText().length() / 2) * 8));

		} else {
			showWeightingLabel(pane, x2, y2, x1, y1, type);
		}
	}

	/**
	 * Hilfsfunktion zum Anzeigen der bidirektionalen Kantengewichte.
	 *
	 * @param pane
	 *            - parent dem das Label hinzugefügt wird
	 * @param x1
	 *            - X-Position des Senders
	 * @param y1
	 *            - Y-Position des Senders
	 * @param x2
	 *            - X-Position des Empfï¿½ngers
	 * @param y2
	 *            - y-Position des Empfï¿½ngers
	 */
	public void showBidirectionalWeightingLabel(PannablePane pane, int x1, int y1, int flow1, int capacity1,
			double flowDistance1, int x2, int y2, int flow2, int capacity2, double flowDistance2,
			visualizationType type) {

		weightingLabel = new Label();
		weightingLabel.setId("weighting-label");

		if (flow1 != 0 || flow2 != 0) {
			edgeColor = Color.BLACK;
		} else {
			edgeColor = Color.GRAY;
		}

		weightingLabel.setTextFill(edgeColor);

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);

		Transform transform = Transform.translate(x1, y1);
		transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));

		if (Math.PI / 2 > angle && angle >= -Math.PI / 2) {
			if (type == visualizationType.NETWORKFLOW) {
				weightingLabel
						.setText("<- " + flow2 + "/" + capacity2 + "    " + (int) flow + "/" + (int) capacity + " ->");
			} else {
				weightingLabel.setText("<- " + flowDistance2 + "    " + flowDistance1 + " ->");
			}

			if (!pane.getChildren().contains(weightingLabel)) {
				pane.getChildren().add(this.weightingLabel);
			}

			weightingLabel.getTransforms().add(new Affine(transform));

			weightingLabel.relocate(weightingLabel.getLayoutX() + (dx / 2), weightingLabel.getLayoutY() + (dy / 2));

			weightingLabel.relocate(
					weightingLabel.getLayoutX() - ((dx / len) * (weightingLabel.getText().length() / 2) * 8),
					weightingLabel.getLayoutY() - ((dy / len) * (weightingLabel.getText().length() / 2) * 8));

		} else {
			showBidirectionalWeightingLabel(pane, x2, y2, flow2, capacity2, flowDistance2, x1, y1, flow1, capacity1,
					flowDistance1, type);
		}
	}

	@Override
	public String toString() {
		return "Edge(" + this.id + ", " + this.origin.getName() + " --> " + this.destination.getName() + ", "
				+ this.flow + "/" + this.capacity + ")";
	}

	public boolean isDashed() {
		return isDashed;
	}

	public void setDashed(boolean isDashed) {
		this.isDashed = isDashed;
	}
}
