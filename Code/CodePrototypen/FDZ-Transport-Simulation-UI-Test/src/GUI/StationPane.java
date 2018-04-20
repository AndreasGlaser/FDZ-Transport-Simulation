package GUI;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class StationPane extends VBox{

	String name;
	Text stationNameText = new Text(name);
	private Double xCord = 0.;
	private Double yCord = 0.;
	private ArrayList<StationPane> reachableStations = new ArrayList<>();
	private ArrayList<BeltNode> outgoingBelts = new ArrayList<>();

	public StationPane(String name, Pane parent, ArrayList<StationPane> stations){

		setStyle("-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-color: aqua; -fx-min-width: 100; -fx-min-height: 120");

		setName(name);
		getChildren().add(stationNameText);

		ChoiceBox congestionBox = new ChoiceBox();
		getChildren().add(congestionBox);

		Text sledText = new Text("empty");
		getChildren().add(sledText);

		Button options = new Button("Optionen");
		getChildren().add(options);
		options.setOnAction(e->{
			VBox optionsPane = new VBox();
			parent.getChildren().add(optionsPane);
			optionsPane.setStyle("-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-color: cadetblue; -fx-min-width: 80; -fx-min-height: 100");
			optionsPane.translateXProperty().bind(translateXProperty());
			optionsPane.translateYProperty().bind(translateYProperty());


			Button close = new Button("x");
			optionsPane.getChildren().add(close);
			close.setOnAction(e2->{
				parent.getChildren().remove(optionsPane);
			});

			TextField stationNameTextField = new TextField(this.getName());
			optionsPane.getChildren().add(stationNameTextField);
			stationNameTextField.setOnKeyReleased(event -> {
				this.setName(stationNameTextField.getText());
			});

			Text info = new Text("erreichbare Stationen:");
			optionsPane.getChildren().add(info);

			for (StationPane i: stations){
				CheckBox box = new CheckBox(i.getName());
				optionsPane.getChildren().add(box);
				box.selectedProperty().addListener((observable, oldValue, newValue) -> {
					if(newValue)reachableStations.add(stations.get(0));//TODO: Arraylist in map umwandeln und richtige station über namen als schlüssel heraussuchen
					else reachableStations.remove(stations.get(0));//TODO: das selbe wie eine zeile drüber
					refreshBelts(parent);
					
				});

			}



		});


	}

	private void refreshBelts(Pane parent) {
		for(BeltNode i: outgoingBelts){ //alle Belts löschen damit sie nicht doppelt vorhanden sind
			parent.getChildren().remove(i);
		}
		for(StationPane i: reachableStations){
			BeltNode belt = new BeltNode();
			belt.setStrokeWidth(3);
			outgoingBelts.add(belt);

			System.out.println("layoutProperty"+i.translateXProperty());
			belt.startXProperty().bind(Bindings.add(50, this.translateXProperty()));
			belt.startYProperty().bind(Bindings.add(60, this.translateYProperty()));
			belt.endXProperty().bind(Bindings.add(50, i.translateXProperty()));
			belt.endYProperty().bind(Bindings.add(60, i.translateYProperty()));
			System.out.println("Stroke endX:"+i.getXCord());
			parent.getChildren().add(belt);
			belt.toBack();
		}


	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
		stationNameText.setText(this.name);
	}

	public void setXCord(Double newX){
		xCord = newX;
	}
	public void setYCord(Double newY){
		yCord = newY;
	}
	public Double getXCord(){
		return xCord;
	}
	public Double getYCord(){
		return yCord;
	}
}
