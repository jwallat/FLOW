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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
 * ermoeglicht.
 *
 * @author Jonas Wallat
 *
 */
public class FlowSceneController implements Initializable {

	@FXML
	private MenuBar menuBar;

	@FXML
	private HBox outerHBox;

	@FXML
	private HBox hBox;

	@FXML
	private AnchorPane anchor;

	private PannablePane pannablePane;

	@FXML
	private TitledPane informationPane;

	@FXML
	private HBox toggleButtonHBox;

	private SwitchButton switchButton;

	@FXML
	private HBox pathsToggleButtonHBox;

	private SwitchButton pathsSwitchButton;

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
	private CheckBox highlightCentralitiesCheckBox;

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
		// Faegt dem MenuPanel auf der rechten seite einen Switch button zum an
		// und ausstellen der FLOW-Notation hinzu.
		this.switchButton = new SwitchButton(this, "flow");
		toggleButtonHBox.getChildren().add(switchButton);

		this.pathsSwitchButton = new SwitchButton(this, "paths");
		pathsToggleButtonHBox.getChildren().add(pathsSwitchButton);

		this.centralitySwitchButton = new SwitchButton(this, "centrality");
		centralityToggleButtonHBox.getChildren().add(centralitySwitchButton);

		this.weightsSwitchButton = new SwitchButton(this, "weights");
		weightsToggleButtonHBox.getChildren().add(weightsSwitchButton);

		// Initialisiere die Legende der Zentralitaeten
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
	 * Uebergibt die Stage, sodass der FileChooser verwendet werden kann.
	 *
	 * @param stage
	 *            Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;

		stage.setMaximized(true);

		stage.getScene().getWindow().widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
					Number newSceneWidth) {
				System.out.println("Width: " + newSceneWidth);
				System.out.println("hBox: " + hBox.getWidth() + " pref: " + hBox.getPrefWidth());
				System.out.println("outer: " + outerHBox.getWidth());
				hBox.setPrefWidth(stage.getScene().getWidth());
				hBox.setMaxHeight(stage.getScene().getHeight());
				hBox.autosize();
				pannablePane.autosize();
				anchor.autosize();
			}
		});

		// Order Elements and set size-Properties
		hBox.setPrefWidth(stage.getScene().getWidth());
		hBox.setPrefHeight(stage.getScene().getHeight());
		hBox.autosize();
		hBox.toBack();

		menuBar.toFront();
		menuBar.prefWidthProperty().bind(anchor.widthProperty());

		pannablePane = new PannablePane();
		anchor.getChildren().add(pannablePane);

		anchor.prefWidthProperty().bind(hBox.widthProperty());
		anchor.prefHeightProperty().bind(hBox.heightProperty());
		anchor.autosize();
		anchor.toBack();

		pannablePane.prefWidthProperty().bind(anchor.widthProperty());
		pannablePane.prefHeightProperty().bind(anchor.heightProperty());
		pannablePane.autosize();

		pannablePane.toFront();
		informationPane.toFront();

		SceneGestures sceneGestures = new SceneGestures(pannablePane);
		stage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		stage.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		stage.getScene().addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
	}

	/**
	 * Oeffnet einen FileChooser fuer XML-Dateien.
	 *
	 */
	public void openFile() {

		if (network != null) {
			clearVertices();
			clearLines();
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
	 * Funktion die das Programm beendet. Sie wird ueber das UI aufgerufen.
	 *
	 */
	public void close() {
		System.exit(0);
	}

	/**
	 * Funktion die bei Auswahl des Visualized-Weightings Radiobuttons ausgefuehrt
	 * wird. Die drei Radiobuttons schliessen sich gegenseitig aus und geben an,
	 * welche Information auf den Kanten angezeigt wird. Der Enum VisualizationType
	 * wird entsprechend gesetzt.
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
	 * Funktion die bei Auswahl des Radiobuttons ausgefuehrt wird. Die beiden
	 * Radiobuttons schliessen sich gegenseitig aus und geben an, welche Information
	 * auf den Kanten angezeigt wird. Der Enum VisualizationType wird entsprechend
	 * gesetzt.
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
	 * Funktion die bei Auswahl des Radiobuttons ausgefuehrt wird. Die drei
	 * Radiobuttons schliessen sich gegenseitig aus und geben an, welche Information
	 * auf den Kanten angezeigt wird.
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
	 * Oeffnet das InformationPane und laesst den Nutzer Source/Sink mit klick
	 * auswaehlen. Wird ausgefuehrt, wenn der "select vertices" Button geklickt
	 * wird. Das Netzwerk wird zusaetzlich in den Ausgangszustand zurueckgefuehrt.
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

		// setze alles zurueck
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
					stage.getScene().setCursor(Cursor.DEFAULT);
				}
			});
		}
	}

	/**
	 * Funktion die bei Klick auf den "Calculate"-Button aufgerufen wird. Daraufhin
	 * werden sowohl der MaxFLOW als auch FLOW-Space berechnet und entsprechend in
	 * InoformationPane angezeigt.
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
	 * Funktion die bei Klick des highlightFlow Toggle-Buttons ausgefuehrt wird.
	 *
	 */
	public void highlightFlowButtonClicked() {

		if (file != null) {
			highlightFlow = highlightFlowButton.isSelected();

			updateGraphics();
		}
	}

	/**
	 * Funktion die bei Klick des Info-Expansion-Forewards-Buttons ausgefuehrt wird.
	 */
	public void infoExpansionForewardsButtonClicked() {
		if (iE != null) {
			iE.iterateForewards();
			clearLines();
			showEdges("informationExpansion");
		}
	}

	/**
	 * Funktion die bei Klick des Info-Expansion-Backwards-Buttons ausgefuehrt wird.
	 */
	public void infoExpansionBackwardsButtonClicked() {
		if (iE != null) {
			iE.iterateBackwards();
			clearLines();
			showEdges("informationExpansion");
		}
	}

	/**
	 * Funktion die bei Klicken des "Select Source" Buttons ausgefuehrt wird. Es die
	 * hier ausgewaehlte Source wird fuer die Informationsausbreitung verwendet.
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

		// setze alles zurueck
		clearVertexHighlights();
		resetNetworkFlow();
		updateGraphics();

		maxFlowLabel.setText("");
		flowDistanceLabel.setText("");

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
	 * Funktion die bei Klicken des "Go"-Buttons ausgefuehrt wird. Dabei werden die
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
	 * Funktion die bei Klicken des "Go"-Buttons ausgefuehrt wird. Dabei werden die
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
	 * Click-Methode die ausgefuehrt wenn der computeOutput-Button geklickt wird.
	 * Daraufhin werden Listen berechnet, in den fuer die gegebene Prozentzahl fuer
	 * alle Schritte jeweils alle Knoten aufgelistet werden, die weniger
	 * als/mindestens die gegebene Prozentzahl aller Knoten erreichen.
	 *
	 */
	public void computeOutputButtonClicked() {
		if (percentageTextField.getText().matches("\\d+")) {
			double percentage = Double.parseDouble(percentageTextField.getText());

			if (percentage <= 100) {
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
	 * Funktion die ausgefuehrt wird, wenn der FLOW-Notations switch geklickt wird.
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
			// line styles zu gestrichelt - zumindest wenn die Quelle ein
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
			// line styles zuruecksetzen
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
	 * Funktion die ausgefuehrt wird, wenn der Paths-switch geklickt wird. Ist der
	 * switch aktiviert sollen alle Pfade zwischen den Knoten hervorgehoben werden.
	 * Ist er deaktiviert sollen nur die Pfade hervorgehoben werden, die auch in der
	 * MaxFLOW-Berechnung Informationen transportieren.
	 */
	public void pathsToggleButtonClicked() {
		calculateButtonClicked();
	}

	/**
	 * Funktion die ausgefuehrt wird, wenn der Centrality-switch geklicket wird.
	 */
	public void centralityToggleButtonClicked() {

		try {
			for (Vertex v : network.getVertices()) {
				pannablePane.getChildren().remove(v.getClosenessLabel());
				pannablePane.getChildren().remove(v.getBetweennessLabel());
				pannablePane.getChildren().remove(v.getDegreeLabel());
			}

			legendHBox.getChildren().remove(legendImageView);

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
	 * Funktion die beim Klicken der Highlight Centralities-CheckBox ausgefuehrt
	 * wird. Ist diese markiert sollen extreme Zentralitaeten gehighlighted werden.
	 * Wird diese CheckBox abgewaehlt, soll das Highlighting entfernt werden. Die
	 * Bedingung, welche Zentralitaeten extrem sind, soll hier durch eine Formel
	 * eingesetzt werden koennen.
	 */
	public void highlightCentralitiesCheckBoxClicked() {
		// entferne alle Zentralitaetshighlights
		for (Vertex v : network.getVertices()) {
			v.getBetweennessLabel().setId("centrality");
			v.getClosenessLabel().setId("centrality");
			v.getDegreeLabel().setId("centrality");
		}

		if (highlightCentralitiesCheckBox.isSelected()) {
			// Spezifiziere neue Bedingungen
			// @Jil: An dieser Stelle musst du deine Highlight-Bedingungen spezifizieren.
			// Wenn du Bedingungen wie "nur die top 10% der hoechsten
			// Closeness-Zentralit‰ten" abfragen moechtest, m¸sstest du ¸ber alle Knoten
			// iterieren und den Zahlenwert der Closeness-Zentralit‰t f¸r deine
			// Anfrage bestimmen.
			double betweennessThreshold = 0.3;
			double closenessThreshold = 0.05;
			int degreeThresholdIn = 4;
			int degreeThresholdOut = 10;

			// Highlighte alles Zentralitaeten auf die, die Bedingung zutrifft
			for (Vertex v : network.getVertices()) {
				double betweennessCentrality = Double.valueOf(v.getBetweennessLabel().getText().replaceAll(",", "."));
				double closenessCentrality = Double.valueOf(v.getClosenessLabel().getText().replaceAll(",", "."));
				int degreeIn = Integer
						.valueOf(v.getDegreeLabel().getText().substring(1, v.getDegreeLabel().getText().indexOf(",")));
				int degreeOut = Integer.valueOf(v.getDegreeLabel().getText().substring(
						v.getDegreeLabel().getText().indexOf(",") + 1, v.getDegreeLabel().getText().indexOf(")")));

				if (betweennessCentrality >= betweennessThreshold) {
					v.getBetweennessLabel().setId("betweenness_highlighted");
				}
				if (closenessCentrality >= closenessThreshold) {
					v.getClosenessLabel().setId("closeness_highlighted");
				}
				if (degreeIn >= degreeThresholdIn || degreeOut >= degreeThresholdOut) {
					v.getDegreeLabel().setId("degree_highlighted");
				}
			}
		}
	}

	/**
	 * Funktion die ausgefuehrt wird wenn der Weightings-SwitchButton geklickt wird.
	 */
	public void weightsToggleButtonClicked() {
		updateGraphics();
	}

	/**
	 * Funktion die nach Auswahl einer Datei aufgerufen wird und initial die
	 * Betweenness-, Closenesszentralit√§t und den Grad eines Knotens berechnet.
	 */
	public void computeCentralities() {
		// berechne zentralitaeten
		ClosenessCentrality cc = new ClosenessCentrality(network);
		cc.compute();

		BetweennessCentrality bc = new BetweennessCentrality(network);
		bc.compute();

		// Kantengrad eingehend/ausgehend
		DegreeCentrality dc = new DegreeCentrality(network);
		dc.compute();

		// Dichte des Netzwerks
		Density density = new Density(network);
		densityLabel.setText(density.getDensity());
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
	 * Visualisiert das Netzwerk aus der XML-Datei. Wird aufgerufen, nachdem ueber
	 * den FileChooser eine Datei ausgewaehlt wurde.
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
	 * Setzt das Netzwerk auf den Ausgangszustand zurueck. Dabei werden alle
	 * Informationsfluesse geloescht.
	 */
	private void resetNetworkFlow() {
		for (Edge e : network.getEdges()) {
			e.setFlow(0);
			e.setOnAPath(false);
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
	 * Aktualisiert die Elemente des Canvas, nach einer Aenderung. Das Netzwerk aus
	 * der XML wird angezeigt.
	 */
	private void updateGraphics() {
		clearLines();
		showEdges();
	}

	/**
	 * Funktion die die Edges visualisiert, indem die Kanten als Shape zum Pane
	 * hinzugefuegt werden.
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
				if (pathsSwitchButton.switchOnProperty().get() == false) {
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
				} else {
					if (/* e in allPaths */e.isOnAPath()) {
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
		// ueber
		// interessanten kanten liegen: Layering soll wie folgt sein (von oben
		// nach
		// unten):
		// 1. NameLabels + Centralit√§tslabels
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

		// entferne Edge-Labels wenn toggleButton nicht ausgewaehlt
		if (!weightsSwitchButton.switchOnProperty().get()) {
			for (Edge e : network.getEdges()) {
				pannablePane.getChildren().remove(e.getWeightingLabel());
			}
		}
	}

	/**
	 * Methode die aufgerufen wird wenn der RadioButton fuer das Anzeigen des
	 * Kanalsystems ausgewaehlt wurde. Hier werden die Kanten als
	 *
	 */
	private void drawWaterPipes() {

		// Verteile die Kapazitaeten auf eine strokeWidth zwischen 5 und 15
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
