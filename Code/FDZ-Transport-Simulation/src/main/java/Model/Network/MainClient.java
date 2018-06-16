package Model.Network;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainClient {

    public static void main(String[] args) throws UnknownHostException, FDZNetworkException {

        try {
            new Facade().addStation("Robot", "ro");
            new Facade().addStation("Storage", "st");
            new Facade().addStation("I/O", "io");
        } catch (IllegalSetupException e) {
            e.printStackTrace();
        }
        new Facade().addPrevStation("Storage", "Robot");
            new Facade().addPrevStation("Storage", "I/O");
            new Facade().addPrevStation("Robot", "Storage");
            new Facade().addPrevStation("I/O", "Robot");
            try {
                new Facade().setHopsToNewCarriage("Robot", 2);
                new Facade().setHopsToNewCarriage("Storage", 1);
                new Facade().setHopsToNewCarriage("I/O", 1);
            }catch(Exception e){}


        NetworkController ch = NetworkController.getInstance();
        byte[] ipAddr = new byte[]{(byte)Integer.parseInt("172"),(byte)Integer.parseInt("16"), (byte)Integer.parseInt("48"), (byte)Integer.parseInt("24")};
        //byte [] ipAddr = new byte[]{127,0,0,1};
        ch.connect(ipAddr, 40001);
        Scanner sc = new Scanner(System.in);

        boolean test = true;
        while (test){
            String s = sc.next();

            switch (s) {
                //empty carriage
                case "1":
                    ch.acknowledge1("1527162650:00");
                    break;
                case "2":
                    ch.connect(ipAddr, 47331);
                    break;
                case "3":
                    ch.disconnect();
                    //test = false;
                    break;
            }

            if (s.equals("q")){
                break;
            }
        }

        ch.disconnect();
    }

}
