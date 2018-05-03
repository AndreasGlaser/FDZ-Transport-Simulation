package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class CommandListener {

    private ArrayList<Station> stationList;

    public CommandListener(ArrayList<Station> currentStationList){
        this.stationList = currentStationList;
    }


    /*--TESTMETHODEN-------------------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
    }


    private void invokeInterpreter(String command){
        try{
            new CommandInterpreter(command, stationList).run();
        }catch(IllegalCommandException e){
            System.out.println(e.getMessage());
        }
    }

    /*------------------------------Netzwerkanbindung------------------------------*/
    public void connectToController (String ip, int port){
        Socket socket = null;
        try {
            socket = new Socket (ip, port);

            OutputStream output = socket.getOutputStream();
            PrintStream ps = new PrintStream(output, true);
            InputStream input = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
        } catch (UnknownHostException e){
            System.out.println("Unknown Host...");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("IOProblem...");
            e.printStackTrace();
        } finally {
            if (socket != null){
                try {
                    socket.close();
                    System.out.println("Socket closed");
                } catch (IOException e){
                    System.out.println("Socket cant closed");
                    e.printStackTrace();
                }
            }
        }
    }



}

