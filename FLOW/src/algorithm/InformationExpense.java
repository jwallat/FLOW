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
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.RED.interpolate(Color.BLUE, 0).darker(), 30, 0.7, 0, 0);
	
	
	List<DropShadow> dropShadowList = new ArrayList<DropShadow>();
	
	public InformationExpense(Network network, Vertex center) {
		center.getShape().setEffect(highlightInformationFlow);
		List<Vertex> tmp = new ArrayList<Vertex>();
		tmp.add(center);
		informationExpenseStepContainer.add(tmp);
		step.set("0");
		percentageReached.set("0,00");
		this.network = network;
		
		
		//computeHighlightLists(center);
		
		getVerticesNotReached(2, 0.8);
	}
	
	public void iterateForewards() {		
		if (percentage < 1.0) {
			
			int s = Integer.parseInt(step.get());
			
			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()) + 1)) {
				v.getShape().setEffect(new DropShadow(BlurType.GAUSSIAN ,Color.RED.interpolate(Color.BLUE, (double) ((double) (s + 1) / (double) (highlightLists.size() - 1))), 30, 0.7, 0, 0));
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
		
		//System.out.println("In function");
		List<Vertex> firstList = new ArrayList<Vertex>();
		firstList.add(center);
		highlightLists.add(firstList);
		int numElements = 1;
		//System.out.println("After add first");
		
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
		
		//System.out.println("After Loop");
		
		this.numElements[0] = 1;
		for (int i = 1; i < highlightLists.size(); i++) {
			this.numElements[i] = this.numElements[i-1] + highlightLists.get(i).size();
		}
		
		/*System.out.println("Total number: " + network.getVertices().size());
		for (int i = 0; i < highlightLists.size(); i++) {
			//System.out.println("\n\n");
			//System.out.println("List: " + i + ", Size: " + highlightLists.get(i).size());
			for (Vertex v : highlightLists.get(i)) {
				System.out.println(v.getName());
			}
		}*/	
		
		List<List<Vertex>> resultList = new ArrayList<List<Vertex>>();
		resultList.addAll(highlightLists);
		
		return resultList;
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
	
	/**
	 * Gibt eine Liste an Knoten zurück, die nach einer gegebenen Anzahl an Schritten 
	 * weniger als eine gegebene Prozentzahl an Knoten erreicht haben.
	 * 
	 * @param steps - die Anzahl der Schritte
	 * @param percentage - die Prozentzahl aller Knoten, die nach "steps" Schritten erreicht werden müssen.
	 * @return
	 */
	public List<Vertex> getVerticesNotReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		
		for (Vertex v: network.getVertices()) {
			System.out.println("Prrrrring");
			List<List<Vertex>> list = computeHighlightLists(v);
			System.out.println(list.size());
			System.out.println("Ping");
			double percentageReached = (double) ((double) getSizeAfterISteps(list, steps) / (double) network.getVertices().size());
			if (percentageReached < percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName());
			}
		}
		
		return resultList;
	}
	
	private int getSizeAfterISteps(List<List<Vertex>> list, int steps) {
		int num = 0;
		
		for (int i = 0; i <= steps; i++) {
			List<Vertex> l2 = list.get(i);
			for (Vertex v : l2) {
				num++;
			}
		}
		return num;
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
