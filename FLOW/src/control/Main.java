package control;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("ComControl");

		initRootLayout();

		showFlowScene();
	}
	
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getClassLoader().getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showFlowScene() {
		try {
			FXMLLoader flowSceneLoader = new FXMLLoader();
			flowSceneLoader
					.setLocation(Main.class.getClassLoader().getResource("view/FlowScene.fxml"));
			AnchorPane flowScene = (AnchorPane) flowSceneLoader.load();

			rootLayout.setCenter(flowScene);
			
			@SuppressWarnings("unused")
			Controller controller = new Controller(null, flowSceneLoader.getController());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
