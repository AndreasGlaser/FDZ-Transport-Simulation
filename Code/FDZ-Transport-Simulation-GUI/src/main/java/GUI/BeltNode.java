package GUI;

import javafx.beans.binding.Bindings;
import javafx.scene.shape.Line;

public class BeltNode extends Line{

	public BeltNode(StationPane fromStation, StationPane toStation){
		setStrokeWidth(3);
		startXProperty().bind(Bindings.add(50, fromStation.translateXProperty()));
		startYProperty().bind(Bindings.add(60, fromStation.translateYProperty()));

		endXProperty().bind(Bindings.add(50, toStation.translateXProperty()));
		endYProperty().bind(Bindings.add(60, toStation.translateYProperty()));
	}
}
