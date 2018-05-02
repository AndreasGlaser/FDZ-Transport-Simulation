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
	}

	private ArrayList<Station> newStandardStations() {
			ArrayList<Station> standardStations = Lists.newArrayList(
			new Station("Robot", "ro"),
			new Station("Lager", "la"),
			new Station("EinAusgabe", "ea"));
			return standardStations;
	}
}
