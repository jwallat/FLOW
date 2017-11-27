package algorithm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

/**
 * Klasse, die die Funktionalität der Informationsausbreitung (Information
 * Expense) enthält. Aufgerufen werden Funnktionen dieser Klasse aus dem
 * FlowSceneController bei Interaktion mit der GUI.
 *
 * @author jwall
 *
 */
public class InformationExpense {

	private List<List<Vertex>> highlightLists = new ArrayList<List<Vertex>>();

	private SimpleStringProperty step = new SimpleStringProperty();
	private SimpleStringProperty percentageReached = new SimpleStringProperty();
	private double percentage = 0.0;
	private int numElements[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private Network network;
	private Vertex center;
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
		percentageReached.set("0,00");
		this.network = network;
		this.center = center;
		// safeAsCSV(0.7);
	}

	/**
	 * Funktion die bei bei Klicken des ">" Buttons aufgerufen wird und den
	 * InformationExpense einen Schritt weiter anzeigt.
	 */
	public void iterateForewards() {
		if (percentage < 1.0) {

			int s = Integer.parseInt(step.get());

			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()) + 1)) {
				double size = 30 - (25 * ((double) (s + 1) / (double) (highlightLists.size() - 1)));
				System.out.println("Size: " + size);
				v.getShape().setEffect(new DropShadow(BlurType.GAUSSIAN,
						// Color.RED.interpolate(Color.BLUE, (double) (s + 1) /
						// (double) (highlightLists.size() - 1)), 30,
						Color.RED.interpolate(Color.BLUE, (double) (s + 1) / (double) (highlightLists.size() - 1)),
						size, 0.7, 0, 0));
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

			for (Vertex v : highlightLists.get(Integer.parseInt(step.get()))) {
				v.getShape().setEffect(null);
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
	 * Helfer Funktion, die eine Liste aus Listen erzeugt. In der i-ten Liste
	 * sind die Knoten enthalten, die im i-ten Schritt gehighlighted werden
	 * müssen.
	 *
	 * @param center
	 * @return
	 */
	public List<List<Vertex>> computeHighlightLists(Vertex center) {
		List<List<Vertex>> resultLists = new ArrayList<List<Vertex>>();

		List<Vertex> firstList = new ArrayList<Vertex>();
		firstList.add(center);

		resultLists.add(firstList);

		int numElements = 1;

		for (int i = 1; numElements < network.getVertices().size(); i++) {
			List<Vertex> stepIList = new ArrayList<Vertex>();
			for (Vertex v : resultLists.get(i - 1)) {
				for (Edge e : network.getEdges()) {
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
			this.numElements[i] = this.numElements[i - 1] + resultLists.get(i).size();
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
	 * Gibt eine Liste an Knoten zur�ck, die nach einer gegebenen Anzahl an
	 * Schritten weniger als eine gegebene Prozentzahl an Knoten erreicht haben.
	 *
	 * @param steps
	 *            - die Anzahl der Schritte
	 * @param percentage
	 *            - die Prozentzahl aller Knoten, die nach "steps" Schritten
	 *            erreicht werden m�ssen.
	 * @return
	 */
	public List<Vertex> getVerticesNotReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		System.out.println("*****************");
		for (Vertex v : network.getVertices()) {
			List<List<Vertex>> list = computeHighlightLists(v);
			double percentageReached = (double) getSizeAfterISteps(list, steps) / (double) network.getVertices().size();
			if (percentageReached < percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName() + ", " + getSizeAfterISteps(list, steps));
			}
		}

		return resultList;
	}

	/**
	 * Gibt eine Liste an Knoten zur�ck, die nach einer gegebenen Anzahl an
	 * Schritten mindestens eine gegebene Prozentzahl an Knoten erreicht haben.
	 *
	 * @param steps
	 *            - die Anzahl der Schritte
	 * @param percentage
	 *            - die Prozentzahl aller Knoten, die nach "steps" Schritten
	 *            erreicht werden m�ssen.
	 * @return
	 */
	public List<Vertex> getVerticesReached(int steps, double percentage) {
		List<Vertex> resultList = new ArrayList<Vertex>();
		System.out.println("*****************");
		for (Vertex v : network.getVertices()) {
			List<List<Vertex>> list = computeHighlightLists(v);
			double percentageReached = (double) getSizeAfterISteps(list, steps) / (double) network.getVertices().size();
			if (percentageReached >= percentage) {
				resultList.add(v);
				System.out.println("Name: " + v.getName() + ", " + getSizeAfterISteps(list, steps));
			}
			if (percentageReached >= 1.0) {
				break;
			}
		}

		return resultList;
	}

	private int getSizeAfterISteps(List<List<Vertex>> lists, int steps) {
		int num = 0;
		// System.out.println("++++++");
		int i = 0;
		for (List<Vertex> list : lists) {
			if (i <= steps) {
				num += list.size();
				// System.out.println(list.size());
			}

			i++;
		}

		// System.out.println("Num: " + num);
		return num;
	}

	/**
	 * Funktion die zu einer gegebenen Prozentzahl alle Listen von Knoten, die
	 * in X Steps weniger als Y Prozent der Knoten erreichen berechnet und diese
	 * in eine CSV schreibt.
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
	 * Funktion die zu einer gegebenen Prozentzahl alle Listen von Knoten, die
	 * in X Steps mindestens Y Prozent der Knoten erreichen berechnet und diese
	 * in eine CSV schreibt.
	 */
	public void safeVerticesReachedAsCSV(double percentage) {
		try {
			PrintWriter out = new PrintWriter(System.getProperty("user.dir") + "/out/Knoten_die_mindestens_"
					+ percentage * 100 + "_Prozent_aller_Knoten_erreichen.csv");

			out.println("Nach Steps geordnete Auflistung der Knoten, die mindestens " + percentage * 100
					+ " Prozent aller Knoten erreicht haben");
			List<Vertex> list = getVerticesReached(1, percentage);
			for (int i = 1; !list.isEmpty(); i++) {
				String names = "";
				for (Vertex v : list) {
					names += v.getName() + ", ";
				}
				names = names.substring(0, names.length() - 2);
				out.println(i + ";" + names);

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
