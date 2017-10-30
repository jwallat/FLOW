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
	private DecimalFormat dm = new DecimalFormat("#,##0.00");
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE.brighter(), 30, 0.7, 0, 0);
	private static final DropShadow d1 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(255, 0, 0), 30, 0.7, 0, 0);
	private static final DropShadow d2 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(230, 0, 25), 30, 0.7, 0, 0);
	private static final DropShadow d3 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(205, 0, 50), 30, 0.7, 0, 0);
	private static final DropShadow d4 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(180, 0, 75), 30, 0.7, 0, 0);
	private static final DropShadow d5 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(155, 0, 100), 30, 0.7, 0, 0);
	private static final DropShadow d6 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(130, 0, 125), 30, 0.7, 0, 0);
	
	List<DropShadow> dropShadowList = new ArrayList<DropShadow>();
	
	public InformationExpense(Network network, Vertex center) {
		center.getShape().setEffect(highlightInformationFlow);
		List<Vertex> tmp = new ArrayList<Vertex>();
		tmp.add(center);
		informationExpenseStepContainer.add(tmp);
		step.set("0");
		percentageReached.set("0,00");
		this.network = network;
		
		dropShadowList.add(d1);
		dropShadowList.add(d2);
		dropShadowList.add(d3);
		dropShadowList.add(d4);
		dropShadowList.add(d5);
		dropShadowList.add(d6);
	}
	
	public void iterateForewards() {		
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
			
			DropShadow d;
			if (dropShadowList.get(Integer.parseInt(step.get())) != null) {
				d = dropShadowList.get(Integer.parseInt(step.get()));
			}
			else {
				d = highlightInformationFlow;
			}
			// set visual effect to the preferred dropshadow
			for (Vertex v : tmp) {
				//v.getShape().setEffect(highlightInformationFlow);
				
				v.getShape().setEffect(d);
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
		if (Integer.parseInt(step.get()) > 0) {
			// waehle die liste der knoten des vorherigen schrittes
			List<Vertex> verticesToHighlight = informationExpenseStepContainer.get(Integer.parseInt(step.get()) - 1);
			// entferne von allen knoten den effekt
			network.getVertices().stream().forEach(v -> v.getShape().setEffect(null));
			// wende den effekt auf die oben gewaehlten knoten an
			verticesToHighlight.stream().forEach(v -> v.getShape().setEffect(highlightInformationFlow));
			// verringere den stepCounter
			step.set(Integer.parseInt(step.get()) - 1 + "");
			
			percentage = (double) (verticesToHighlight.size()) / (double) network.getVertices().size();
			percentageReached.set(dm.format(percentage));
		}
	}
	
	public final DropShadow getScaledDropShadow(int step) {
		System.out.println("in method");
		int r = 255 - step * 25;
		int b = 0 + step * 25;
		final DropShadow d = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(r, 0, b), 30, 0.7, 0, 0);
		System.out.println("DS created");
		return d;
	}
	
	public SimpleStringProperty Step() {
		return step;
	}
	
	public SimpleStringProperty PercentageReached() {
		return percentageReached;
	}
}
