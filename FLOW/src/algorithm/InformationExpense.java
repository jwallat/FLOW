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
	
	//private List<List<Vertex>> informationExpenseStepContainer = new ArrayList<List<Vertex>>();
	private SimpleStringProperty step = new SimpleStringProperty();
	private SimpleStringProperty percentageReached = new SimpleStringProperty();
	private double percentage = 0.0;
	private int numElements[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	private Network network;
	private DecimalFormat dm = new DecimalFormat("#,##0.00");
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.RED.interpolate(Color.BLUE, 0).darker(), 30, 0.7, 0, 0);
	
	
	List<DropShadow> dropShadowList = new ArrayList<DropShadow>();
	
	public InformationExpense(Network network, Vertex center) {
		//center.getShape().setEffect(highlightInformationFlow);
		//List<Vertex> tmp = new ArrayList<Vertex>();
		//tmp.add(center);
		//informationExpenseStepContainer.add(tmp);
		step.set("0");
		percentageReached.set("0,00");
		this.network = network;
		
		
		//getVerticesNotReached(4, 0.8);
		
		//highlightLists = computeHighlightLists(center);
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
		center.getShape().setEffect(highlightInformationFlow);
		List<List<Vertex>> resultLists = new ArrayList<List<Vertex>>();
		
		List<Vertex> firstList = new ArrayList<Vertex>();
		firstList.add(center);
		
		resultLists.add(firstList);
		
		int numElements = 1;
		
		for (int i = 1; numElements < network.getVertices().size(); i++) {
			List<Vertex> stepIList = new ArrayList<Vertex>();
			for (Vertex v : resultLists.get(i-1)) {
				for (Edge e: network.getEdges()) {
					if (e.getOrigin().equals(v)) {
						if (!contains(resultLists, e.getDestination()) && !stepIList.contains(e.getDestination())) {
							stepIList.add(e.getDestination());
							numElements++;
						}
					}
				}
			}
			resultLists.add(stepIList);
		}
		
		this.numElements[0] = 1;
		for (int i = 1; i < resultLists.size(); i++) {
			this.numElements[i] = this.numElements[i-1] + resultLists.get(i).size();
		}
		
		highlightLists = resultLists;
		
		return resultLists;
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
	 * Gibt eine Liste an Knoten zur�ck, die nach einer gegebenen Anzahl an Schritten 
	 * weniger als eine gegebene Prozentzahl an Knoten erreicht haben.
	 * 
	 * @param steps - die Anzahl der Schritte
	 * @param percentage - die Prozentzahl aller Knoten, die nach "steps" Schritten erreicht werden m�ssen.
	 * @return
	 */
	public List<Vertex> getVerticesNotReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		System.out.println("*****************");
		for (Vertex v: network.getVertices()) {
			List<List<Vertex>> list = computeHighlightLists(v);
			double percentageReached = (double) ((double) getSizeAfterISteps(list, steps) / (double) network.getVertices().size());
			if (percentageReached < percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName() + ", " + getSizeAfterISteps(list, steps));
			}
		}
		
		return resultList;
	}
	
	private int getSizeAfterISteps(List<List<Vertex>> lists, int steps) {
		int num = 0;
		//System.out.println("++++++");
		int i = 0;
		for (List<Vertex> list : lists) {
			if (i <= steps) {
				num += list.size();
				//System.out.println(list.size());
			}
			
			i++;
		}
		
		//System.out.println("Num: " + num);
		return num;
	}
	
	public SimpleStringProperty Step() {
		return step;
	}
	
	public SimpleStringProperty PercentageReached() {
		return percentageReached;
	}
}
