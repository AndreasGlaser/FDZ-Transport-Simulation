import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Station robot = new Station("robot");
        Station stock = new Station("stock");
        Station inOut = new Station("inOut");
        CommandListener listener = new CommandListener();
        Scanner sc = new Scanner(System.in);


        robot.addPrevStation(stock);
        stock.addPrevStation(robot);
        stock.addPrevStation(inOut);
        inOut.addPrevStation(robot);

/*--CommandLineInterface-----------------------------------------------------*/

        String help =   "+–––––––––––––––––––––––––––––––––+\n" +
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
            listener.testCommand("");
        }
        System.out.println("Goodbye");
    }
}
