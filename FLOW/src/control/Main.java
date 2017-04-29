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


public class Main extends Application {
	
	private Stage primaryStage;
	private Parent root;
	
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
		FlowSceneController fsc = (FlowSceneController) loader.getController();
		fsc.init(primaryStage);
		
		
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

		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
