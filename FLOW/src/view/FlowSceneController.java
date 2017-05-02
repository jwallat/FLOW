package view;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import control.Parser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import model.Network;

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
	}
	
	/**
	 * Funktion die das Programm beendet. Sie wird ueber das UI aufgerufen.
	 * 
	 */
	public void close() {
		System.exit(0);
	}

	public void showNetwork() {
		// Male das Netzwerk und so
	}

	
}
