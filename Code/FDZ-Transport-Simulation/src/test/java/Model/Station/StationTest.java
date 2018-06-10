package Model.Station;

import Model.Exception.IllegalSetupException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;

/**
 * @author nlehmann
 *
 * Complete Tests for the Station Class
 */
public class StationTest {

    private Station station, prev1, prev2;
    private final String name="Station", shortCut="st";

    @Before
    public void initiate(){
        try{
            station = new Station(name,shortCut);
            prev1 = new Station("prev1", "p1");
            prev2 = new Station("prev2", "p2");
            StationHandler.getInstance().addStation(station);
            StationHandler.getInstance().addStation(prev1);
            StationHandler.getInstance().addStation(prev2);
            station.setHopsToNewCarriage(2);
        }catch (IllegalSetupException e){
            fail();
        }
    }

    @Test
    public void initiateIllegalSetup(){
        try{
            station = new Station(null, "st");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("name"));
        }
        try{
            station = new Station("", "st");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("name"));
        }
        try{
            station = new Station("Station", null);
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
        try{
            station = new Station("Station", "s");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
        try{
            station = new Station("Station", "stn");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
    }

    @Test
    public void assignValues(){
        try{
            station.setHopsToNewCarriage(2);
        }catch (IllegalSetupException e){
            fail();
        }
    }

    @Test
    public void assignedValues(){
        Assert.assertSame(station.getName(), name);
        Assert.assertSame(station.getShortCut(), shortCut);
        Assert.assertEquals(2, station.getHopsToNewCarriage());
    }

    @Test
    public void assignFalseValues(){
        try{
            station.setName(null);
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("name"));
        }
        try{
            station.setName("");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("name"));
        }
        try{
            station.setShortCut(null);
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
        try{
            station.setShortCut("s");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
        try{
            station.setShortCut("stn");
            fail();
        }catch(IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("shortcut"));
        }
        try {
            station.setHopsToNewCarriage(0);
            fail();
        }catch (IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("hops"));
        }
        try {
            station.setHopsToNewCarriage(-1);
            fail();
        }catch (IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("hops"));
        }
        try {
            station.setHopsToNewCarriage(StationHandler.getInstance().getAmountOfStations());
            fail();
        }catch (IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("hops"));
        }try {
            station.setHopsToNewCarriage(Integer.MIN_VALUE);
            fail();
        }catch (IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("hops"));
        }
        try {
            station.setHopsToNewCarriage(Integer.MAX_VALUE);
            fail();
        }catch (IllegalSetupException e){
            Assert.assertTrue(e.getMessage().toLowerCase().contains("hops"));
        }
    }

    @Test
    public void setPrevStations(){
        station.addPrevStation(prev1);
        Assert.assertEquals(prev1, station.getPrevStations().get(0));
        station.addPrevStation(prev2);
        Assert.assertEquals(prev2, station.getPrevStations().get(1));
    }

    @Test
    public void driveInPossible(){
        station.driveInSled(1);
        Assert.assertEquals(1, (int)station.getSledsInStation().get(0));
        try{
            station.getSledsInStation().get(1);
            fail();
        }catch (IndexOutOfBoundsException e){
            TestCase.assertTrue(true);
        }
    }

    @Test
    public void driveInNotPossible(){
        station.driveInSled(1);
        Thread th = new Thread(()->{
            station.driveInSled(2);
        });
        th.start();
        Assert.assertEquals(1, (int)station.getSledsInStation().get(0));
        wait(100);
        Assert.assertTrue(th.isAlive());
        Assert.assertTrue(station.isCongested());
        Assert.assertEquals(2, (int)station.getSledsInStation().get(1));
        station.driveOutSled();
        wait(100);
        Assert.assertEquals(1, station.getSledsInStation().size());
        Assert.assertEquals(2, (int)station.getSledsInStation().get(0));
    }

    @Test
    public void driveOutRequired(){
        station.driveInSled(1);
        Assert.assertEquals(1, (int)station.getSledsInStation().get(0));
        station.driveOutSled();
        Assert.assertEquals(null, station.getSledsInStation().get(0));
    }


    private void wait(int millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException e){
            System.err.println("interrupted Test sleep");
        }
    }
}
