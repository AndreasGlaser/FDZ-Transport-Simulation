package GUI;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import persistance.StationData;

import java.util.ArrayList;

public class StationPane extends VBox{

	/*private String name = "";
	private String shortcut = "";
	private Double xCord = 0.;
	private Double yCord = 0.;
	private ArrayList<String> reachableStationsByName = new ArrayList<>();*/
	private StationData data = new StationData("");

	//helper Variables
	private Text stationNameText = new Text(data.getName());
	private ArrayList<BeltNode> outgoingBelts = new ArrayList<>();
	private Double dragXTrans = .0;
	private Double dragYTrans = .0;
	private Double sceneX = .0;
	private Double sceneY = .0;

	public StationPane(StationData data, Pane parent, ArrayList<StationPane> stations){
		setData(data);
		parent.getChildren().add(this);
		stations.add(this);
		refreshBelts(parent, stations);
		setStyle("-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-color: aqua; -fx-min-width: 100; -fx-min-height: 120");

		setName(data.getName());
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
				if(i.equals(this))continue;
				CheckBox box = new CheckBox(i.getName());
				optionsPane.getChildren().add(box);
				if(data.getReachableStationsByName().contains(i.getData().getName()))box.setSelected(true);
				else box.setSelected(false);
				box.selectedProperty().addListener((observable, oldValue, newValue) -> {
					if(newValue) data.getReachableStationsByName().add(i.getData().getName());
					else data.getReachableStationsByName().remove(i.getData().getName());
					refreshBelts(parent, stations);
					
				});

			}



		});

		setOnMousePressed(e ->{
			sceneX = e.getSceneX();
			sceneY = e.getSceneY();

			dragXTrans = getTranslateX();
			dragYTrans = getTranslateY();
		});
		setOnMouseDragged(e->{
			/*System.out.println("xcord: "+getXCord());
			System.out.println("sceneX: "+ e.getSceneX());
			System.out.println("scenex - xcord: "+ (e.getSceneX() - getXCord())+ dragXTrans);
			System.out.println("translateX: "+ getTranslateX());*/
			setXCord(e.getSceneX()  - sceneX + dragXTrans);
			setYCord(e.getSceneY() - sceneY + dragYTrans);
		});



	}

	/**
	 * removes all Belts and adds Belts to fit the current configuration
	 * @param parent the Pane the Belts will be displayed in
	 * @param stations the List of stations available in the system
	 */
	public void refreshBelts(Pane parent, ArrayList<StationPane> stations) {
		parent.getChildren().removeAll(outgoingBelts);
		for(StationPane i: stations){
			if(data.getReachableStationsByName().contains(i.getName())){
				BeltNode belt = new BeltNode(this, i);
				outgoingBelts.add(belt);
				parent.getChildren().add(belt);
				belt.toBack();
			}

		}


	}

	public String getName(){
		return data.getName();
	}
	public void setName(String name){
		data.setName(name);
		stationNameText.setText(data.getName());
	}
	public String getShortcut(){return data.getShortcut();}
	public void setShortcut(String shortcut){data.setShortcut(shortcut);}
	public void setXCord(Double newX){
		data.setXCord(newX);
		setTranslateX(newX);
	}
	public void setYCord(Double newY){
		data.setYCord(newY);
		setTranslateY(newY);
	}
	public Double getXCord(){
		return data.getXCord();
	}
	public Double getYCord(){
		return data.getYCord();
	}
	public ArrayList<String> getReachableStationsByName(){return data.getReachableStationsByName();}
	public StationData getData(){return data;}
	public void setData(StationData data){
		this.data = data;
		setTranslateX(data.getXCord());
		setTranslateY(data.getYCord());
	}
}
