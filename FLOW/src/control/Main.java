package control;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.FlowSceneController;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Main Klasse, die die Stage anzeigt und die Groesse des Fensters auf die 
 * Bildschirm-masse setzt.
 * 
 * @author Jonas Wallat
 *
 */
public class Main extends Application {
	
	private Stage primaryStage;
	private Parent root;
	
	/**
	 * Methode die aus der main(args) aufgerufen wird und die Stage und Scene setzt.
	 * 
	 */
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("BigFLOW");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FlowScene.fxml"));
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Scene scene = new Scene(root, 800, 800);
		scene.getStylesheets().add("control/application.css");
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Big FLOW");
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        //set Stage boundaries to visible bounds of the main screen
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        //primaryStage.setMaximized(true);
		primaryStage.show();
		
		FlowSceneController fsc = (FlowSceneController) loader.getController();
		fsc.init(primaryStage);
	}
	
	/**
	 * FX-Methode. Fuehrt die start-Methode aus.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
