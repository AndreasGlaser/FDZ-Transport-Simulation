import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.fail;

public class CommandTests {



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
		ArrayList<Station> standardStations = newStandardStations();
		NetworkController listener =NetworkController.getInstance();
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,(int)standardStations.get(0).getSledsInStation().get(0));

		//Schlitten zu Lager schicken
		listener.testCommand("STStK0030002000401la");
		Assert.assertEquals(1,(int)standardStations.get(0).getSledsInStation().get(0));
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
	}

	@Test
	public void shutdownTest(){
		ArrayList<Station> standardStations = newStandardStations();
		NetworkController listener = NetworkController.getInstance();
		listener.testCommand("STStK00400010000");
		//TODO: erkennen ob das System heruntergefahren wurde
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
