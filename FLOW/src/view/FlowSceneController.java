package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.EdmondsKarp;
import algorithm.FlowDistance;
import control.Parser;
import javafx.application.Platform;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
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
	private boolean highlightFlow = false;
	
	public enum visualizationType {
		NETWORKFLOW, FLOWDISTANCE, WATERPIPES 
	}
	
	private visualizationType edgeWeighting = visualizationType.NETWORKFLOW;
	
	//private static final DropShadow highlightSource = new DropShadow(50, Color.RED);
	private static final DropShadow highlightSource = new DropShadow(BlurType.GAUSSIAN, Color.RED, 30, 0.7, 0, 0);
	//private static final DropShadow highlightSink = new DropShadow(30, Color.GREEN);
	private static final DropShadow highlightSink = new DropShadow(BlurType.GAUSSIAN, Color.GREEN, 30, 0.7, 0, 0);

	
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
		
		canvas.widthProperty().bind(pannablePane.widthProperty());
		canvas.heightProperty().bind(pannablePane.heightProperty());
		//canvas.setWidth(1900);
		//canvas.setHeight(1000);
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
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Big FLOW");
		fc.setInitialDirectory(new File(System.getProperty("user.dir") + "/resource"));
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
		
		//informationPane.visibleProperty().set(false);
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
		
		maxFlowLabel.setText("");
		flowDistanceLabel.setText("");
		sourceLabel.setText("");
		sinkLabel.setText("");
		
		//informationPane.visibleProperty().set(true);
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
			e.getShape().getTransforms().clear();
			pannablePane.getChildren().remove(e.getShape());
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
						
						e.drawBidirectionalWeighting(gc, (int) e.getOrigin().getX(), (int) e.getOrigin().getY(), (int) e.getFlow(), (int) e.getCapacity(),
							(int) e.getDestination().getX(), (int) e.getDestination().getY(), (int) e2.getFlow(), (int) e2.getCapacity(), edgeWeighting);
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
		for (Edge e: network.getEdges()) {
			int x1 = e.getOrigin().getX();
			int y1 = e.getOrigin().getY();
			
			int x2 = e.getDestination().getX();
			int y2 = e.getDestination().getY();
			
			int dx = x2 - x1;
			int dy = y2 - y1;
			
			int length = (int) Math.sqrt(dx * dx + dy * dy);
			double angle = Math.atan2(dy, dx);
			
			
			Line line = e.getShape();
			
			line.setStartX(e.getOrigin().getX());
			line.setStartY(e.getOrigin().getY());
			
			line.setEndX(e.getOrigin().getX() + length);
			line.setEndY(e.getOrigin().getY());
			
			line.getTransforms().add(new Rotate(Math.toDegrees(angle), x1,y1));
	
			double fillPercentage = e.getFlow() / e.getCapacity();
			fillPercentage *= 0.6;
			fillPercentage += 0.19998;
			System.out.println("FillP: " + fillPercentage);
			
			//Scale stroke width by capacity!
			line.setStrokeWidth(10);
			line.setStroke(new LinearGradient(0d, -5d, 0d, 5d, false, CycleMethod.REFLECT, 
					new Stop(0,Color.BLACK), 
					new Stop(0.199,Color.BLACK), 
					new Stop(0.2,Color.BLUE),
					new Stop(fillPercentage,Color.BLUE),
					new Stop(fillPercentage + 0.001, Color.WHITE),
					new Stop(0.79999,Color.WHITE),
					new Stop(0.8, Color.BLACK),
					new Stop(1, Color.BLACK)));
			
			pannablePane.getChildren().add(line);
			line.toBack();
			
			e.getOrigin().getShape().toFront();
			e.getDestination().getShape().toFront();
		}
	}
}
