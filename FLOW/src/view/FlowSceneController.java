package view;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.EdmondsKarp;
import control.Parser;
import helper.PannablePane;
import helper.SceneGestures;
import javafx.event.ActionEvent;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Edge;
import model.Network;
import model.Vertex;

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
	private AnchorPane anchor;
	
	private PannablePane pannablePane;
	
	//@FXML
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
	 * Übergibt die Stage, sodass der FileChooser verwendet werden kann.
	 * 
	 * @param stage Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;
		
		//Order Elements and set size-Properties
		menuBar.toFront();
		menuBar.prefWidthProperty().bind(anchor.widthProperty());
		
		pannablePane = new PannablePane();
		anchor.getChildren().add(pannablePane);
		
		anchor.setPrefWidth(stage.getScene().getWidth());
		anchor.setPrefHeight(stage.getScene().getHeight());
		anchor.autosize();
		anchor.toBack();
		
		informationPane.toFront();
		
		pannablePane.setPrefWidth(anchor.widthProperty().get());
		pannablePane.setPrefHeight(anchor.heightProperty().get());
		pannablePane.autosize();
		
		canvas = new Canvas();
		pannablePane.getChildren().add(canvas);		
		
		canvas.widthProperty().bind(pannablePane.widthProperty());
		canvas.heightProperty().bind(pannablePane.heightProperty());
		canvas.autosize();
		
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.BEIGE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
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
		informationPane.visibleProperty().set(false);
		
		FileChooser fc = new FileChooser();
		fc.setTitle("Big FLOW");
		fc.setInitialDirectory(new File(System.getProperty("user.dir") + "/resource"));
		fc.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
		
		file = fc.showOpenDialog(stage);
		
		if (file != null) {
			System.out.println("File: " + file);
		}
		
		parser = new Parser(file);
		parser.parse();
		network = parser.getData();
		
		showNetwork();
	}
	
	/**
	 * Funktion die beim Klicken von Compute --> network flow aufgerufen wird.
	 * In dieser Funktion sollen Quelle und Senke gewählt und der Algrithmus gestartet werden.
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
	 * @param e
	 */
	public void sourceAndSinkAccepted(ActionEvent e) {
		if (source != null && sink != null) {
			for (Vertex v: network.getVertices()) {
				Shape shape = v.getShape();
				shape.setOnMouseEntered(null);
				shape.setOnMouseClicked(null);
				shape.setOnMouseExited(null);
			}
			
			EdmondsKarp edmondsKarp = new EdmondsKarp(network, source, sink);
			maxFlowLabel.setText(edmondsKarp.getMaxFlow() + "");
			
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
	 * Funktion die das Programm beendet. Sie wird über das UI aufgerufen.
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
			pannablePane.getChildren().remove(v);
		}
	}

	/**
	 * Visualisiert das Netzwerk aus der XML-Datei.
	 * Wird aufgerufen, nachdem über den FileChooser eine Datei ausgewählt wurde.
	 * 
	 */
	private void showNetwork() {
		
		List<Vertex> vertices = network.getVertices();
		
		for (Vertex v : vertices) {
			//gc.strokeOval(v.getCenterX(), v.getCenterY(), v.getRadius(), v.getRadius());
			gc.strokeText(v.getName(), v.getX() - 15, v.getY() + 25);
			pannablePane.getChildren().add(v.getShape());
		}
		
		List<Edge> edges = network.getEdges();
		
		for (Edge e : edges) {
			e.draw(gc);
		}
		
		System.out.println(gc.getFill().toString());
	}
}
