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

	private List<List<Vertex>> highlightLists = new ArrayList<List<Vertex>>();
	
	private List<List<Vertex>> informationExpenseStepContainer = new ArrayList<List<Vertex>>();
	private SimpleStringProperty step = new SimpleStringProperty();
	private SimpleStringProperty percentageReached = new SimpleStringProperty();
	private double percentage = 0.0;
	private int numElements[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private Network network;
	private DecimalFormat dm = new DecimalFormat("#,##0.00");
	
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE.brighter(), 30, 0.7, 0, 0);
	private static final DropShadow d1 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(255, 0, 0), 30, 0.7, 0, 0);
	private static final DropShadow d2 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(230, 0, 25), 30, 0.7, 0, 0);
	private static final DropShadow d3 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(205, 0, 50), 30, 0.7, 0, 0);
	private static final DropShadow d4 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(180, 0, 75), 30, 0.7, 0, 0);
	private static final DropShadow d5 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(155, 0, 100), 30, 0.7, 0, 0);
	private static final DropShadow d6 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(100, 0, 150), 30, 0.7, 0, 0);
	private static final DropShadow d7 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(75, 0, 175), 30, 0.7, 0, 0);
	private static final DropShadow d8 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(50, 0, 200), 30, 0.7, 0, 0);
	private static final DropShadow d9 = new DropShadow(BlurType.GAUSSIAN ,Color.rgb(25, 0, 225), 30, 0.7, 0, 0);
	
	List<DropShadow> dropShadowList = new ArrayList<DropShadow>();
	
	public InformationExpense(Network network, Vertex center) {
		center.getShape().setEffect(highlightInformationFlow);
		List<Vertex> tmp = new ArrayList<Vertex>();
		tmp.add(center);
		informationExpenseStepContainer.add(tmp);
		step.set("0");
		percentageReached.set("0,00");
		this.network = network;
		
		
		computeHighlightLists(center);
		
		dropShadowList.add(d1);
		dropShadowList.add(d2);
		dropShadowList.add(d3);
		dropShadowList.add(d4);
		dropShadowList.add(d5);
		dropShadowList.add(d6);
		dropShadowList.add(d7);
		dropShadowList.add(d8);
		dropShadowList.add(d9);
	}
	
	public void iterateForewards() {		
		if (percentage < 1.0) {
			
			DropShadow d;
			if (dropShadowList.get(Integer.parseInt(step.get())) != null) {
				d = dropShadowList.get(Integer.parseInt(step.get()));
			}
			else {
				d = highlightInformationFlow;
			}

			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()) + 1)) {
				v.getShape().setEffect(d);
			}
			
			// calculate new percentage
			percentage = (double) (numElements[Integer.parseInt(step.get()) + 1] / (double) network.getVertices().size());
			percentageReached.set(dm.format(percentage));
			
			step.set(Integer.parseInt(step.get()) + 1 + "");
		}
	}
	
	public void iterateBackwards() {
		if (Integer.parseInt(step.get()) > 0) {
			
			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()))) {
				v.getShape().setEffect(null);
			}
			
			// verringere den stepCounter
			step.set(Integer.parseInt(step.get()) - 1 + "");
			
			
			percentage = (double) (numElements[Integer.parseInt(step.get())] / (double) network.getVertices().size());
			percentageReached.set(dm.format(percentage));
		}
	}
	
	public List<List<Vertex>> computeHighlightLists(Vertex center) {
		
		List<Vertex> firstList = new ArrayList<Vertex>();
		firstList.add(center);
		highlightLists.add(firstList);
		int numElements = 1;
		
		for (int i = 1; numElements < network.getVertices().size(); i++) {
			List<Vertex> stepIList = new ArrayList<Vertex>();
			for (Vertex v : highlightLists.get(i-1)) {
				for (Edge e: network.getEdges()) {
					if (e.getOrigin().equals(v)) {
						if (!contains(highlightLists, e.getDestination()) && !stepIList.contains(e.getDestination())) {
							stepIList.add(e.getDestination());
							numElements++;
						}
					}
				}
			}
			highlightLists.add(stepIList);
		}
		
		this.numElements[0] = 1;
		for (int i = 1; i < highlightLists.size(); i++) {
			this.numElements[i] = this.numElements[i-1] + highlightLists.get(i).size();
		}
		
		System.out.println("Total number: " + network.getVertices().size());
		for (int i = 0; i < highlightLists.size(); i++) {
			System.out.println("\n\n");
			System.out.println("List: " + i + ", Size: " + highlightLists.get(i).size());
			for (Vertex v : highlightLists.get(i)) {
				System.out.println(v.getName());
			}
		}
		
		
		return highlightLists;
	}
	
	private boolean contains(List<List<Vertex>> list, Vertex v) {
		for (List<Vertex> l2 : list) {
			for (Vertex v2 : l2) {
				if (v.getName().equals(v2.getName())) {
					return true;
				}
			}
		}
		
		return false;
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
