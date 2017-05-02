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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Edge;
import model.Network;
import model.Vertex;

/**
 * Kontroller zur FlowScene.fxml. Hier werden alle Interaktionen mit dem UI ermoeglicht.
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
		// TODO Auto-generated method stub
		gc = canvas.getGraphicsContext2D();
	}
	
	/**
	 * Uebergibt die Stage, sodass der FileChooser verwendet werden kann.
	 * 
	 * @param stage Stage der FlowScene.fxml
	 */
	public void init(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * Oeffnet einen FileChooser fuer XML-Dateien.
	 * 
	 */
	public void openFile() {
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
		
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
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
	 * Funktion die das Programm beendet. Sie wird ueber das UI aufgerufen.
	 * 
	 */
	public void close() {
		System.exit(0);
	}

	/**
	 * Visualisiert das Netzwerk aus der XML-Datei.
	 * Wird aufgerufen, nachdem über den FileChooser eine Datei ausgewählt wurde.
	 * 
	 */
	public void showNetwork() {
		
		List<Vertex> vertices = network.getVertices();
		
		for (Vertex v : vertices) {
			gc.strokeOval(v.getX(), v.getY(), v.getWidth(), v.getHeight());
			gc.strokeText(v.getName(), v.getX() - 10, v.getY() + 25);
		}
		
		List<Edge> edges = network.getEdges();
		
		for (Edge e : edges) {
			Vertex origin = e.getOrigin();
			Vertex destination = e.getDestination();
		
			int widthHalf = origin.getWidth() / 2;
			int heightHalf = origin.getHeight() / 2;
			drawArrow(gc, origin.getX() + widthHalf, origin.getY() + heightHalf, 
						destination.getX() + widthHalf, destination.getY() + heightHalf);
		}
	}

	/**
	 * Hilfsfunktion zum Zeichnen der Pfeile. 
	 * 
	 * @param gc - graphical Context
	 * @param x1 - X-Position des Senders
	 * @param y1 - Y-Position des Senders
	 * @param x2 - X-Position des Empfängers
	 * @param y2 - y-Position des Empfängers
	 */
	private void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
	    gc.setFill(Color.BLACK);

	    double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    ////////////////////////// -5 hier fuer die halbe breite des knotens ////////////////////////////////
	    int len = (int) Math.sqrt(dx * dx + dy * dy) - 5;
	    int ARR_SIZE = 5;

	    Transform transform2 = gc.getTransform();
	    
	    Transform transform = Transform.translate(x1, y1);
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    Paint old = gc.getStroke();
	    gc.setStroke(Color.BLACK);	    
	    gc.strokeLine(0, 0, len, 0);
	    //////////////////////////////////////////////////////////////////////////////////////////////////
	    if (Math.PI/2 > angle  && angle >= -Math.PI/2) {
	    	gc.strokeText("0/0", (len/2) - 2, 15);
	    }
	    //////////////////////////////////////////////////////////////////////////////////////////////////
	    gc.setStroke(old);
	    
	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
	            4);
	    
	    // Transofrmation rückgängig machen:
	    gc.setTransform(new Affine(transform2));
	}
	
}
