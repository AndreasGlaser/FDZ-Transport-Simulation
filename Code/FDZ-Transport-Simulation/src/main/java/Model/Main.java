package Model;


import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

/*--@author nlehmann-------------------------------------------------------------*/


public class Main {

    private static int id=-1;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Station robot = new Station("robot", "ro");
        Station stock = new Station("stock", "la");
        Station inOut = new Station("inOut", "ea");
        ArrayList<Station> stationList = new ArrayList<Station>(3);

        CommandListener listener = new CommandListener(stationList);

        robot.addPrevStation(stock);
        stock.addPrevStation(inOut);
        stock.addPrevStation(robot);
        inOut.addPrevStation(robot);

        robot.setHopsToNewCarriage(2);
        stock.setHopsToNewCarriage(1);
        inOut.setHopsToNewCarriage(1);

        stationList.add(robot);
        stationList.add(inOut);
        stationList.add(stock);

        /*--CommandLineInterface-----------------------------------------------------*/

        String help =
                "+–––––––––––––––––––––––––––––––––+\n" +
                "| Type in TestCommand:            |\n" +
                "| -\"q\" to end Programm            |\n" +
                "| -\"h\" for help                   |\n" +
                "| -\"p\" to print current State     |\n" +
                "| -\"i\" insert new Station         |\n" +
                "| -\"d\" delete Station             |\n" +
                "| -\"FDZ-Command\" to test System   |\n" +
                "+–––––––––––––––––––––––––––––––––+";
        System.out.println(help);

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
                printState(stationList);
                continue;
            }
            if (input.compareTo("i") == 0){
                /*TODO insert station*/
                System.out.println("##not yet tested##");
                newStation(stationList);
                continue;
            }
            if (input.compareTo("d") == 0){
                /*TODO delete Station*/
                System.out.println("##not yet implemented##");
                continue;
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
            listener.testCommand(input);
        }
        System.out.println("Goodbye");
    }

    private static void printState(ArrayList<Station> stationList){
        for(int i=0; i<stationList.size(); i++){
            /*foreach station*/
            System.out.println(
                        "\t|"+stationList.get(i).getName()+"\n"+
                        "\t|––––––––––––––––––––––––\n" +
                        "\t|->shortCut  |" + stationList.get(i).getShortCut() + "\n"+
                        "\t|->   id     |" + stationList.get(i).getSledInside() + "\n"+
                        "\t|->congested |" + stationList.get(i).isCongested() + "\n");
        }
    }

    private static void newStation(ArrayList<Station> stations){
        System.out.print("Name of Station (not shortCut!):");
        String name = sc.nextLine();
        System.out.print("shortcut for Name:");
        String shortCut = sc.nextLine();
        Station temp = new Station(name, shortCut);
        stations.add(temp);
        System.out.print("How many directly previous Stations?:");
        int prevs = sc.nextInt();
        if(sc.hasNext()) sc.nextLine();
        for (int i=0; i<prevs; i++){
            System.out.print("type in shortcut for prevStation:");
            String sCut = sc.nextLine();
            temp.addPrevStation(findSCInList(sCut, stations));
            if(sc.hasNext()) sc.nextLine();
        }
        System.out.print("How many Hops back to CarriageSource:");
        temp.setHopsToNewCarriage(sc.nextInt());
        if(sc.hasNext()) sc.nextLine();
    }

    private static void deleteStation(){

    }

    static int getID(){
        return id;
    }
    static void saveID(int id){Main.id = id;}

    private static Station findSCInList(String position, ArrayList<Station> stationList) {
        /*returns null if POS not found*/
        int idx = 0;
        while (stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return null;}
        }
        return stationList.get(idx);
    }

}

