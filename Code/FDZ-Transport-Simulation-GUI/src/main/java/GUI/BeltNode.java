package GUI;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

public class BeltNode extends Pane {

	public BeltNode(StationPane fromStation, StationPane toStation){

		drawBelt(new Point2D(fromStation.getXCord()+40, fromStation.getYCord()+50), new Point2D(toStation.getXCord()+60, toStation.getYCord()+70));
		fromStation.translateXProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+40, fromStation.getYCord()+50), new Point2D(toStation.getXCord()+60, toStation.getYCord()+70));
		});
		fromStation.translateYProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+40, fromStation.getYCord()+50), new Point2D(toStation.getXCord()+60, toStation.getYCord()+70));
		});


		toStation.translateXProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+40, fromStation.getYCord()+50), new Point2D(toStation.getXCord()+60, toStation.getYCord()+70));
		});
		toStation.translateYProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+40, fromStation.getYCord()+50), new Point2D(toStation.getXCord()+60, toStation.getYCord()+70));
		});

	}

	/**
	 * draws a shape representing an conveyer belt between to points
	 * @param fromPoint : the starting Point
	 * @param toPoint : the end Point
	 */
	private void drawBelt(Point2D fromPoint, Point2D toPoint) {
		Shape belt;
		Color beltColor = Color.GRAY;
		Double distance = fromPoint.distance(toPoint);
		Point2D xAxis = new Point2D(1,0);


		Line upperLine = new Line();
		upperLine.setStroke(beltColor);
		upperLine.setStartX(fromPoint.getX());
		upperLine.setStartY(fromPoint.getY() - 10);

		upperLine.setEndX(fromPoint.getX()+ distance);
		upperLine.setEndY(fromPoint.getY() - 10);

		Line lowerLine = new Line();
		lowerLine.setStroke(beltColor);
		lowerLine.setStartX(fromPoint.getX());
		lowerLine.setStartY(fromPoint.getY() + 10);

		lowerLine.setEndX(fromPoint.getX()+ distance);
		lowerLine.setEndY(fromPoint.getY() + 10);
		belt = Shape.union(lowerLine, upperLine);

		Double memberX = fromPoint.getX() + 10;
		Double memberY = fromPoint.getY();
		for(int i = 1; i< distance/15 ; i++){
			Polygon triangle = new Polygon();
			triangle.getPoints().addAll(
					memberX+(15 * (i-2))+5, memberY-10,
					memberX + (15 * (i-1))+5, memberY,
					memberX + (15 * (i-2))+5, memberY+10);
			belt = Shape.union(belt, triangle);
		}
		belt.setFill(beltColor);
		getChildren().add(belt);

		if(fromPoint.getY()<toPoint.getY()){
			belt.getTransforms().add(
					new Rotate(xAxis.angle(toPoint.subtract(fromPoint)), //Point2D is calculated as vector for angle calculation, see Javafx docs
							fromPoint.getX(), fromPoint.getY()));
		}else{
			belt.getTransforms().add(
					new Rotate(-xAxis.angle(toPoint.subtract(fromPoint)),
							fromPoint.getX(), fromPoint.getY()));
		}


	}
}
