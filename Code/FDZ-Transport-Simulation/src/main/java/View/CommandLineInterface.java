package View;

import Model.Command.ShutdownObserver;
import Model.Facade;
import Model.Network.ConnectionObserver;
import Model.Network.NetworkController;
import Model.Station.PrevPair;
import Model.Station.Station;
import Model.Station.StationHandler;
import Model.Station.StationObserver;

import java.util.*;

public class CommandLineInterface extends Thread implements StationObserver, ConnectionObserver, ShutdownObserver {

    private final Scanner sc;
    private final Facade facade;
    private final String HELP =
                    "+–––––––––––––––––––––––––––––––––+\n" +
                    "| Type in TestCommand:            |\n" +
                    "| -\"q\" to end program             |\n" +
                    "| -\"h\" for help                   |\n" +
                    "| -\"p\" to print current State     |\n" +
                    "| -\"FDZ-Command\" to test System   |\n" +
                    "+–––––––––––––––––––––––––––––––––+";


    public CommandLineInterface() {
        this.sc = new Scanner(System.in);
        System.out.println(HELP);
        facade = new Facade();
    }

    @Override
    public void update(Station station)
    {
        printState();
    }
    @Override
    public void update(){ printStatus(); }
    @Override
    public void shutdown(){ this.quit(); }

    @Override
    public void run(){
        while (true) {

            System.out.print(">");

            String input = sc.nextLine();
            if(input.length()>1) {
                if (input.contains("STStK004")) {
                    if (quit()) break;
                }
                if(NetworkController.getInstance().isConnected()){
                    testCommand(input);
                }else{
                    System.out.println("Need to connect first!");
                }
            }else if(input.length()==1) {
                switch (input.charAt(0)) {
                    case 'q': break;
                    case 'h': System.out.println(HELP); continue;
                    case 's': printStatus(); continue;
                    case 'p': printState(); continue;
                    case 'o': addObserver(); continue;
                }
                if(input.charAt(0)=='q'){break;}
            }
        }
        System.out.println("Goodbye");
    }

    private void addObserver(){
        StationHandler.getInstance().getStationList().forEach(station ->
            facade.addToStationObservable(station.getName(), this)
        );
        facade.addToConnectionObservable(this);
        facade.addToShutdownObservable(this);
    }

    private void printStatus(){
        System.out.println("Connected: "+NetworkController.getInstance().isConnected());
    }

    private void printState(){
        List<Station> stationList = StationHandler.getInstance().getStationList();//TODO falscher Zugriff
        for (Station aStationList : stationList) {
            /*foreach station*/
            System.out.print(
                    "\t|" + aStationList.getName() + "\n" +
                            "\t|––––––––––––––––––––––––\n" +
                            "\t|->shortCut  |" + aStationList.getShortCut() + "\n" +
                            "\t|->   id     |");
            try {
                System.out.print(facade.getSledsInStation(aStationList.getName()).get(0) + "\n");
            } catch (Exception e) {
                System.out.print("-2\n");
            }
            System.out.println("\t|->congested |" + aStationList.isCongested());
            if (aStationList.isCongested()) {
                aStationList.getSledsInStation().forEach(id -> System.out.print("\t\twith " + id + "\n"));
            }
            System.out.println("\t|->hopsBack  |" + aStationList.getHopsToNewCarriage());
            ArrayList<PrevPair> prev = aStationList.getPrevStations();
            System.out.println(
                    "\t" + aStationList.getPrevStations().size() + " prevs");
            for (int j = 0; j < prev.size(); j++) {
                System.out.print(
                        "\t| prev" + j + " = " + prev.get(j).getPrevStation().getName() + " " + prev.get(j).getPathTime() + "\n");
            }
            System.out.println("\n----END-----\n");
        }
    }

    private void testCommand(String input){
        facade.testCommand(input);
    }

    private boolean quit(){
        String QUIT = "+––––––––––––––––––––––––––+\n" +
                "|Detected Shutdown, do you |\n" +
                "|want to quit as well?[y|*]|\n" +
                "+––––––––––––––––––––––––––+\n#";
        System.out.print(QUIT);
        if(sc.next().compareToIgnoreCase("y") == 0) {
            return true;
        }
        if(sc.hasNext()) sc.nextLine();
        return false;
    }

}
