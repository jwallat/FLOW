package helper;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

public class PannablePane extends Pane {
	
	DoubleProperty myScale = new SimpleDoubleProperty(1.0);

	//dran denken: Größe weiterhin ans anchor binden!
    public PannablePane() {
        //this.setPrefSize(600, 600);
        //setStyle("-fx-background-color: lightgrey; -fx-border-color: blue;");

        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    public double getScale() {
        return myScale.get();
    }

    public void setScale( double scale) {
        myScale.set(scale);
    }

    public void setPivot( double x, double y) {
        setTranslateX(getTranslateX()-x);
        setTranslateY(getTranslateY()-y);
    }
}
