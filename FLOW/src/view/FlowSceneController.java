package view;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import control.Parser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
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
	private AnchorPane anchor;
	
	@FXML
	private Canvas canvas;
	
	@FXML
	private GraphicsContext gc;
	
	private Stage stage;
	private File file;
	private Parser parser;
	private Network network;
	
	
	/**
	 * Initialisierungs-Mehtode, die aufgerufen wird, wenn in der Main.java die FlowScene.fxml geladen wird.
	 * 
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//gc = canvas.getGraphicsContext2D();
	}
	
	/**
	 * Übergibt die Stage, sodass der FileChooser verwendet werden kann.
	 * 
	 * @param stage Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;
		gc = canvas.getGraphicsContext2D();
		canvas.isResizable();
		canvas.widthProperty().bind(anchor.widthProperty());
		canvas.heightProperty().bind(anchor.heightProperty());;
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
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Network Flow");
		alert.setHeaderText(null);
		alert.setContentText("Select the source!");

		alert.showAndWait();
		
		//Select Source/Sink
		
		
		//EDMONDS-KARP
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
			anchor.getChildren().remove(v);
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
			gc.strokeText(v.getName(), v.getCenterX() - 15, v.getCenterY() + 25);
			anchor.getChildren().add(v);
		}
		
		List<Edge> edges = network.getEdges();
		
		for (Edge e : edges) {
			e.draw(gc);
		}
		
		System.out.println(gc.getFill().toString());
	}
}
