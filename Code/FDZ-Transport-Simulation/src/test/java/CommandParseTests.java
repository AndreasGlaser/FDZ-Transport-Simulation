
import Model.Network.NetworkController;
import Model.Station.Station;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class CommandParseTests {

    @Test
    public void requestEmptyCarMissingTargetTest(){
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002");
        //TODO: ausgehende Netzwerknachrichten abfangen
    }

    @Test
    public void repositionCarMissingTargetTest(){
        //leeren Schlitten zum Roboter
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002ro");
        Assert.assertEquals(-1,standardStations.get(0).getSledInside());

        listener.testCommand("STStK0030002000401");
        //TODO: ausgehende Netzwerknachrichten abfangen
    }

    @Test
    public void repositionCarMissingIDTest(){
        //leeren Schlitten zum Roboter
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002ro");
        Assert.assertEquals(-1,standardStations.get(0).getSledInside());

        listener.testCommand("STStK00300020004la");
        //TODO: ausgehende Netzwerknachrichten abfangen
    }

    private ArrayList<Station> newStandardStations() {
        ArrayList<Station> standardStations = new ArrayList();
        standardStations.add(new Station("Robot", "ro"));
        standardStations.add(new Station("Lager", "la"));
        standardStations.add(new Station("EinAusgabe", "ea"));
        return standardStations;
    }
}
