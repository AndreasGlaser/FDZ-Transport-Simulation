package Controller;

import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * The Controller for Crossings
 * @author Andreas Glaser
 *
 *
 */
public class CrossingController extends AbstractStation {

	private final Pane parent;
	private final ArrayList<AbstractStation> stations;

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
	@FXML
	private Button crossingOptionsButton;

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

	@Override
	public void initAfterAllStationLoaded() {

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
			for (AbstractStation station: stations){
				if(station.equals(this))continue;
				BorderPane prevStationBorderPane = new BorderPane();
				getPreviousStationsPane().getChildren().add(prevStationBorderPane);

				CheckBox box = new CheckBox(station.getName());
				prevStationBorderPane.setLeft(box);
				if(prevStationsContains(station.getData().getName()))box.setSelected(true);
				else box.setSelected(false);


				HBox timeBox = new HBox();
				prevStationBorderPane.setRight(timeBox);
				Text prevStationTimeText = new Text("s: ");
				timeBox.getChildren().add(prevStationTimeText);
				Integer time = 1;
				for(Pair<String, Integer> pair: data.getPreviousStationsByName()){
					if(pair.getKey().equals(station.getName())) time=pair.getValue();
				}
				TextField prevStationTimeTextField = new TextField(time.toString());
				timeBox.getChildren().add(prevStationTimeTextField);
				prevStationTimeTextField.setMaxWidth(50);
				prevStationTimeTextField.setTooltip(new Tooltip("Time in s to this crossing"));
				prevStationTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
					try {
						prevStationTimeTextField.getStyleClass().remove("red");
						prevStationTimeTextField.getStyleClass().add("green");
						for(Pair<String, Integer> pair: data.getPreviousStationsByName()){
							if(pair.getKey().equals(station.getName())){
								addPrevStation(new Pair<>(pair.getKey(), Integer.parseInt(newValue)));
								lookForStationsThatNeedToUpdate(this);
								break;
							}
						}
					}catch (NumberFormatException e){
						prevStationTimeTextField.getStyleClass().remove("green");
						prevStationTimeTextField.getStyleClass().add("red");

					}



				});
				box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
					if(newValue){
						Integer pathTime = Integer.parseInt(prevStationTimeTextField.getText());
						addPrevStation(new Pair<>(station.getData().getName(),pathTime));
						lookForStationsThatNeedToUpdate(this);
					}
					else {
						prevStationRemove(station.getData().getName());
						lookForStationsThatNeedToUpdate(this);
					}
					refreshBelts(parent, stations);

				});

			}
		}
	}

	/**
	 * looks for all stations that have this station as prevStation and Updates them
	 * @param triggerStation to prevent infinite loops, it is checked if a prevStation is the one that triggered the Update
	 */
	private void lookForStationsThatNeedToUpdate(AbstractStation triggerStation) {
		stations.stream().filter(station -> !station.equals(triggerStation)).forEach(station ->{
			station.getPreviousStationsByName().stream().filter(pair -> pair.getKey().equals(getName())).forEach(pair ->{
				if(station.getData().getstationType().equals(StationType.STATION)){
					((StationController)station).updatePrevStations(this, pair.getValue());
				}else {
					((CrossingController)station).lookForStationsThatNeedToUpdate(triggerStation);
				}
			});
		});
	}

	@FXML
	public void deleteCrossing(){
		parent.getChildren().remove(viewPane);
		stations.remove(this);
		parent.getChildren().removeAll(incomingBelts);
		for(AbstractStation station: stations){
			station.refreshBelts(parent, stations);
		}
	}

	@FXML
	public void closeOptions(){
		crossingOptions.setVisible(false);
	}

	@Override
	public void setDisableOptionsButton(Boolean bool) {
		crossingOptionsButton.setDisable(bool);
	}

	private Pane getPreviousStationsPane(){
		return previousStationsPane;
	}




}
