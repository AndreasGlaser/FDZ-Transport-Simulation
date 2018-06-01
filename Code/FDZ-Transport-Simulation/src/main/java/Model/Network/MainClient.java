package Model.Network;

import Model.Station;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainClient {

    public static void main(String[] args) throws UnknownHostException, FDZNetworkException {
        Station robot = new Station("robot", "ro");
        Station stock = new Station("stock", "la");
        Station inOut = new Station("inOut", "ea");
        ArrayList<Station> stationList = new ArrayList<Station>(3);

        NetworkController ch = new NetworkController(stationList);
        byte[] ipAddr = new byte[]{127, 0, 0, 1};
        ch.connect(ipAddr, 47331);
        Scanner sc = new Scanner(System.in);

        while (true){
            String s = sc.next();

            switch (s) {
                //empty carriage
                case "1":
                    ch.sendCommandAnswer("STStK0041527162650:000000");
                    break;
                case "2":
                    ch.connect(ipAddr, 47332);
                    break;
                case "3":
                    ch.disconnect();
                    break;
            }

            if (s.equals("q")){
                break;
            }
        }

        ch.disconnect();
    }

}
