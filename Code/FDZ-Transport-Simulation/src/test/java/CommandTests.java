import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Network.NetworkController;
import Model.Station.Station;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.fail;

public class CommandTests {

/*

	@Test
	public void requestEmptyCarriageTest(){
		ArrayList<Station> standardStations = newStandardStations();
		NetworkController listener = NetworkController.getInstance();
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,(int)standardStations.get(0).getSledsInStation().get(0));
	}

	@Test
	public void repositionCarriageTest(){
		//leeren Schlitten zum Roboter
		new Facade().setFastTime(true);
		addStandadStation();
		NetworkController listener =NetworkController.getInstance();
		try{
			listener.testCommand("STStK0010000000001:000002ro");

		}catch (NullPointerException e){

		}
		try {
			sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertEquals(-1,new Facade().getSledsInStation("Robot").get(0).intValue());

		//Schlitten zu Lager schicken
		listener.testCommand("STStK0030000000002:00000401la");
		Assert.assertEquals(1,new Facade().getSledsInStation("Lager").get(0).intValue());
	}

	@Test
	public void releaseCarriageTest(){
		//leeren Schlitten zum Roboter
		ArrayList<Station> standardStations = newStandardStations();
		NetworkController listener = NetworkController.getInstance();
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,(int)standardStations.get(0).getSledsInStation().get(0));

		//Schlitten zu Lager schicken
		listener.testCommand("STStK0030002000401la");
		Assert.assertEquals(1, (int)standardStations.get(0).getSledsInStation().get(0));

		//Schlitten freigeben
		listener.testCommand("STStK0020003000201");
		Assert.assertEquals(-2, (int)standardStations.get(0).getSledsInStation().get(0));
	}*/

	@Test
	public void shutdownTest(){
		ArrayList<Station> standardStations = newStandardStations();
		NetworkController listener = NetworkController.getInstance();
		listener.testCommand("STStK00400010000");
		//TODO: erkennen ob das System heruntergefahren wurde
	}

	private void addStandadStation(){
		ArrayList<Station> standardStations = newStandardStations();
		for(Station station: standardStations){
			try {
				new Facade().addStation(station.getName(), station.getShortCut());
			} catch (IllegalSetupException e) {
				e.printStackTrace();
			}
		}
	}
	private ArrayList<Station> newStandardStations() {
		try {
			ArrayList<Station> standardStations = new ArrayList();
			standardStations.add(new Station("Robot", "ro"));
			standardStations.add(new Station("Lager", "la"));
			standardStations.add(new Station("EinAusgabe", "ea"));
			return standardStations;
		}catch(IllegalSetupException e){
			fail();
		}
		return null;
	}
}
