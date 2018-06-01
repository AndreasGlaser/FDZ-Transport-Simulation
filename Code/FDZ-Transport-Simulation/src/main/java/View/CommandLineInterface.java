package View;

import Model.Station;
import Model.StationHandler;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandLineInterface extends Thread{

    private Scanner sc;
    private StationHandler handler;
    private final String help =
                    "+–––––––––––––––––––––––––––––––––+\n" +
                    "| Type in TestCommand:            |\n" +
                    "| -\"q\" to end program             |\n" +
                    "| -\"h\" for help                   |\n" +
                    "| -\"p\" to print current State     |\n" +
                    "| -\"a\" to add Station             |\n" +
                    "| -\"d\" to delete Station          |\n" +
                    "| -\"FDZ-Command\" to test System   |\n" +
                    "+–––––––––––––––––––––––––––––––––+";

    public CommandLineInterface() {
        sc = new Scanner(System.in);
        handler = StationHandler.getInstance();
    }

    @Override
    public void run(){
        while (true) {
            /* TODO: Implement Navigation */
            System.out.print(">");

            String input = sc.nextLine();
            if (input.compareTo("q") == 0) {
                break;
            }
            if (input.compareTo("h") == 0) {
                System.out.println(help);
                continue;
            }
            if (input.compareTo("p") == 0) {
                printState(handler.getStationList());
                continue;
            }
            if (input.compareTo("d") == 0) {
                //delete Station
                delete();
            }
            if (input.compareTo("a") == 0){
                //add Station
                add();
            }
            if (input.contains("STStK004")){
                System.out.print(
                                "+––––––––––––––––––––––––––+\n" +
                                "|Detected Shutdown, do you |\n" +
                                "|want to quit as well?[y|*]|\n" +
                                "+––––––––––––––––––––––––––+\n#");
                if(sc.next().compareToIgnoreCase("y") == 0) {
                    break;
                }
                if(sc.hasNext()) sc.nextLine();
            }
            testCommand(input);
        }
        System.out.println("Goodbye");
    }

    private void printState(ArrayList<Station> stationList){
        for(int i=0; i<stationList.size(); i++){
            /*foreach station*/
            System.out.println(
                    "\t|"+stationList.get(i).getName()+"\n"+
                            "\t|––––––––––––––––––––––––\n" +
                            "\t|->shortCut  |" + stationList.get(i).getShortCut() + "\n"+
                            "\t|->   id     |" + stationList.get(i).getSledInside() + "\n"+
                            "\t|->congested |" + stationList.get(i).isCongested());
            ArrayList<Station> prev = stationList.get(i).getPrevStations();
            for (int j = 0; j < prev.size(); j++) {
                System.out.print(
                        "\t| prev"+j+" = "+ prev.get(j).getName() +"\n");
            }
            System.out.println();
        }
    }

    private void add(){
        StationHandler handler = StationHandler.getInstance();

        System.out.println("adding a Station...");
        System.out.print("Type in name:");
        String name = sc.nextLine();
        System.out.print("Type in ShortCut:");
        String shortCut = sc.nextLine();
        handler.addStation(name,shortCut);

        System.out.print("Hops back to new Carriage:");
        handler.getStationByShortCut(shortCut).setHopsToNewCarriage(sc.nextInt());
        System.out.print("How many prev Stations?");
        int prevs=sc.nextInt();
        for(int i=0; i<prevs; ++i){
            addPrev(handler.getStationByShortCut(shortCut));
        }
    }

    private void delete(){
        System.out.print("Name of Station to delete:");
        try{
            StationHandler.getInstance().deleteStation(sc.nextLine());
        }catch(NullPointerException e){
            System.out.println("ERROR: No such Station known!");
        }

    }

    private void addPrev(Station station){
        Scanner s = new Scanner(System.in);
        System.out.print("name of prevStation:");
        String prev = s.nextLine();
        System.out.println(prev);
        station.addPrevStation(
                StationHandler.getInstance().getStationByName(prev));
        s.reset();
    }

    private void testCommand(String input){

    }

}
