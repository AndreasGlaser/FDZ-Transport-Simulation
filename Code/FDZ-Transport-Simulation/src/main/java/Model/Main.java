package Model;


import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;
import View.CommandLineInterface;

import java.net.UnknownHostException;
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

        NetworkController ch = NetworkController.getInstance();
        //byte[] ipAddr = new byte[]{(byte)Integer.parseInt("127"),(byte)Integer.parseInt("0"), (byte)Integer.parseInt("0"), (byte)Integer.parseInt("1")};
        byte [] ipAddr = new byte[]{127,0,0,1};
        try{
            ch.connect(ipAddr, 47331);
        }catch(UnknownHostException e){
            System.err.println("Unknown Host");
        }

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

