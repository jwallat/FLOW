package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class FlowSceneController implements Initializable {

	@FXML
	private AnchorPane anchor;
	
	@FXML
	private Canvas canvas;
	
	@FXML
	private GraphicsContext gc;
	
	@FXML
	private ComboBox<String> computationTypeChooser;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		gc = canvas.getGraphicsContext2D();
		
		//init ComboBox
		computationTypeChooser.getItems().addAll("Kanalsystem", "Social Network Analysis");
		computationTypeChooser.setOnMousePressed(new EventHandler<MouseEvent>(){
		    @Override
		    public void handle(MouseEvent event) {
		    	computationTypeChooser.requestFocus();
		    }
		});
		computationTypeChooser.setValue("Kanalsystem");
	}

	public void showNetwork() {
		// Male das Netzwerk und so
	}
}
