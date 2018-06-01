package Model;


import Model.Network.NetworkController;
import View.CommandLineInterface;

import java.util.ArrayList;
import java.util.Scanner;

/**@author Noah Lehmann*/

public class Main {

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        StationHandler handler = StationHandler.getInstance();
        handler.addStation("robot", "ro");
        handler.addStation("stock", "la");
        handler.addStation("inOut", "ea");

        Station robot = handler.getStationByShortCut("ro");
        Station stock = handler.getStationByShortCut("la");
        Station inOut = handler.getStationByShortCut("ea");


        robot.addPrevStation(stock);
        stock.addPrevStation(inOut);
        stock.addPrevStation(robot);
        inOut.addPrevStation(robot);

        robot.setHopsToNewCarriage(2);
        stock.setHopsToNewCarriage(1);
        inOut.setHopsToNewCarriage(1);

        CommandLineInterface cli = new CommandLineInterface();
        cli.run();
        while(cli.isAlive()) {
            try {
                cli.join();
            } catch (InterruptedException e) {
                continue;
            }
        }
    }
}

