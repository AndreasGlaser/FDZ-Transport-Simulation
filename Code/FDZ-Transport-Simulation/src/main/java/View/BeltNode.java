package View;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;

class BeltNode extends Group {

	public BeltNode(AbstractStation fromStation, AbstractStation toStation){
		Double fromXOffset = 120.;
		Double fromYOffset = 70.;
		Double toXOffset = 120.;
		Double toYOffset = 70.;

		drawBelt(new Point2D(fromStation.getXCord()+fromXOffset, fromStation.getYCord()+fromYOffset), new Point2D(toStation.getXCord()+toXOffset, toStation.getYCord()+toYOffset));
		fromStation.getTranslateXProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+fromXOffset, fromStation.getYCord()+fromYOffset), new Point2D(toStation.getXCord()+toXOffset, toStation.getYCord()+toYOffset));
		});
		fromStation.getTranslateYProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+fromXOffset, fromStation.getYCord()+fromYOffset), new Point2D(toStation.getXCord()+toXOffset, toStation.getYCord()+toYOffset));
		});


		toStation.getTranslateXProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+fromXOffset, fromStation.getYCord()+fromYOffset), new Point2D(toStation.getXCord()+toXOffset, toStation.getYCord()+toYOffset));
		});
		toStation.getTranslateYProperty().addListener((event, oldX, newX)->{
			getChildren().retainAll();
			drawBelt(new Point2D(fromStation.getXCord()+fromXOffset, fromStation.getYCord()+fromYOffset), new Point2D(toStation.getXCord()+toXOffset, toStation.getYCord()+toYOffset));
		});

	}

	/**
	 * draws a shape representing an conveyor belt between to points
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


		if(fromPoint.getY()<toPoint.getY()){
			belt.getTransforms().add(
					new Rotate(xAxis.angle(toPoint.subtract(fromPoint)), //Point2D is calculated as vector for angle calculation, see Javafx docs
							fromPoint.getX(), fromPoint.getY()));
		}else{
			belt.getTransforms().add(
					new Rotate(-xAxis.angle(toPoint.subtract(fromPoint)),
							fromPoint.getX(), fromPoint.getY()));
		}
		getChildren().add(belt);
	}
}
