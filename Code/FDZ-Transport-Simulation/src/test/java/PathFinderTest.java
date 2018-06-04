import Model.Command.PathFinder;
import Model.CongestionException;
import Model.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station;
import Model.StationHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.Assert.fail;

/**@author nlehmann */

public class PathFinderTest {

    private ArrayList<Station> stations = StationHandler.getInstance().getStationList();
    private Station robot = new Station("robot", "ro");
    private Station stock = new Station("stock", "la");
    private Station inOut = new Station("InOut", "ea");

    @Before
    public void initiate(){
        stations.add(robot);
        stations.add(stock);
        stations.add(inOut);
        robot.addPrevStation(stock);
        stock.addPrevStation(inOut);
        stock.addPrevStation(robot);
        inOut.addPrevStation(robot);
        robot.setHopsToNewCarriage(2);
        stock.setHopsToNewCarriage(1);
        inOut.setHopsToNewCarriage(1);
    }

    @Test
    public void findPathToRobotBlocked(){
        NetworkController.getInstance().testCommand("STStK0011527162650:000002la");
        try{
            new PathFinder(inOut,robot);
        }catch (CongestionException e){
            Assert.assertEquals(e.getBlockingStation(), stock);
        }
    }
    @Test
    public void newCarriageToRobotBlocked(){
        NetworkController.getInstance().testCommand("STStK0011527162650:000002la");
        try{
            new PathFinder(robot,2);
        }catch (CongestionException e){
            Assert.assertEquals(e.getBlockingStation(), stock);
        }catch (IllegalSetupException e){
            System.err.println("Wrong TestSetup");
        }
    }
    @Test
    public void twoExtraStationTest(){
        twoExtraStations();
        try {
            new PathFinder(robot,robot.getHopsToNewCarriage());
        }catch(CongestionException e){
            fail("Should not be Congested");
        }catch (IllegalSetupException e){
            System.err.println("Wrong TestSetup");
        }
        printPath(inOut, robot);
    }

    private void twoExtraStations(){
        Station station1 = new Station("one", "1");
        Station station2 = new Station("two", "2");
        station1.setHopsToNewCarriage(2);
        station2.setHopsToNewCarriage(1);
        station2.addPrevStation(robot);
        station1.addPrevStation(station2);
        station1.addPrevStation(inOut);
        stock.getPrevStations().remove(0);
        stock.addPrevStation(station1);
        stock.addPrevStation(inOut);

        station1.driveInSled(12);
    }


    private void printPath(Station from,Station to){
        try{
            LinkedList<Station> path = new PathFinder(from,to).getPath();
            for (int i = 0; i < path.size(); i++) {
                Station s =  path.get(i);
                System.out.println("\t\t#"+i+". "+s.getName());
            }
        }catch (Exception e){
            fail();
        }
    }

}
