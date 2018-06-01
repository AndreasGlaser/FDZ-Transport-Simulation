package View;

import Controller.CLIController;
import Model.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface extends Thread{

    private Scanner sc;
    private CLIController controller;
    private final String help =
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

    public CommandLineInterface() {
        sc = new Scanner(System.in);
        controller = new CLIController();
        System.out.println(help);
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
                    case 'h': System.out.println(help); continue;
                    case 'p': printState(controller.getStationList()); continue;
                    case 'd': delete(); continue;
                    case 'a': add(); continue;
                    case 'm': manipulate(); continue;
                    case 's': printStatus(); continue;
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

    private void printState(List<Station> stationList){
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
        String name, shortCut;
        System.out.println("adding a Station...");

        System.out.print("Type in name:");
        name = sc.nextLine();

        System.out.print("Type in ShortCut:");
        shortCut = sc.nextLine();

        if(controller.addStation(name,shortCut)){
            System.out.println("Station "+name+" successfully added");
        }else{
            System.out.println("Could not add Station"+name);
        }
    }

    private void delete(){
        String name;
        System.out.print("Name of Station to delete:");
        name = sc.nextLine();
        if(controller.deleteStation(name)){
            System.out.println("Station "+name+" successfully deleted");
        }else{
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
                    if(controller.setHopsToNewCarriage(name, sc.nextInt())){
                        System.out.println("Set Hops of "+name+" successfully");
                    }else{
                        System.out.println(name+" not known or hops<0 || hops>|stations|");
                    }
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

        if(controller.addPrevStation(station, prev)){
            System.out.println("PrevStation "+prev+" added successfully to "+station);
        }else{
            System.out.println("Could not add "+prev+" to "+station);
        }
        s.reset();
    }

    private void testCommand(String input){
        controller.testCommand(input);
    }

    private boolean quit(){
        System.out.print(
                "+––––––––––––––––––––––––––+\n" +
                        "|Detected Shutdown, do you |\n" +
                        "|want to quit as well?[y|*]|\n" +
                        "+––––––––––––––––––––––––––+\n#");
        if(sc.next().compareToIgnoreCase("y") == 0) {
            return true;
        }
        if(sc.hasNext()) sc.nextLine();
        return false;
    }

}
