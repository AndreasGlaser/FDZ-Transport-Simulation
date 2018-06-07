package View;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface extends Thread{

    private Scanner sc;
    private Facade facade;
    private final String HELP =
                    "+–––––––––––––––––––––––––––––––––+\n" +
                    "| Type in TestCommand:            |\n" +
                    "| -\"q\" to end program             |\n" +
                    "| -\"h\" for help                   |\n" +
                    "| -\"p\" to print current State     |\n" +
                    "| -\"a\" to add Station             |\n" +
                    "| -\"d\" to delete Station          |\n" +
                    "| -\"m\" to manipulate Station      |\n" +
                    "| -\"FDZ-Command\" to test System   |\n" +
                    "+–––––––––––––––––––––––––––––––––+";
    private final String QUIT =
                    "+––––––––––––––––––––––––––+\n"+
                    "|Detected Shutdown, do you |\n" +
                    "|want to quit as well?[y|*]|\n" +
                    "+––––––––––––––––––––––––––+\n#";


    public CommandLineInterface() {
        sc = new Scanner(System.in);
        System.out.println(HELP);
        facade = new Facade();
        for(int i=0; i< StationHandler.getInstance().getStationList().size(); ++i){
            facade.getStationChangedProperty(StationHandler.getInstance().getStationList().get(i).getName()).addListener((observable, oldValue, newValue) ->{
                System.err.println("CHANGED");
                printState();
            } );
        }
    }

    @Override
    public void run(){
        while (true) {

            System.out.print(">");

            String input = sc.nextLine();
            if(input.length()>1) {
                if (input.contains("STStK004")) {
                    if (quit()) break;
                }
                testCommand(input);
            }else if(input.length()==1) {
                switch (input.charAt(0)) {
                    case 'q': break;
                    case 'h': System.out.println(HELP); continue;
                    case 'd': delete(); continue;
                    case 'a': add(); continue;
                    case 'm': manipulate(); continue;
                    case 's': printStatus(); continue;
                    case 'p': printState(); continue;
                }
                if(input.charAt(0)=='q'){break;}
            }
        }
        System.out.println("Goodbye");
    }

    private void printStatus(){
        /*TODO*/
        System.out.println("To be implemented");
    }

    private void printState(){
        List<Station> stationList = StationHandler.getInstance().getStationList();/*TODO falscher Zugriff*/
        for(int i=0; i<stationList.size(); i++){
            /*foreach station*/
            System.out.print(
                    "\t|"+stationList.get(i).getName()+"\n"+
                             "\t|––––––––––––––––––––––––\n" +
                             "\t|->shortCut  |" + stationList.get(i).getShortCut() + "\n"+
                             "\t|->   id     |");
            try {
                System.out.print(facade.getSledsInStation(stationList.get(i).getName()).get(0) + "\n");
            }catch(Exception e){
                System.out.print("-2\n");
            }
            System.out.print("\t|->congested |" + stationList.get(i).isCongested()+"\n"+
                             "\t|->hopsBack  |" + stationList.get(i).getHopsToNewCarriage());
            ArrayList<Station> prev = stationList.get(i).getPrevStations();
            for (int j = 0; j < prev.size(); j++) {
                System.out.print(
                        "\t| prev"+j+" = "+ prev.get(j).getName() +"\n");
            }
            System.out.println();
        }
    }

    private void add(){
        String name, shortCut;
        System.out.println("adding a Station...");

        System.out.print("Type in name:");
        name = sc.nextLine();

        System.out.print("Type in ShortCut:");
        shortCut = sc.nextLine();

        try{
            facade.addStation(name,shortCut);
            System.out.println("Station "+name+" successfully added");
        }catch(IllegalSetupException e){
            System.out.println("Could not add Station"+name);
        }
    }

    private void delete(){
        String name;
        System.out.print("Name of Station to delete:");
        name = sc.nextLine();
        try{
            facade.deleteStation(name);
            System.out.println("Station "+name+" successfully deleted");
        }catch(NullPointerException e){
            System.out.println("No such Station");
        }
    }

    private void manipulate(){
        String name;

        System.out.println("Manipulate Station...");
        System.out.print("Which Station:");
        name = sc.nextLine();
        System.out.print(
                "Which Attribute:\n"+
                "\t(1) hopsBackToNewCarriage\n"+
                "\t(2) prevStation List\n"+
                "#");
        switch (sc.nextInt()){
            case 1:
                System.out.print("Hops back to new Carriage:");
                try{
                    facade.setHopsToNewCarriage(name, sc.nextInt());
                    System.out.println("Set Hops of "+name+" successfully");
                }catch(Exception e){
                    System.out.println("Wrong Input, try again!");
                }
                break;
            case 2:
                System.out.print("How many prev Stations?");
                try{
                    int prevs=sc.nextInt();
                    for(int i=0; i<prevs; ++i) {
                        addPrev(name);
                    }
                }catch(Exception e) {
                    System.out.println("Wrong Input, try again!");
                }
        }
    }

    private void addPrev(String station){
        Scanner s = new Scanner(System.in);
        System.out.print("name of prevStation:");
        String prev = s.nextLine();
        System.out.println(prev);

        try{
            facade.addPrevStation(station, prev);
            System.out.println("PrevStation "+prev+" added successfully to "+station);
        }catch(NullPointerException e){
            System.out.println("Could not add "+prev+" to "+station);
        }
        s.reset();
    }

    private void testCommand(String input){
        facade.testCommand(input);
    }

    private boolean quit(){
        System.out.print(QUIT);
        if(sc.next().compareToIgnoreCase("y") == 0) {
            return true;
        }
        if(sc.hasNext()) sc.nextLine();
        return false;
    }

}
