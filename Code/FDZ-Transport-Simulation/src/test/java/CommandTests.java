import Model.CommandListener;
import Model.*;
import org.junit.Assert;
import org.junit.Test;
import com.google.common.collect.Lists;

import java.util.ArrayList;

public class CommandTests {



	@Test
	public void requestEmptyCarriageTest(){
		ArrayList<Station> standardStations = newStandardStations();
				CommandListener listener = new CommandListener(standardStations);
		listener.testCommand("STStK00100010002ro");
		Assert.assertEquals(-1,standardStations.get(0).getSledInside()); //TODO: A: eigentlich sollte das klappen oder habe ich in der Funktionsweise was falsch verstanden?
	}

	private ArrayList<Station> newStandardStations() {
			ArrayList<Station> standardStations = Lists.newArrayList(
			new Station("Robot", "ro"),
			new Station("Lager", "la"),
			new Station("EinAusgabe", "ea"));
			return standardStations;
	}
}
