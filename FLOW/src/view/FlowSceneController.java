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
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		gc = canvas.getGraphicsContext2D();
	}
	
	public void init(Stage stage) {
		this.stage = stage;
	}
	
	public void openFile() {
		System.out.println("OpenFile");
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

	public void showNetwork() {
		// Male das Netzwerk und so
	}

	
}
