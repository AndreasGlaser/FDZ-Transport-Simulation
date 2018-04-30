package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Station robot = new Station("robot", "ro");
        Station stock = new Station("stock", "la");
        Station inOut = new Station("inOut", "ea");
        ArrayList<Station> stationList = new ArrayList<Station>(3);

        CommandListener listener = new CommandListener(stationList);
        Scanner sc = new Scanner(System.in);


        robot.addPrevStation(stock);
        stock.addPrevStation(robot);
        stock.addPrevStation(inOut);
        inOut.addPrevStation(robot);

        stationList.add(robot);
        stationList.add(inOut);
        stationList.add(stock);

        /*--CommandLineInterface-----------------------------------------------------*/

        String help =
                "+–––––––––––––––––––––––––––––––––+\n" +
                "| Type in TestCommand:            |\n" +
                "| -\"q\" to end Programm            |\n" +
                "| -\"h\" for help                   |\n" +
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
            listener.testCommand(input);
        }
        System.out.println("Goodbye");
    }
}

