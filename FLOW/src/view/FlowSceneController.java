package view;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.EdmondsKarp;
import control.Parser;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Edge;
import model.Network;
import model.Vertex;
import util.PannablePane;
import util.SceneGestures;

/**
 * Kontroller zur FlowScene.fxml. Hier werden alle Interaktionen mit dem UI erm�glicht.
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
	
	@FXML
	private GraphicsContext gc;
	
	@FXML
	private TitledPane informationPane;
	
	@FXML
	private Label sourceLabel;
	
	@FXML 
	private Label sinkLabel;
	
	@FXML
	private Label maxFlowLabel;
	
	@FXML 
	private Button acceptButton;
	
	private Stage stage;
	private File file;
	private Parser parser;
	private Network network;
	private Vertex source;
	private Vertex sink;
	
	private static final DropShadow highlightSource = new DropShadow(20, Color.GOLDENROD);
	private static final DropShadow highlightSink = new DropShadow(20, Color.GREEN);

	
	
	/**
	 * Initialisierungs-Mehtode, die aufgerufen wird, wenn in der Main.java die FlowScene.fxml geladen wird.
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		
	}
	
	/**
	 * �bergibt die Stage, sodass der FileChooser verwendet werden kann.
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
	 * �ffnet einen FileChooser fuer XML-Dateien.
	 * 
	 */
	public void openFile() {
		
		if (network != null) {
			clearVertices();
			gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		}
		informationPane.visibleProperty().set(false);
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Big FLOW");
		fc.setInitialDirectory(new File(System.getProperty("user.dir") + "/resource"));
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		
		file = fc.showOpenDialog(stage);
		
		if (file == null) {
			System.out.println("Fehler beim lesen der Datei!");
		}
		
		parser = new Parser(file);
		parser.parse();
		network = parser.getData();
		
		showNetwork();
	}
	
	/**
	 * Funktion die beim Klicken von Compute --> network flow aufgerufen wird.
	 * In dieser Funktion sollen Quelle und Senke gew�hlt und der Algrithmus gestartet werden.
	 * 
	 */
	public void computeNetworkFlow() {
		
		if (source != null) {
			source.getShape().setEffect(null);
			source = null;
		}
		if (sink != null) {
			sink.getShape().setEffect(null);
			sink = null;
		}
		maxFlowLabel.setText("");
		sourceLabel.setText("");
		sinkLabel.setText("");
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Network Flow");
		alert.setHeaderText(null);
		alert.setContentText("Select the source and sink!");

		alert.showAndWait();
		
		selectSourceAndSink();
		
		
		//EDMONDS-KARP
	}
	
	/**
	 * 
	 * 
	 */
	private void selectSourceAndSink() {
		informationPane.visibleProperty().set(true);

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
	 * Funktion die bei Klick auf den "Accept"-Button aufgerufen wird.
	 * 
	 */
	public void sourceAndSinkAccepted() {
		if (source != null && sink != null) {
			for (Vertex v: network.getVertices()) {
				Shape shape = v.getShape();
				shape.setOnMouseEntered(null);
				shape.setOnMouseClicked(null);
				shape.setOnMouseExited(null);
			}
			
			EdmondsKarp edmondsKarp = new EdmondsKarp(network); 
			if (!edmondsKarp.areConnected(source, sink)) {
				maxFlowLabel.setText("Not connected");
			}
			else {
				edmondsKarp.run(source, sink);
				updateGraphics();
				maxFlowLabel.setText(edmondsKarp.getMaxFlow() + "");
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Network Flow");
			alert.setHeaderText(null);
			alert.setContentText("Select the source AND sink!");

			alert.showAndWait();
		}
	}
	
	/**
	 * Funktion die das Programm beendet. Sie wird �ber das UI aufgerufen.
	 * 
	 */
	public void close() {
		System.exit(0);
	}
	
	/**
	 * Entfernt alle Vertices vom AnchorPane.
	 * 
	 */
	private void clearVertices() {
		for (Vertex v : network.getVertices()) {
			pannablePane.getChildren().remove(v.getShape());
		}
	}

	/**
	 * Visualisiert das Netzwerk aus der XML-Datei.
	 * Wird aufgerufen, nachdem �ber den FileChooser eine Datei ausgew�hlt wurde.
	 * 
	 */
	private void showNetwork() {
		
		List<Vertex> vertices = network.getVertices();
		network.prepareNetwork();
		
		for (Vertex v : vertices) {
			gc.strokeText(v.getName(), v.getX() - ((v.getName().length() / 2) * 6), v.getY() + 25);
			pannablePane.getChildren().add(v.getShape());
		}
		
		List<Edge> edges = network.getEdges();
		boolean weightingDrawn = false;
		List<Edge> drawnEdges = new ArrayList<Edge>();
		
		for (Edge e : edges) {
			e.draw(gc);
			weightingDrawn = false;
			for (Edge e2 : edges) {
				if ((e.getOrigin() == e2.getDestination()) && (e.getDestination() == e2.getOrigin())) {
					
					if (!drawnEdges.contains(e2) && !drawnEdges.contains(e)) {
						
						e.drawBidirectionalWeighting(gc, (int) e.getOrigin().getX(), (int) e.getOrigin().getY(), (int) e.getFlow(), (int) e.getCapacity(),
							(int) e.getDestination().getX(), (int) e.getDestination().getY(), (int) e2.getFlow(), (int) e2.getCapacity());
						drawnEdges.add(e);
						/*System.out.println("BI:");
						System.out.println(e);
						System.out.println(e2);
						System.out.println("/BI");*/
					}	
					weightingDrawn = true;
				}
			}
			if (!weightingDrawn) {
				e.drawWeighting(gc, e.getOrigin().getX(), e.getOrigin().getY(), e.getDestination().getX(), e.getDestination().getY());
				drawnEdges.add(e);
			}
		}
	}
	
	/**
	 * Aktualisiert die Elemente des Canvas, nach einer �nderung.
	 * 
	 */
	private void updateGraphics() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		//gc.setFill(Color.WHITE);
		//gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		List<Vertex> vertices = network.getVertices();
		for (Vertex v : vertices) {
			gc.strokeText(v.getName(), v.getX() - ((v.getName().length() / 2) * 6), v.getY() + 25);
		}
		
		List<Edge> edges = network.getEdges();
		boolean weightingDrawn = false;
		List<Edge> drawnEdges = new ArrayList<Edge>();
		
		for (Edge e : edges) {
			e.draw(gc);
			weightingDrawn = false;
			for (Edge e2 : edges) {
				if ((e.getOrigin() == e2.getDestination()) && (e.getDestination() == e2.getOrigin())) {
					
					if (!drawnEdges.contains(e2) && !drawnEdges.contains(e)) {
						
						e.drawBidirectionalWeighting(gc, (int) e.getOrigin().getX(), (int) e.getOrigin().getY(), (int) e.getFlow(), (int) e.getCapacity(),
							(int) e.getDestination().getX(), (int) e.getDestination().getY(), (int) e2.getFlow(), (int) e2.getCapacity());
						drawnEdges.add(e);
						/*System.out.println("BI:");
						System.out.println(e);
						System.out.println(e2);
						System.out.println("/BI");*/
					}	
					weightingDrawn = true;
				}
			}
			if (!weightingDrawn) {
				e.drawWeighting(gc, e.getOrigin().getX(), e.getOrigin().getY(), e.getDestination().getX(), e.getDestination().getY());
				drawnEdges.add(e);
			}
		}
	}
}
