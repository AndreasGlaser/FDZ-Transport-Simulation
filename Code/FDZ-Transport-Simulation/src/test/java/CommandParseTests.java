
import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.fail;

public class CommandParseTests {

    @Test
    public void requestEmptyCarMissingTargetTest(){
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002");
    }

    @Test (expected = NullPointerException.class)
    public void repositionCarMissingTargetTest(){
        //leeren Schlitten zum Roboter
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002ro");
        Assert.assertEquals(-1,(int)standardStations.get(0).getSledsInStation().get(0));

        listener.testCommand("STStK0030002000401");
    }

    @Test(expected = NullPointerException.class)
    public void repositionCarMissingIDTest(){
        //leeren Schlitten zum Roboter
        ArrayList<Station> standardStations = newStandardStations();
        NetworkController listener = NetworkController.getInstance();
        listener.testCommand("STStK00100010002ro");
        Assert.assertEquals(-1,(int)standardStations.get(0).getSledsInStation().get(0));

        listener.testCommand("STStK00300020004la");
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
