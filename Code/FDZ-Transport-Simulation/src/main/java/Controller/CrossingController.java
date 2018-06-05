package Controller;

import Persistance.StationData;
import View.AbstractStation;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class CrossingController extends AbstractStation {

	private Pane parent;
	private ArrayList<AbstractStation> stations;

	@FXML
	private Pane rootPane;
	@FXML
	private Pane crossingOptions;
	@FXML
	private Text nameText;
	@FXML
	private TextField crossingNameTextField;
	@FXML
	private Pane previousStationsPane;

	public CrossingController(StationData data, Pane parent, ArrayList<AbstractStation> stations){
		this.data = data;
		this.parent = parent;
		this.stations = stations;
		stations.add(this);
	}


	@FXML
	public void initialize(){
		viewPane = rootPane;
		parent.getChildren().add(viewPane);
		crossingNameTextField.setOnKeyReleased(event -> {
			setName(crossingNameTextField.getText());
			data.setName(crossingNameTextField.getText());
		});
		refreshBelts(parent, stations);

		setData(data);
		setName(data.getName());

		//make Dragable
		viewPane.setOnMousePressed(e ->{
			sceneX = e.getSceneX();
			sceneY = e.getSceneY();

			dragXTrans = viewPane.getTranslateX();
			dragYTrans = viewPane.getTranslateY();
		});
		viewPane.setOnMouseDragged(e->{
			setXCord(e.getSceneX()  - sceneX + dragXTrans);
			setYCord(e.getSceneY() - sceneY + dragYTrans);
		});

		crossingNameTextField.setOnKeyReleased(event -> {
			setName(crossingNameTextField.getText());
			data.setName(crossingNameTextField.getText());
		});
	}

	public void setName(String name){
		nameText.setText(name);
	}

	@FXML
	private void openCloseOptions(){
		if(crossingOptions.isVisible())crossingOptions.setVisible(false);
		else {
			crossingOptions.setVisible(true);
			crossingNameTextField.setText(nameText.getText());
			getPreviousStationsPane().getChildren().clear();
			for (AbstractStation i: stations){
				if(i.equals(this))continue;
				CheckBox box = new CheckBox(i.getName());
				getPreviousStationsPane().getChildren().add(box);
				if(data.getPreviousStationsByName().contains(i.getData().getName()))box.setSelected(true);
				else box.setSelected(false);
				box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
					if(newValue) data.getPreviousStationsByName().add(i.getData().getName());
					else data.getPreviousStationsByName().remove(i.getData().getName());
					refreshBelts(parent, stations);

				});

			}
		}
	}
	@FXML
	public void deleteCrossing(){
		parent.getChildren().remove(viewPane);
		stations.remove(this);
		parent.getChildren().removeAll(incomingBelts);
	}

	@FXML
	private void closeOptions(){
		crossingOptions.setVisible(false);
	}

	public Pane getPreviousStationsPane(){
		return previousStationsPane;
	}

	public Pane getOptionsPane(){
		return crossingOptions;
	}



}
