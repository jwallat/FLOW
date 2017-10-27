package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.EdmondsKarp;
import algorithm.FlowDistance;
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
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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

/**
 * Kontroller zur FlowScene.fxml. Hier werden alle Interaktionen mit dem UI ermöglicht.
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
	
	private static final DropShadow highlightSource = new DropShadow(BlurType.GAUSSIAN, Color.VIOLET.darker().darker(), 30, 0.7, 0, 0);
	private static final DropShadow highlightSink = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE, 30, 0.7, 0, 0);
	
	private static final DropShadow highlightInformationFlow = new DropShadow(BlurType.GAUSSIAN, Color.ORANGE.brighter(), 30, 0.7, 0, 0);

	
	/**
	 * Initialisierungs-Mehtode, die aufgerufen wird, wenn in der Main.java die FlowScene.fxml geladen wird.
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		
	}
	
	/**
	 * Übergibt die Stage, sodass der FileChooser verwendet werden kann.
	 * 
	 * @param stage Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;
		
		//Order Elements and set size-Properties
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
		/*
		Canvas anchorCanvas = new Canvas();
		anchorCanvas.widthProperty().bind(anchor.widthProperty());
		anchorCanvas.heightProperty().bind(anchor.heightProperty());
		anchorCanvas.autosize();
		
		GraphicsContext gc2 = anchorCanvas.getGraphicsContext2D();
		gc2 = anchorCanvas.getGraphicsContext2D();
		gc2.setFill(Color.RED);
		gc2.fillRect(0, 0, anchorCanvas.getWidth(), anchorCanvas.getHeight());
		
		anchor.getChildren().add(anchorCanvas);
		*/
		anchor.toBack();
		
		pannablePane.setPrefWidth(anchor.widthProperty().get());
		pannablePane.setPrefHeight(anchor.heightProperty().get());
		//pannablePane.setPrefWidth(3000);
		//pannablePane.setPrefHeight(3000);
		pannablePane.autosize();
		
		pannablePane.toFront();
		informationPane.toFront();
		
		
		canvas = new Canvas();
		pannablePane.getChildren().add(canvas);		
		
		//canvas.widthProperty().bind(pannablePane.widthProperty());
		//canvas.heightProperty().bind(pannablePane.heightProperty());
		canvas.setWidth(1900);
		canvas.setHeight(1000);
		canvas.autosize();
		
		gc = canvas.getGraphicsContext2D();
		//gc.setFill(Color.BEIGE);
		//gc.setFill(Color.WHITE);
		//gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		SceneGestures sceneGestures = new SceneGestures(pannablePane);
		stage.getScene().addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        stage.getScene().addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        stage.getScene().addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());        
 	}
	
	/**
	 * Öffnet einen FileChooser fuer XML-Dateien.
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

		showNetwork();
		
		informationPane.expandedProperty().set(true);
	}
	
	/**
	 * Funktion die das Programm beendet. Sie wird über das UI aufgerufen.
	 * 
	 */
	public void close() {
		System.exit(0);
	}
	
	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgeführt wird. 
	 * Die beiden Radiobuttons schließen sich gegenseitig aus und geben an, welche Information auf den 
	 * Kanten angezeigt wird.
	 * 
	 */
	public void networkFlowRadioButtonClicked() {
		if (file != null) {
			if (flowDistanceRadioButton.isSelected() || waterPipeRadioButton.isSelected()) {
				flowDistanceRadioButton.setSelected(false);
				waterPipeRadioButton.setSelected(false);
				
				edgeWeighting = visualizationType.NETWORKFLOW;
				
				updateGraphics();
			}
			else {
				networkFlowRadioButton.setSelected(true);
			}
		}
	}
	
	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgeführt wird. 
	 * Die beiden Radiobuttons schließen sich gegenseitig aus und geben an, welche Information auf den 
	 * Kanten angezeigt wird.
	 * 
	 */
	public void flowDistanceRadioButtonClicked() {
		if (file != null) {
			if (waterPipeRadioButton.isSelected() || networkFlowRadioButton.isSelected()) {
				networkFlowRadioButton.setSelected(false);
				waterPipeRadioButton.setSelected(false);
				edgeWeighting = visualizationType.FLOWDISTANCE;
			
				updateGraphics();
			}
			else {
				flowDistanceRadioButton.setSelected(true);
			}
		}
	}
	
	/**
	 * Funktion die bei Auswahl des Radiobuttons ausgeführt wird. 
	 * Die drei Radiobuttons schließen sich gegenseitig aus und geben an, welche Information auf den 
	 * Kanten angezeigt wird.
	 * 
	 */
	public void waterPipeRadioButtonClicked() {
		if (file != null) {
			if (flowDistanceRadioButton.isSelected() || networkFlowRadioButton.isSelected()) {
				networkFlowRadioButton.setSelected(false);
				flowDistanceRadioButton.setSelected(false);
				edgeWeighting = visualizationType.WATERPIPES;
				
				
				updateGraphics();
			}
			else {
				waterPipeRadioButton.setSelected(true);
			}
		}
	}
	
	/**
	 * Öffnet das InformationPane und lässt den Nutzer Source/Sink mit klick
	 * auswählen. Wird ausgeführt, wenn der "select vertices" Button geklickt wird.
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
		
		clearVertexHighlights();
		
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
					}
					else if (sink == null) {
						sink = v;
						v.getShape().setEffect(highlightSink);
						sinkLabel.setText(v.getName());
					}
					else {
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
                    //shape.setEffect(null);
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
			for (Vertex v: network.getVertices()) {
				Shape shape = v.getShape();
				shape.setOnMouseEntered(null);
				shape.setOnMouseClicked(null);
				shape.setOnMouseExited(null);
			}
			
			EdmondsKarp edmondsKarp = new EdmondsKarp(network); 
			FlowDistance flowDistance = new FlowDistance(network);
			
			if (!edmondsKarp.areConnected(source, sink)) {
				maxFlowLabel.setText("Not connected");
				flowDistanceLabel.setText("Not connected");
			}
			else {
				edmondsKarp.run(source, sink);
				updateGraphics();
				maxFlowLabel.setText(edmondsKarp.getMaxFlow() + "");
				
				flowDistance.run(source, sink);
				flowDistanceLabel.setText(flowDistance.getFlowDistance() + "");
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Calculation ERROR");
			alert.setHeaderText(null);
			alert.setContentText("Select the source AND sink!");

			alert.showAndWait();
		}
	}
	
	/**
	 * Funktion die bei Klick des Toggle-Buttons ausgeführt wird.
	 * 
	 */
	public void highlightFlowButtonClicked() {
		
		if (file != null) {
			highlightFlow = highlightFlowButton.isSelected();
		
			updateGraphics();
		}
	}

	/**
	 * Funktion die bei Klick des Info-Expansion-Buttons ausgeführt wird.
	 */
	public void infoExpansionButtonClicked() {
		if (iE != null) {
			iE.iterateForewards();
		} 
	}
	
	/**
	 * Funktion die bei Klicken des "Select Source" Buttons ausgeführt wird. 
	 * Es die hier ausgewählte Source wird für die Informationsausbreitung verwendet.
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
		clearVertexHighlights();
		
		maxFlowLabel.setText("");
		flowDistanceLabel.setText("");
		
		//stepCounterLabel.setText("0");
				
		
		informationPane.expandedProperty().set(true);

        for (Vertex v : network.getVertices()) {
        	Shape shape = v.getShape();
        	
        	shape.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stage.getScene().setCursor(Cursor.HAND);
                }
            });
        	
        	/*
        	 * To be reworked
        	 */
        	shape.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					centerVertex = v;
					iE = new InformationExpense(network, v);
					stepCounterLabel.textProperty().bind(iE.Step());
					verticesReachedLabel.textProperty().bind(iE.PercentageReached());
					//v.getShape().setEffect(highlightInformationFlow);
					centerVertexLabel.setText(v.getName());
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
	 * Entfernt alle Vertices vom AnchorPane.
	 * 
	 */
	private void clearVertices() {
		for (Vertex v : network.getVertices()) {
			pannablePane.getChildren().remove(v.getShape());
			pannablePane.getChildren().remove(v.getNameLabel());
		}
	}
	
	/**
	 * Entfernt alle WaterPipe-Lines vom Pane.
	 * 
	 */
	private void clearLines() {

		for (Edge e: network.getEdges()) {
			//e.getShape().getTransforms().clear();
			for (Node n : e.getShapes()) {
				n.getTransforms().clear();
				pannablePane.getChildren().remove(n);
			}
		}	
	}

	/**
	 * Visualisiert das Netzwerk aus der XML-Datei.
	 * Wird aufgerufen, nachdem über den FileChooser eine Datei ausgewählt wurde.
	 * 
	 */
	private void showNetwork() {
		
		List<Vertex> vertices = network.getVertices();
		//network.prepareNetwork();
		
		for (Vertex v : vertices) {
			//gc.strokeText(v.getName(), v.getX() - ((v.getName().length() / 2) * 6), v.getY() + 25);
			pannablePane.getChildren().add(v.getShape());
			v.getNameLabel().setLayoutX(v.getX() - ((v.getName().length() / 2) * 8));
			v.getNameLabel().setLayoutY(v.getY() + 18);
			pannablePane.getChildren().add(v.getNameLabel());
		}
		
		drawEdges();
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
	 * Aktualisiert die Elemente des Canvas, nach einer Änderung.
	 * Das Netzwerk aus der XML wird angezeigt.
	 */
	private void updateGraphics() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		clearLines();
		//gc.setFill(Color.WHITE);
		//gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		drawEdges();
	}
	
	/**
	 * Zeichnet die Edges auf das Canvas.
	 * Besonders ist dabei zu beachten, dass für bidirektionale Edges besonders vorgegangen wird:
	 * Das Weighting soll 2x gezeichnet werden, so dass jeweils Flow/Kapazität in Flussrichtung
	 * zeigen.
	 * 
	 */
	private void drawEdges() {
		
		if (edgeWeighting == visualizationType.WATERPIPES) {
			drawWaterPipes();
			return;
		}
		
		List<Edge> edges = network.getEdges();
		boolean weightingDrawn = false;
		List<Edge> drawnEdges = new ArrayList<Edge>();
		
		for (Edge e : edges) {
			
			if (e.getFlow() > 0)  {
				if (highlightFlow) {
					e.setColor(Color.ORANGE);
				}
				else {
					e.setColor(Color.BLACK);
				}
			}
			else {
				e.setColor(Color.GREY);
			}
			
			e.draw(gc);
			weightingDrawn = false;
			for (Edge e2 : edges) {
				if ((e.getOrigin() == e2.getDestination()) && (e.getDestination() == e2.getOrigin())) {
					
					if (e.getFlow() > 0 || e2.getFlow() > 0) {
						if (highlightFlow) {
							e.setColor(Color.ORANGE);
							e.draw(gc);
							e2.setColor(Color.ORANGE);
							e2.draw(gc);
						}
						else {
							e.setColor(Color.BLACK);
							e.draw(gc);
							e2.setColor(Color.BLACK);
							e2.draw(gc);
						}
						
					}
					else {
						e.setColor(Color.GREY);
					}
					
					if (!drawnEdges.contains(e2) && !drawnEdges.contains(e)) {
						
						e.drawBidirectionalWeighting(gc, (int) e.getOrigin().getX(), (int) e.getOrigin().getY(), (int) e.getFlow(), (int) e.getCapacity(), e.getFlowDistance(),
							(int) e.getDestination().getX(), (int) e.getDestination().getY(), (int) e2.getFlow(), (int) e2.getCapacity(), e2.getFlowDistance(), edgeWeighting);
						drawnEdges.add(e);
					}	
					weightingDrawn = true;
				}
			}
			if (!weightingDrawn) {
				e.drawWeighting(gc, e.getOrigin().getX(), e.getOrigin().getY(), e.getDestination().getX(), e.getDestination().getY(), edgeWeighting);
				drawnEdges.add(e);
			}
		}
	}
	
	/**
	 * Methode die aufgerufen wird wenn der RadioButton für das Anzeigen des Kanalsystems ausgewählt wurde.
	 * Hier werden die Kanten als 
	 * 
	 */
	private void drawWaterPipes() {
		
		// Verteile die Kapazitäten auf eine strokeWidth zwischen 5 und 15
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
			double value = minWidth + (( (double) (capacity - minCapacity) / (maxCapacity - minCapacity)) * (maxWidth - minWidth));
			capacitiesHashMap.replace(capacity, 0.0, value);	
		}
		
		
		for (Edge e: network.getEdges()) {
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
						
			
			Rectangle baseRect = new Rectangle(x1, y1 - width/2, length, width);
			baseRect.setFill(Color.WHITE);
			baseRect.setStrokeType(StrokeType.OUTSIDE);
			baseRect.setStroke(Color.BLACK);
			baseRect.setStrokeWidth(2);
			
			Rectangle fillRect = new Rectangle(x1, y1 - width/2, length, width * (e.getFlow() / e.getCapacity()));
			fillRect.setFill(Color.BLUE);
			
			//transform:
			baseRect.getTransforms().add(new Rotate(Math.toDegrees(angle), x1,y1));
			fillRect.getTransforms().add(new Rotate(Math.toDegrees(angle), x1,y1));
			
			e.getShapes().add(baseRect);
			e.getShapes().add(fillRect);
			
			pannablePane.getChildren().add(baseRect);
			pannablePane.getChildren().add(fillRect);
			
			fillRect.toFront();
			baseRect.toBack();
			
			
			/*Line line = e.getShape();
			
			line.setStartX(e.getOrigin().getX());
			line.setStartY(e.getOrigin().getY());
			
			line.setEndX(e.getOrigin().getX() + length);
			line.setEndY(e.getOrigin().getY());
			
			//line.getTransforms().add(new Rotate(Math.toDegrees(angle), x1,y1));
			*/			
			
			/*System.out.println("Cap: " + e.getCapacity());
			double strokeWidth = capacitiesHashMap.get((int) e.getCapacity());
			strokeWidth = (double) Math.round(strokeWidth);
			if (strokeWidth % 2 != 0) {
				strokeWidth--;
			}
			strokeWidth = 15.0;
			//line.setStrokeWidth(10);
			line.setStrokeWidth(strokeWidth);
			//double halfWidth = (double) strokeWidth / 2;
			//halfWidth = (double) Math.round(halfWidth * 100) / 100;
			int halfWidth = (int) (strokeWidth / 2);
			//halfWidth = Math.round(halfWidth);
			System.out.println(strokeWidth + ", " + halfWidth);
			
						
			
			double fillPercentage = e.getFlow() / e.getCapacity();
			if (fillPercentage == 1) {
				
				line.setStroke(new LinearGradient(0d, -halfWidth, 0d, halfWidth, false, CycleMethod.REFLECT,
					new Stop(0,Color.BLACK),
					new Stop(0.099,Color.BLACK),
					new Stop(0.1,Color.BLUE),
					new Stop(0.899,Color.BLUE),
					new Stop(0.9, Color.BLACK),
					new Stop(1, Color.BLACK)));
			}
			else {
				fillPercentage *= 0.8;
				fillPercentage += 0.09998;

				line.setStroke(new LinearGradient(0, -8, 0, 8, false, CycleMethod.REFLECT, 
					new Stop(0,Color.BLACK), 
					new Stop(0.099,Color.BLACK), 
					new Stop(0.1,Color.BLUE),
					new Stop(fillPercentage,Color.BLUE),
					new Stop(fillPercentage + 0.001, Color.WHITE),
					new Stop(0.89999999999999,Color.WHITE),
					new Stop(0.9, Color.BLACK),
					new Stop(1, Color.BLACK)));
			}
			*/
			
			//pannablePane.getChildren().add(line);
			//line.toBack();
			
			e.getOrigin().getShape().toFront();
			e.getOrigin().getNameLabel().toFront();
			e.getDestination().getShape().toFront();
			e.getDestination().getNameLabel().toFront();
		}
	}
}
