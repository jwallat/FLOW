package algorithm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import algorithm.util.BreadthFirstSearch;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Klasse, die die Funktionalitaet der Informationsausbreitung (Information
 * Expense) enthaelt. Aufgerufen werden Funnktionen dieser Klasse aus dem
 * FlowSceneController bei Interaktion mit der GUI.
 *
 * @author jwall
 *
 */
public class InformationExpense {

	private List<Pair<List<Vertex>, List<Edge>>> highlightLists = new ArrayList<Pair<List<Vertex>, List<Edge>>>();

	private SimpleStringProperty step = new SimpleStringProperty();
	private SimpleStringProperty percentageReached = new SimpleStringProperty();
	private double percentage = 0.0;
	private int numElements[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private Network network;
	private Vertex center;
	private double maxReachablePercentage;
	private DecimalFormat dm = new DecimalFormat("#,##0.00");

	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN,
			Color.RED.interpolate(Color.BLUE, 0).darker(), 30, 0.7, 0, 0);
	List<DropShadow> dropShadowList = new ArrayList<DropShadow>();

	/**
	 * Konstruktor des InformationExpense. Wird bei GUI Interkation aus dem
	 * FlowSceneController aufgerufen.
	 *
	 * @param network
	 *            das Netzwerk, welches gerade in der GUI angezeigt wird
	 * @param center
	 *            der Knoten von dem die Informationsausbreitung ausgehen soll
	 */
	public InformationExpense(Network network, Vertex center) {
		step.set("0");
		percentageReached.set(dm.format((1.0 / network.getVertices().size())) + "");
		this.network = network;
		this.center = center;
		// safeAsCSV(0.7);
	}

	/**
	 * Funktion die bei bei Klicken des ">" Buttons aufgerufen wird und den
	 * InformationExpense einen Schritt weiter anzeigt.
	 */
	public void iterateForewards() {
		if (percentage < maxReachablePercentage) {

			int s = Integer.parseInt(step.get());

			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()) + 1).getKey()) {
				double size = 30 - (25 * ((double) (s + 1) / (double) (highlightLists.size() - 1)));
				System.out.println("Size: " + size);
				v.getShape().setEffect(new DropShadow(BlurType.GAUSSIAN,
						Color.RED.interpolate(Color.BLUE, (double) (s + 1) / (double) (highlightLists.size() - 1)),
						size, 0.7, 0, 0));
			}

			for (Edge e : highlightLists.get(Integer.parseInt(step.get()) + 1).getValue()) {
				e.setColor(Color.BLACK);
			}

			// calculate new percentage
			percentage = numElements[Integer.parseInt(step.get()) + 1] / (double) network.getVertices().size();
			percentageReached.set(dm.format(percentage));

			step.set(Integer.parseInt(step.get()) + 1 + "");
		}
	}

	/**
	 * Funktion die bei bei Klicken des "<" Buttons aufgerufen wird und den
	 * InformationExpense einen Schritt vorher anzeigt.
	 */
	public void iterateBackwards() {
		if (Integer.parseInt(step.get()) > 0) {

			for (Vertex v : highlightLists.get(Integer.parseInt(step.get())).getKey()) {
				v.getShape().setEffect(null);
			}

			for (Edge e : highlightLists.get(Integer.parseInt(step.get())).getValue()) {
				e.setColor(Color.GRAY);
			}

			// verringere den stepCounter
			step.set(Integer.parseInt(step.get()) - 1 + "");

			percentage = numElements[Integer.parseInt(step.get())] / (double) network.getVertices().size();
			percentageReached.set(dm.format(percentage));
		}
	}

	/**
	 * Funnktion die den Algorithmus vorbereitet.
	 */
	public void run() {
		this.center.getShape().setEffect(highlightInformationFlow);
		computeHighlightLists(center);
	}

	/**
	 * Helfer Funktion, die eine Liste aus Listen erzeugt. In der i-ten Liste sind
	 * die Knoten enthalten, die im i-ten Schritt gehighlighted werden muessen.
	 *
	 * @param center
	 * @return
	 */
	public List<Pair<List<Vertex>, List<Edge>>> computeHighlightLists(Vertex center) {
		List<Pair<List<Vertex>, List<Edge>>> resultLists = new ArrayList<Pair<List<Vertex>, List<Edge>>>();

		List<Vertex> firstVertexList = new ArrayList<Vertex>();
		List<Edge> firstEdgeList = new ArrayList<Edge>();
		firstVertexList.add(center);

		resultLists.add(new Pair<>(firstVertexList, firstEdgeList));

		int numElements = 1;

		int numReachableElements = 1;

		BreadthFirstSearch bfs = new BreadthFirstSearch(network);
		for (Vertex v : network.getVertices()) {
			if (!v.equals(center)) {
				if (bfs.areConntected(network, center, v)) {
					numReachableElements++;
				}
			}
		}

		this.maxReachablePercentage = (double) numReachableElements / network.getVertices().size();

		for (int i = 1; numElements < numReachableElements; i++) {
			List<Vertex> stepIVertexList = new ArrayList<Vertex>();
			List<Edge> stepIEdgeList = new ArrayList<Edge>();
			for (Vertex v : resultLists.get(i - 1).getKey()) {
				for (Edge e : network.getEdges()) {
					if (e.getOrigin().equals(v)) {
						if (!contains(resultLists, e.getDestination())
								&& !stepIVertexList.contains(e.getDestination())) {
							stepIVertexList.add(e.getDestination());
							numElements++;
						}
						if (!contains(resultLists, e)) {
							stepIEdgeList.add(e);
						}
					}
				}
			}
			resultLists.add(new Pair<>(stepIVertexList, stepIEdgeList));
		}

		this.numElements[0] = 1;
		for (int i = 1; i < resultLists.size(); i++) {
			this.numElements[i] = this.numElements[i - 1] + resultLists.get(i).getKey().size();
		}

		highlightLists = resultLists;

		return resultLists;
	}

	private boolean contains(List<Pair<List<Vertex>, List<Edge>>> list, Vertex v) {
		for (Pair<List<Vertex>, List<Edge>> l2 : list) {
			List<Vertex> vertexList = l2.getKey();
			for (Vertex v2 : vertexList) {
				if (v.getID() == v2.getID()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean contains(List<Pair<List<Vertex>, List<Edge>>> list, Edge e) {
		for (Pair<List<Vertex>, List<Edge>> l2 : list) {
			List<Edge> edgeList = l2.getValue();
			for (Edge e2 : edgeList) {
				if (e.getId() == e2.getId()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gibt eine Liste an Knoten zurueck, die nach einer gegebenen Anzahl an
	 * Schritten weniger als eine gegebene Prozentzahl an Knoten erreicht haben.
	 *
	 * @param steps
	 *            - die Anzahl der Schritte
	 * @param percentage
	 *            - die Prozentzahl aller Knoten, die nach "steps" Schritten
	 *            erreicht werden muessen.
	 * @return
	 */
	public List<Vertex> getVerticesNotReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		System.out.println("*****************");
		for (Vertex v : network.getVertices()) {
			List<Pair<List<Vertex>, List<Edge>>> pairLists = computeHighlightLists(v);
			double percentageReached = (double) getSizeAfterISteps(pairLists, steps)
					/ (double) network.getVertices().size();
			if (percentageReached < percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName() + ", " + getSizeAfterISteps(pairLists, steps));
			}
		}

		return resultList;
	}

	/**
	 * Gibt eine Liste an Knoten zurueck, die nach einer gegebenen Anzahl an
	 * Schritten mindestens eine gegebene Prozentzahl an Knoten erreicht haben.
	 *
	 * @param steps
	 *            - die Anzahl der Schritte
	 * @param percentage
	 *            - die Prozentzahl aller Knoten, die nach "steps" Schritten
	 *            erreicht werden muessen.
	 * @return
	 */
	public List<Vertex> getVerticesReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		System.out.println("*****************");
		for (Vertex v : network.getVertices()) {
			List<Pair<List<Vertex>, List<Edge>>> pairLists = computeHighlightLists(v);
			double percentageReached = (double) getSizeAfterISteps(pairLists, steps)
					/ (double) network.getVertices().size();
			if (percentageReached >= percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName() + ", " + getSizeAfterISteps(pairLists, steps) + ", "
						+ percentageReached);
			}
		}

		return resultList;
	}

	/**
	 * Funktion, die fuer eine Liste aus Listen die Anzahl der Knoten zurueckgibt,
	 * die in den ersten $steps Listen enthalten sind.
	 *
	 * @param lists
	 * @param steps
	 * @return
	 */
	private int getSizeAfterISteps(List<Pair<List<Vertex>, List<Edge>>> pairLists, int steps) {
		int num = 0;
		int i = 0;
		for (Pair<List<Vertex>, List<Edge>> list : pairLists) {
			if (i <= steps) {
				num += list.getKey().size();
			}
			i++;
		}
		return num;
	}

	/**
	 * Funktion die zu einer gegebenen Prozentzahl alle Listen von Knoten, die in X
	 * Steps weniger als Y Prozent der Knoten erreichen berechnet und diese in eine
	 * CSV schreibt.
	 */
	public void safeVerticesNotReachedAsCSV(double percentage) {
		try {
			PrintWriter out = new PrintWriter(System.getProperty("user.dir") + "/out/Knoten_die_weniger_als_"
					+ percentage * 100 + "_Prozent_aller_Knoten_erreichen.csv");

			out.println("Nach Steps geordnete Auflistung der Knoten, die in weniger als " + percentage * 100
					+ " Prozent aller Knoten erreicht haben");

			List<Vertex> list = getVerticesNotReached(1, percentage);
			for (int i = 1; !list.isEmpty(); i++) {
				String names = "";
				for (Vertex v : list) {
					names += v.getName() + ", ";
				}
				names = names.substring(0, names.length() - 2);
				out.println(i + ";" + names);

				list = getVerticesNotReached(i + 1, percentage);
			}

			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Funktion die zu einer gegebenen Prozentzahl alle Listen von Knoten, die in X
	 * Steps mindestens Y Prozent der Knoten erreichen berechnet und diese in eine
	 * CSV schreibt.
	 */
	public void safeVerticesReachedAsCSV(double percentage) {
		try {
			PrintWriter out = new PrintWriter(System.getProperty("user.dir") + "/out/Knoten_die_mindestens_"
					+ percentage * 100 + "_Prozent_aller_Knoten_erreichen.csv");

			out.println("Nach Steps geordnete Auflistung der Knoten, die mindestens " + percentage * 100
					+ " Prozent aller Knoten erreicht haben");
			List<Vertex> list = getVerticesReached(1, percentage);
			List<Vertex> lastList = getVerticesReached(network.getVertices().size(), percentage);

			for (int i = 1; list.size() < lastList.size(); i++) {
				String names = "";
				for (Vertex v : list) {
					names += v.getName() + ", ";
				}
				names = names.substring(0, names.length() - 2);
				out.println(i + ";" + names);

				// lastList = list;
				list = getVerticesReached(i + 1, percentage);
			}

			out.flush();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public SimpleStringProperty Step() {
		return step;
	}

	public SimpleStringProperty PercentageReached() {
		return percentageReached;
	}
}
