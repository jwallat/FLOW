package util;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import view.FlowSceneController;

/**
 * Helfer-Klasse um einen Switch-Button zu erstellen. Dieser wird verwendet um
 * zwischen FLOW-Notation und "normaler" Notation hin und her zu schalten.
 *
 * @author jwall
 *
 */
public class SwitchButton extends Label {
	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

	public SwitchButton(FlowSceneController fsc, String mode) {
		Button switchBtn = new Button();
		switchBtn.setPrefWidth(40);
		switchBtn.setPrefHeight(10);
		if (mode.equals("flow")) {
			switchBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					switchedOn.set(!switchedOn.get());
					fsc.toggleFLOWNotation();
				}
			});
		} else if (mode.equals("paths")) {
			switchBtn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					switchedOn.set(!switchedOn.get());
					fsc.pathsToggleButtonClicked();
				}
			});
		} else if (mode.equals("centrality")) {
			switchBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					switchedOn.set(!switchedOn.get());
					fsc.centralityToggleButtonClicked();
				}
			});
		} else if (mode.equals("weights"))

		{
			switchBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					switchedOn.set(!switchedOn.get());
					fsc.weightsToggleButtonClicked();
				}
			});
		}

		setGraphic(switchBtn);

		switchedOn.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (t1) {
					setText("ON");
					setStyle("-fx-background-color: green;-fx-text-fill:white;");
					setContentDisplay(ContentDisplay.RIGHT);
				} else {
					setText("OFF");
					setStyle("-fx-background-color: grey;-fx-text-fill:black;");
					setContentDisplay(ContentDisplay.LEFT);
				}
			}
		});

		switchedOn.set(false);
	}

	public SimpleBooleanProperty switchOnProperty() {
		return switchedOn;
	}
}
