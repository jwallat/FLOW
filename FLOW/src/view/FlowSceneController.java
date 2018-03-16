package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.BetweennessCentrality;
import algorithm.ClosenessCentrality;
import algorithm.DegreeCentrality;
import algorithm.Density;
import algorithm.EdmondsKarp;
import algorithm.FlowSpace;
import algorithm.InformationExpense;
import control.Parser;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Edge;
import model.Network;
import model.Vertex;
import util.PannablePane;
import util.SceneGestures;
import util.SwitchButton;

/**
 * Kontroller zur FlowScene.fxml. Hier werden alle Interaktionen mit dem UI
 * ermöglicht.
 *
 * @author Jonas Wallat
 *
 */
public class FlowSceneController implements Initializable {

	@FXML
	private MenuBar menuBar;

	@FXML
	private HBox hBox;

	@FXML
	private AnchorPane anchor;

	private PannablePane pannablePane;

	private Canvas canvas;

	private GraphicsContext gc;

	@FXML
	private TitledPane informationPane;

	@FXML
	private HBox toggleButtonHBox;

	private SwitchButton switchButton;

	@FXML
	private HBox weightsToggleButtonHBox;

	private SwitchButton weightsSwitchButton;

	@FXML
	private HBox centralityToggleButtonHBox;

	private SwitchButton centralitySwitchButton;

	@FXML
	private CheckBox closenessCheckBox;

	@FXML
	private CheckBox betweennessCheckBox;

	@FXML
	private CheckBox degreeCheckBox;

	@FXML
	private Label densityLabel;

	@FXML
	private HBox legendHBox;

	private ImageView legendImageView;

	@FXML
	private Label fileNameLabel;

	@FXML
	private Label sourceLabel;

	@FXML
	private Label centerVertexLabel;

	@FXML
	private Label stepCounterLabel;

	@FXML
	private Label verticesReachedLabel;

	@FXML
	private Label sinkLabel;

	@FXML
	private Label maxFlowLabel;

	@FXML
	private Label flowDistanceLabel;

	@FXML
	private Button acceptButton;

	@FXML
	private RadioButton networkFlowRadioButton;

	@FXML
	private RadioButton flowDistanceRadioButton;

	@FXML
	private RadioButton waterPipeRadioButton;

	@FXML
	private ToggleButton highlightFlowButton;

	@FXML
	private TextField reachedLessStepTextField;

	@FXML
	private TextField reachedLessPercentageTextField;

	@FXML
	private TextField reachedMoreStepTextField;

	@FXML
	private TextField reachedMorePercentageTextField;

	@FXML
	private Button reachedLessGoButton;

	@FXML
	private Button reachedMoreGoButton;

	@FXML
	private TextField percentageTextField;

	private Stage stage;
	private File file;
	private Parser parser;
	private Network network;
	private Vertex source;
	private Vertex sink;
	private Vertex centerVertex;
	private boolean highlightFlow = false;
	private InformationExpense iE;

	public enum visualizationType {
		NETWORKFLOW, FLOWDISTANCE, WATERPIPES
	}

	private visualizationType edgeWeighting = visualizationType.NETWORKFLOW;

	private static final DropShadow highlightSource = new DropShadow(BlurType.GAUSSIAN, Color.VIOLET.darker().darker(),
			30, 0.7, 0, 0);
	private static final DropShadow highlightSink = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE, 30, 0.7, 0, 0);

	/**
	 * Initialisierungs-Methode, die aufgerufen wird, wenn in der Main.java die
	 * FlowScene.fxml geladen wird.
	 *
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Fügt dem MenuPanel auf der rechten seite einen Switch button zum an
		// und ausstellen der FLOW-Notation hinzu.
		this.switchButton = new SwitchButton(this, "flow");
		toggleButtonHBox.getChildren().add(switchButton);

		this.centralitySwitchButton = new SwitchButton(this, "centrality");
		centralityToggleButtonHBox.getChildren().add(centralitySwitchButton);

		this.weightsSwitchButton = new SwitchButton(this, "weights");
		weightsToggleButtonHBox.getChildren().add(weightsSwitchButton);

		// Initialisiere die Legende der Zentralitäten
		try {
			this.legendImageView = new ImageView(
					"file:///" + System.getProperty("user.dir") + "/resource/icons/legend.png");
		} catch (Exception e) {
			System.out.println("Legende konnte nicht initialisiert werden. Stellen Sie sicher, dass die Legende "
					+ "in resource/icons/legend.png vorhanden ist");
			this.legendImageView = new ImageView();
		}

	}

	/**
	 * �bergibt die Stage, sodass der FileChooser verwendet werden kann.
	 *
	 * @param stage
	 *            Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;

		stage.setMaximized(true);

		// Order Elements and set size-Properties
		hBox.setPrefWidth(stage.getScene().getWidth());
		hBox.setPrefHeight(stage.getScene().getHeight());
		hBox.autosize();
		hBox.toBack();

		menuBar.toFront();
		menuBar.prefWidthProperty().bind(anchor.widthProperty());

		pannablePane = new PannablePane();
		anchor.getChildren().add(pannablePane);

		anchor.setPrefWidth(hBox.getWidth());
		anchor.setPrefHeight(hBox.getHeight());
		anchor.autosize();
		anchor.toBack();

		pannablePane.setPrefWidth(anchor.widthProperty().get());
		pannablePane.setPrefHeight(anchor.heightProperty().get());
		pannablePane.autosize();

		pannablePane.toFront();
		informationPane.toFront();

		canvas = new Canvas();
		pannablePane.getChildren().add(canvas);

		canvas.widthProperty().bind(pannablePane.widthProperty());
		canvas.heightProperty().bind(pannablePane.heightProperty());
		canvas.autosize();

		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BLUE);
		// gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		SceneGestures sceneGestures = new SceneGestures(pannablePane);
		stage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		stage.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		stage.getScene().addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
	}

	/**
	 * �ffnet einen FileChooser fuer XML-Dateien.
	 *
	 */
	public void openFile() {

		if (network != null) {
			clearVertices();
			clearLines();
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}

		FileChooser fc = new FileChooser();
		fc.setTitle("Big FLOW");
		fc.setInitialDirectory(new File(System.getProperty("user.dir") + "/resource/xmls"));
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));

		file = fc.showOpenDialog(stage);

		if (file == null) {
			System.out.println("Fehler beim lesen der Datei!");
		}

		fileNameLabel.setText(file.getName());

		parser = new Parser(file);
		parser.parse();
		network = parser.getData();
		network.prepareNetwork();

		computeCentralities();

		showNetwork();
		switchButton.switchOnProperty().set(false);

		informationPane.expandedProperty().set(true);
	}

	/**
	 * Funktion die das Programm beendet. Sie wird �ber das UI aufgerufen.
	 *
	 */
	public void close() {
		System.exit(0);
	}

	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgef�hrt wird. Die beiden
	 * Radiobuttons schlie�en sich gegenseitig aus und geben an, welche
	 * Information auf den Kanten angezeigt wird.
	 *
	 */
	public void networkFlowRadioButtonClicked() {
		if (file != null) {
			if (flowDistanceRadioButton.isSelected() || waterPipeRadioButton.isSelected()) {
				flowDistanceRadioButton.setSelected(false);
				waterPipeRadioButton.setSelected(false);

				edgeWeighting = visualizationType.NETWORKFLOW;

				updateGraphics();
			} else {
				networkFlowRadioButton.setSelected(true);
			}
		}
	}

	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgef�hrt wird. Die beiden
	 * Radiobuttons schlie�en sich gegenseitig aus und geben an, welche
	 * Information auf den Kanten angezeigt wird.
	 *
	 */
	public void flowDistanceRadioButtonClicked() {
		if (file != null) {
			if (waterPipeRadioButton.isSelected() || networkFlowRadioButton.isSelected()) {
				networkFlowRadioButton.setSelected(false);
				waterPipeRadioButton.setSelected(false);
				edgeWeighting = visualizationType.FLOWDISTANCE;

				updateGraphics();
			} else {
				flowDistanceRadioButton.setSelected(true);
			}
		}
	}

	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgef�hrt wird. Die drei
	 * Radiobuttons schlie�en sich gegenseitig aus und geben an, welche
	 * Information auf den Kanten angezeigt wird.
	 *
	 */
	public void waterPipeRadioButtonClicked() {
		if (file != null) {
			if (flowDistanceRadioButton.isSelected() || networkFlowRadioButton.isSelected()) {
				networkFlowRadioButton.setSelected(false);
				flowDistanceRadioButton.setSelected(false);
				edgeWeighting = visualizationType.WATERPIPES;

				updateGraphics();
			} else {
				waterPipeRadioButton.setSelected(true);
			}
		}
	}

	/**
	 * �ffnet das InformationPane und l�sst den Nutzer Source/Sink mit klick
	 * ausw�hlen. Wird ausgef�hrt, wenn der "select vertices" Button geklickt
	 * wird.
	 *
	 */
	@FXML
	private void selectSourceAndSink() {

		if (network == null) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Network Flow");
			alert.setHeaderText(null);
			alert.setContentText("Please open a network first!");

			alert.showAndWait();
			return;
		}

		if (source != null) {
			source.getShape().setEffect(null);
			source = null;
		}
		if (sink != null) {
			sink.getShape().setEffect(null);
			sink = null;
		}
		if (centerVertex != null) {
			centerVertex.getShape().setEffect(null);
			centerVertex = null;
			centerVertexLabel.setText("");
		}

		// setze alles zur�ck
		resetNetworkFlow();
		clearVertexHighlights();
		updateGraphics();

		maxFlowLabel.setText("");
		flowDistanceLabel.setText("");
		sourceLabel.setText("");
		sinkLabel.setText("");

		informationPane.expandedProperty().set(true);

		for (Vertex v : network.getVertices()) {
			Shape shape = v.getShape();

			shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.getScene().setCursor(Cursor.HAND);
				}
			});

			shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					if (source == null) {
						source = v;
						v.getShape().setEffect(highlightSource);
						sourceLabel.setText(v.getName());
					} else if (sink == null) {
						sink = v;
						v.getShape().setEffect(highlightSink);
						sinkLabel.setText(v.getName());
					} else {
						source.getShape().setEffect(null);
						source = sink;
						source.getShape().setEffect(highlightSource);
						sourceLabel.setText(source.getName());
						sink = v;
						sink.getShape().setEffect(highlightSink);
						sinkLabel.setText(v.getName());
					}
				}
			});

			shape.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// shape.setEffect(null);
					stage.getScene().setCursor(Cursor.DEFAULT);
				}
			});
		}
	}

	/**
	 * Funktion die bei Klick auf den "Calculate"-Button aufgerufen wird.
	 *
	 */
	public void calculateButtonClicked() {
		if (source != null && sink != null) {
			for (Vertex v : network.getVertices()) {
				Shape shape = v.getShape();
				shape.setOnMouseEntered(null);
				shape.setOnMouseClicked(null);
				shape.setOnMouseExited(null);
			}

			EdmondsKarp edmondsKarp = new EdmondsKarp(network);
			FlowSpace flowDistance = new FlowSpace(network);

			if (!edmondsKarp.areConnected(source, sink)) {
				maxFlowLabel.setText("Not connected");
				flowDistanceLabel.setText("Not connected");
			} else {
				edmondsKarp.run(source, sink);
				updateGraphics();
				maxFlowLabel.setText(edmondsKarp.getMaxFlow() + "");

				flowDistance.run(source, sink);
				flowDistanceLabel.setText(flowDistance.getFlowDistance() + "");
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Calculation ERROR");
			alert.setHeaderText(null);
			alert.setContentText("Select the source AND sink!");

			alert.showAndWait();
		}
	}

	/**
	 * Funktion die bei Klick des Toggle-Buttons ausgef�hrt wird.
	 *
	 */
	public void highlightFlowButtonClicked() {

		if (file != null) {
			highlightFlow = highlightFlowButton.isSelected();

			updateGraphics();
		}
	}

	/**
	 * Funktion die bei Klick des Info-Expansion-Forewards-Buttons ausgef�hrt
	 * wird.
	 */
	public void infoExpansionForewardsButtonClicked() {
		if (iE != null) {
			iE.iterateForewards();
			clearLines();
			showEdges("informationExpansion");
		}
	}

	/**
	 * Funktion die bei Klick des Info-Expansion-Backwards-Buttons ausgef�hrt
	 * wird.
	 */
	public void infoExpansionBackwardsButtonClicked() {
		if (iE != null) {
			iE.iterateBackwards();
			clearLines();
			showEdges("informationExpansion");
		}
	}

	/**
	 * Funktion die bei Klicken des "Select Source" Buttons ausgef�hrt wird. Es
	 * die hier ausgew�hlte Source wird f�r die Informationsausbreitung
	 * verwendet.
	 */
	public void selectInformationSource() {
		if (network == null) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Network Flow");
			alert.setHeaderText(null);
			alert.setContentText("Please open a network first!");

			alert.showAndWait();
			return;
		}

		if (source != null) {
			source.getShape().setEffect(null);
			sourceLabel.setText("");
			source = null;
		}
		if (sink != null) {
			sink.getShape().setEffect(null);
			sinkLabel.setText("");
			sink = null;
		}

		// setze alles zur�ck
		clearVertexHighlights();
		resetNetworkFlow();
		updateGraphics();

		maxFlowLabel.setText("");
		flowDistanceLabel.setText("");

		// stepCounterLabel.setText("0");

		informationPane.expandedProperty().set(true);

		for (Vertex v : network.getVertices()) {
			Shape shape = v.getShape();

			shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.getScene().setCursor(Cursor.HAND);
				}
			});

			shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					centerVertex = v;
					iE = new InformationExpense(network, v);
					iE.run();
					stepCounterLabel.textProperty().bind(iE.Step());
					verticesReachedLabel.textProperty().bind(iE.PercentageReached());
					centerVertexLabel.setText(v.getName());

					stage.getScene().setCursor(Cursor.DEFAULT);

					for (Vertex v : network.getVertices()) {
						Shape shape = v.getShape();
						shape.setOnMouseEntered(null);
						shape.setOnMouseClicked(null);
						shape.setOnMouseExited(null);
					}
				}
			});

			shape.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					stage.getScene().setCursor(Cursor.DEFAULT);
				}
			});
		}
	}

	/**
	 * Funktion die bei Klicken des "Go"-Buttons ausgeführt wird. Dabei werden die
	 * angebenen Parameter gelesen und alle Knoten, die in $steps nicht mindestens
	 * $prozent der gesamten Knoten erreichen ausgegeben.
	 */
	public void reachedLessGoButtonClicked() {
		clearVertexHighlights();

		InformationExpense iE = new InformationExpense(network, null);
		if (reachedLessStepTextField.getText().matches("\\d+")) {
			int step = Integer.parseInt(reachedLessStepTextField.getText());
			if (reachedLessPercentageTextField.getText().matches("\\d+")) {
				double percentage = Double.parseDouble(reachedLessPercentageTextField.getText());

				if (percentage <= 100) {
					List<Vertex> list = iE.getVerticesNotReached(step, percentage / 100.0);

					for (Vertex v : list) {
						v.getShape().setEffect(highlightSink);
					}

				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Percentage");
					alert.setHeaderText(null);
					alert.setContentText("Valid Input is a number between 0-100");

					alert.showAndWait();
				}

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Percentage");
				alert.setHeaderText(null);
				alert.setContentText("Valid Input is a number between 0-100");

				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Step");
			alert.setHeaderText(null);
			alert.setContentText("Valid Input is a number >= 0");

			alert.showAndWait();
		}
	}

	/**
	 * Funktion die bei Klicken des "Go"-Buttons ausgeführt wird. Dabei werden die
	 * angebenen Parameter gelesen und alle Knoten, die in $steps mindestens
	 * $prozent der gesamten Knoten erreichen ausgegeben.
	 */
	public void reachedMoreGoButtonClicked() {
		clearVertexHighlights();

		InformationExpense iE = new InformationExpense(network, null);
		if (reachedMoreStepTextField.getText().matches("\\d+")) {
			int step = Integer.parseInt(reachedMoreStepTextField.getText());
			if (reachedMorePercentageTextField.getText().matches("\\d+")) {
				double percentage = Double.parseDouble(reachedMorePercentageTextField.getText());

				if (percentage <= 100) {
					List<Vertex> list = iE.getVerticesReached(step, percentage / 100.0);

					for (Vertex v : list) {
						v.getShape().setEffect(highlightSink);
					}

				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Percentage");
					alert.setHeaderText(null);
					alert.setContentText("Valid Input is a number between 0-100");

					alert.showAndWait();
				}

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Percentage");
				alert.setHeaderText(null);
				alert.setContentText("Valid Input is a number between 0-100");

				alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Step");
			alert.setHeaderText(null);
			alert.setContentText("Valid Input is a number >= 0");

			alert.showAndWait();
		}
	}

	/**
	 * Click-Methode die ausgef�hrt wenn der computeOutput-Button geklickt wird.
	 * Daraufhin werden Listen berechnet, in den f�r die gegebene Prozentzahl
	 * f�r alle Schritte jeweils alle Knoten aufgelistet werden, die weniger
	 * als/mindestens die gegebene Prozentzahl aller Knoten erreichen.
	 *
	 */
	public void computeOutputButtonClicked() {
		if (percentageTextField.getText().matches("\\d+")) {
			double percentage = Double.parseDouble(percentageTextField.getText());

			if (percentage <= 100) {
				// List<Vertex> list = iE.getVerticesNotReached(step, percentage
				// / 100.0);
				InformationExpense iE = new InformationExpense(network, null);
				iE.safeVerticesReachedAsCSV(percentage / 100.0);
				iE.safeVerticesNotReachedAsCSV(percentage / 100.0);

			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Percentage");
				alert.setHeaderText(null);
				alert.setContentText("Valid Input is a number between 0-100");

				alert.showAndWait();
			}

		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Percentage");
			alert.setHeaderText(null);
			alert.setContentText("Valid Input is a number between 0-100");

			alert.showAndWait();
		}
	}

	/**
	 * Funktion die ausgeführt wird, wenn der FLOW-Notations switch geklickt wird.
	 * Werden zur Zeit die Konten in der FLOW-Notation angezeigt, dann wird auf die
	 * normale, schlichtere Visualisierung gewechselt und umgekehrt.
	 */
	public void toggleFLOWNotation() {

		if (switchButton.switchOnProperty().get() == true) {
			// zeige alles in FLOW-Notation
			for (Vertex v : network.getVertices()) {
				if (v.getImg() != null) {
					v.getShape().setFill(new ImagePattern(v.getImg()));
				}
			}
			// TODO- line styles zu gestrichelt - zumindest wenn die Quelle ein
			// Dokument ist
			for (Edge e : network.getEdges()) {
				if (e.getOrigin().getType().equals("document")) {
					e.setDashed(true);
				}
			}

		} else {
			// zeige wieder alles normal an
			for (Vertex v : network.getVertices()) {
				v.getShape().setFill(Color.WHITE);
			}
			// TODO- line styles zurücksetzen
			for (Edge e : network.getEdges()) {
				if (e.getOrigin().getType().equals("document")) {
					e.setDashed(false);
				}
			}
		}
		clearVertices();
		clearLines();
		showNetwork();
	}

	/**
	 * Funktion die ausgeführt wird, wenn der Centrality-switch geklicket wird.
	 */
	public void centralityToggleButtonClicked() {

		try {
			for (Vertex v : network.getVertices()) {
				pannablePane.getChildren().remove(v.getClosenessLabel());
				pannablePane.getChildren().remove(v.getBetweennessLabel());
				pannablePane.getChildren().remove(v.getDegreeLabel());
			}

			// if (legendHBox.getChildren().contains(legendImageView)) {
			legendHBox.getChildren().remove(legendImageView);
			// }

			// show centralities
			if (centralitySwitchButton.switchOnProperty().get() == true) {
				for (Vertex v : network.getVertices()) {
					if (closenessCheckBox.isSelected()) {
						pannablePane.getChildren().add(v.getClosenessLabel());
					}
					if (betweennessCheckBox.isSelected()) {
						pannablePane.getChildren().add(v.getBetweennessLabel());
					}
					if (degreeCheckBox.isSelected()) {
						pannablePane.getChildren().add(v.getDegreeLabel());
					}

					if (!legendHBox.getChildren().contains(legendImageView)) {
						legendHBox.getChildren().add(legendImageView);
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Funktion die ausgeführt wird wenn der Weightings-SwitchButton geklickt wird.
	 */
	public void weightsToggleButtonClicked() {
		updateGraphics();
	}

	/**
	 * Funktion die nach Auswahl einer Datei aufgerufen wird und initial die
	 * Betweenness-, Closenesszentralität und den Grad eines Knotens berechnet.
	 */
	public void computeCentralities() {
		// berechne zentralitäten
		ClosenessCentrality cc = new ClosenessCentrality(network);
		// cc.compute();

		BetweennessCentrality bc = new BetweennessCentrality(network);
		// bc.compute();

		// Kantengrad eingehend/ausgehend
		DegreeCentrality dc = new DegreeCentrality(network);
		// dc.compute();

		// Dichte des Netzwerks
		Density density = new Density(network);
		// densityLabel.setText(density.getDensity());
	}

	/**
	 * Entfernt alle Vertices vom AnchorPane.
	 *
	 */
	private void clearVertices() {
		for (Vertex v : network.getVertices()) {
			pannablePane.getChildren().remove(v.getShape());
			pannablePane.getChildren().remove(v.getNameLabel());
			pannablePane.getChildren().remove(v.getClosenessLabel());
			pannablePane.getChildren().remove(v.getBetweennessLabel());
			pannablePane.getChildren().remove(v.getDegreeLabel());
		}
	}

	/**
	 * Entfernt alle WaterPipe-Lines vom Pane.
	 *
	 */
	private void clearLines() {

		try {
			for (Edge e : network.getEdges()) {
				// e.getShape().getTransforms().clear();
				for (Node n : e.getShapes()) {
					n.getTransforms().clear();
					pannablePane.getChildren().remove(n);
				}
				pannablePane.getChildren().remove(e.getWeightingLabel());
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Visualisiert das Netzwerk aus der XML-Datei. Wird aufgerufen, nachdem �ber
	 * den FileChooser eine Datei ausgew�hlt wurde.
	 *
	 */
	private void showNetwork() {

		List<Vertex> vertices = network.getVertices();

		for (Vertex v : vertices) {
			pannablePane.getChildren().add(v.getShape());
			v.getNameLabel().setLayoutX(v.getX() - ((v.getName().length() / 2) * 8));
			v.getNameLabel().setLayoutY(v.getY() + v.getHeight() + 10);
			pannablePane.getChildren().add(v.getNameLabel());

			v.getClosenessLabel().setLayoutX(v.getX() - 3 * ((v.getClosenessLabel().getText().length() / 2) * 8));
			v.getClosenessLabel().setLayoutY(v.getY() - v.getHeight() * 3);

			v.getBetweennessLabel().setLayoutX(v.getX() + ((v.getBetweennessLabel().getText().length() / 2) * 8));
			v.getBetweennessLabel().setLayoutY(v.getY() - v.getHeight() * 3);

			v.getDegreeLabel().setLayoutX(v.getX() + ((v.getDegreeLabel().getText().length() / 2) * 8));
			v.getDegreeLabel().setLayoutY(v.getY() + v.getHeight() / 2);
		}
		showEdges();
	}

	/**
	 * Setzt das Netzwerk auf den Ausgangszustand zur�ck. Dabei werden alle
	 * Informationsfl�sse gel�scht.
	 */
	private void resetNetworkFlow() {
		for (Edge e : network.getEdges()) {
			e.setFlow(0);
		}
	}

	/**
	 * Clear Vertex highlightings.
	 */
	private void clearVertexHighlights() {
		for (Vertex v : network.getVertices()) {
			v.getShape().setEffect(null);
		}
	}

	/**
	 * Aktualisiert die Elemente des Canvas, nach einer �nderung. Das Netzwerk aus
	 * der XML wird angezeigt.
	 */
	private void updateGraphics() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		clearLines();
		showEdges();
	}

	/**
	 * Zeichnet die Edges auf das Canvas. Besonders ist dabei zu beachten, dass
	 * f�r bidirektionale Edges besonders vorgegangen wird: Das Weighting soll 2x
	 * gezeichnet werden, so dass jeweils Flow/Kapazit�t in Flussrichtung zeigen.
	 *
	 */

	/**
	 * Funktion die die Edges visualisiert, indem die Kanten als Shape zum Pane
	 * hinzugef�gt werden.
	 */
	private void showEdges(String... mode) {

		if (edgeWeighting == visualizationType.WATERPIPES) {
			drawWaterPipes();
			return;
		}

		List<Edge> edges = network.getEdges();
		boolean weightingDrawn = false;
		List<Edge> drawnEdges = new ArrayList<Edge>();

		for (Edge e : edges) {

			if (mode.length > 0) {
				if (mode[0].equals("informationExpansion")) {
					e.show(pannablePane);
				}
			} else {
				if (e.getFlow() > 0) {
					if (highlightFlow) {
						e.setColor(Color.ORANGE);
					} else {
						e.setColor(Color.BLACK);
					}
				} else {
					e.setColor(Color.GREY);
				}
				e.show(pannablePane);
			}

			weightingDrawn = false;
			for (Edge e2 : edges) {
				if ((e.getOrigin() == e2.getDestination()) && (e.getDestination() == e2.getOrigin())) {

					if (!drawnEdges.contains(e2) && !drawnEdges.contains(e)) {

						e.showBidirectionalWeightingLabel(pannablePane, e.getOrigin().getX(), e.getOrigin().getY(),
								(int) e.getFlow(), (int) e.getCapacity(), e.getFlowDistance(),
								e.getDestination().getX(), e.getDestination().getY(), (int) e2.getFlow(),
								(int) e2.getCapacity(), e2.getFlowDistance(), edgeWeighting);
						drawnEdges.add(e);
					}
					weightingDrawn = true;
				}
			}
			if (!weightingDrawn) {
				e.showWeightingLabel(pannablePane, e.getOrigin().getX(), e.getOrigin().getY(),
						e.getDestination().getX(), e.getDestination().getY(), edgeWeighting);
				drawnEdges.add(e);
			}
		}

		// bringe nicht verwendete kanten in den hintergrund, damit sie nicht
		// �ber
		// interessanten kanten liegen: Layering soll wie folgt sein (von oben
		// nach
		// unten):
		// 1. NameLabels + Centralitätslabels
		// 2. Pfeilspitzen mit farbe
		// 3. Pfeilspitzen grau
		// 4. Vertex-Shapes
		// 5. edges mit farbe
		// 6. edges grau

		for (Edge e : edges) {
			if (e.getColor() != Color.GRAY) {
				for (Node n : e.getShapes()) {
					n.toFront();
				}
			}
		}
		for (Vertex v : network.getVertices()) {
			v.getShape().toFront();
		}
		for (Edge e : edges) {
			if (e.getColor() == Color.GRAY) {
				for (Node n : e.getShapes()) {
					if (n.getClass().toString().contains("javafx.scene.shape.Polygon")) {
						n.toFront();
					}
				}
			}
		}
		for (Edge e : edges) {
			if (e.getColor() != Color.GRAY) {
				for (Node n : e.getShapes()) {
					if (n.getClass().toString().contains("javafx.scene.shape.Polygon")) {
						n.toFront();
					}
				}
			}
		}
		for (Vertex v : network.getVertices()) {
			v.getNameLabel().toFront();
			v.getBetweennessLabel().toFront();
			v.getClosenessLabel().toFront();
			v.getDegreeLabel().toFront();
		}

		// entferne Edge-Labels wenn toggleButton nicht ausgewählt
		if (!weightsSwitchButton.switchOnProperty().get()) {
			for (Edge e : network.getEdges()) {
				pannablePane.getChildren().remove(e.getWeightingLabel());
			}
		}
	}

	/**
	 * Methode die aufgerufen wird wenn der RadioButton f�r das Anzeigen des
	 * Kanalsystems ausgew�hlt wurde. Hier werden die Kanten als
	 *
	 */
	private void drawWaterPipes() {

		// Verteile die Kapazit�ten auf eine strokeWidth zwischen 5 und 15
		int minWidth = 6;
		int maxWidth = 14;

		int minCapacity = Integer.MAX_VALUE;
		int maxCapacity = Integer.MIN_VALUE;

		HashMap<Integer, Double> capacitiesHashMap = network.getCapacitiesHashMap();

		for (Integer capacity : network.getCapacities()) {
			if (capacity > maxCapacity) {
				maxCapacity = capacity;
			}
			if (capacity < minCapacity) {
				minCapacity = capacity;
			}
		}

		for (Integer capacity : network.getCapacities()) {
			double value = minWidth
					+ (((double) (capacity - minCapacity) / (maxCapacity - minCapacity)) * (maxWidth - minWidth));
			capacitiesHashMap.replace(capacity, 0.0, value);
		}

		for (Edge e : network.getEdges()) {
			int x1 = e.getOrigin().getX();
			int y1 = e.getOrigin().getY();

			int x2 = e.getDestination().getX();
			int y2 = e.getDestination().getY();

			int dx = x2 - x1;
			int dy = y2 - y1;

			int length = (int) Math.sqrt(dx * dx + dy * dy);
			double angle = Math.atan2(dy, dx);

			// try rects:
			double width = capacitiesHashMap.get((int) e.getCapacity());

			Rectangle baseRect = new Rectangle(x1, y1 - width / 2, length, width);
			baseRect.setFill(Color.WHITE);
			baseRect.setStrokeType(StrokeType.OUTSIDE);
			baseRect.setStroke(Color.BLACK);
			baseRect.setStrokeWidth(2);

			Rectangle fillRect = new Rectangle(x1, y1 - width / 2, length, width * (e.getFlow() / e.getCapacity()));
			fillRect.setFill(Color.BLUE);

			// transform:
			baseRect.getTransforms().add(new Rotate(Math.toDegrees(angle), x1, y1));
			fillRect.getTransforms().add(new Rotate(Math.toDegrees(angle), x1, y1));

			e.getShapes().add(baseRect);
			e.getShapes().add(fillRect);

			pannablePane.getChildren().add(baseRect);
			pannablePane.getChildren().add(fillRect);

			fillRect.toFront();
			baseRect.toBack();

			e.getOrigin().getShape().toFront();
			e.getOrigin().getNameLabel().toFront();
			e.getDestination().getShape().toFront();
			e.getDestination().getNameLabel().toFront();
		}
	}
}
