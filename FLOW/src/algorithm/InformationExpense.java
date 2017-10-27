package algorithm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import model.Edge;
import model.Network;
import model.Vertex;

public class InformationExpense {

	private List<List<Vertex>> informationExpenseStepContainer = new ArrayList<List<Vertex>>();
	private SimpleStringProperty step = new SimpleStringProperty();
	private SimpleStringProperty percentageReached = new SimpleStringProperty();
	private double percentage = 0.0;
	private Network network;
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE.brighter(), 30, 0.7, 0, 0);
	
	public InformationExpense(Network network, Vertex center) {
		center.getShape().setEffect(highlightInformationFlow);
		List<Vertex> tmp = new ArrayList<Vertex>();
		tmp.add(center);
		informationExpenseStepContainer.add(tmp);
		step.set("0");
		percentageReached.set("0,00");
		this.network = network;
	}
	
	public void iterateForewards() {
		DecimalFormat dm = new DecimalFormat("#,##0.00");
		
		if (percentage < 1.0) {
			List<Vertex> tmp = new ArrayList<Vertex>();
			tmp.addAll(informationExpenseStepContainer.get(Integer.parseInt(step.get())));
						
			for (Vertex v : informationExpenseStepContainer.get(Integer.parseInt(step.get()))) {
				for (Edge e : network.getEdges()) {
					if (e.getOrigin().equals(v) && !tmp.contains(e.getDestination())) {
						tmp.add(e.getDestination());
					}
				}
			}
			informationExpenseStepContainer.add(tmp);
			
			// set visual effect to the preferred dropshadow
			for (Vertex v : tmp) {
				v.getShape().setEffect(highlightInformationFlow);
			}
			
			// calculate new percentage
			percentage = (double) (tmp.size() / (double) network.getVertices().size());
			System.out.println(dm.format(percentage));
			System.out.println("Network size: " + network.getVertices().size());
			System.out.println("Tmp size: " + tmp.size());
			percentageReached.set(dm.format(percentage));
			
			step.set(Integer.parseInt(step.get()) + 1 + "");
		}
	}
	
	public void iterateBackwards() {
		// waehle die liste der knoten des vorherigen schrittes
		List<Vertex> verticesToHighlight = informationExpenseStepContainer.get(Integer.parseInt(step.get()) - 1);
		// entferne von allen knoten den effekt
		network.getVertices().stream().forEach(v -> v.getShape().setEffect(null));
		// wende den effekt auf die oben gewaehlten knoten an
		verticesToHighlight.stream().forEach(v -> v.getShape().setEffect(highlightInformationFlow));
		// verringere den stepCounter
		step.set(Integer.parseInt(step.get()) - 1 + "");
	}
	
	public SimpleStringProperty Step() {
		return step;
	}
	
	public SimpleStringProperty PercentageReached() {
		return percentageReached;
	}
}
