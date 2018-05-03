import Model.CommandListener;
import Model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CommandTests {



	@Test
	public void requestEmptyCarriageTest(){
		ArrayList<Station> standardStations = newStandardStations();
		CommandListener listener = new CommandListener(standardStations);
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,standardStations.get(0).getSledInside());
	}

	@Test
	public void repositionCarriageTest(){
		//leeren Schlitten zum Roboter
		ArrayList<Station> standardStations = newStandardStations();
		CommandListener listener = new CommandListener(standardStations);
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,standardStations.get(0).getSledInside());

		//Schlitten zu Lager schicken
		listener.testCommand("STStK0030002000401la");
		Assert.assertEquals(1, standardStations.get(1).getSledInside());
	}

	@Test
	public void releaseCarriageTest(){
		//leeren Schlitten zum Roboter
		ArrayList<Station> standardStations = newStandardStations();
		CommandListener listener = new CommandListener(standardStations);
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,standardStations.get(0).getSledInside());

		//Schlitten zu Lager schicken
		listener.testCommand("STStK0030002000401la");
		Assert.assertEquals(1, standardStations.get(1).getSledInside());

		//Schlitten freigeben
		listener.testCommand("STStK0020003000201");
		Assert.assertEquals(-2, standardStations.get(1).getSledInside());
	}

	@Test
	public void shutdownTest(){
		ArrayList<Station> standardStations = newStandardStations();
		CommandListener listener = new CommandListener(standardStations);
		listener.testCommand("STStK00400010000");
		//TODO: erkennen ob das System heruntergefahren wurde
	}

	private ArrayList<Station> newStandardStations() {
		ArrayList<Station> standardStations = new ArrayList();
				standardStations.add(new Station("Robot", "ro"));
				standardStations.add(new Station("Lager", "la"));
				standardStations.add(new Station("EinAusgabe", "ea"));
		return standardStations;
	}
}
