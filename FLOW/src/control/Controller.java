package control;

import java.io.File;

import model.Network;
import view.FlowSceneController;

public class Controller {

	private Network network;
	private Network residualNetwork;
	private Parser parser;
	private FlowSceneController flowSceneController;
	
	public Controller(File file, FlowSceneController flowSceneController) {
		
		this.flowSceneController = flowSceneController;
		this.parser = new Parser(file);
		this.parser.parse();
		this.network = parser.getData();
		
		flowSceneController.showNetwork();
	}
}
