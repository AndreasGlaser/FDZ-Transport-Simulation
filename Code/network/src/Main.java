import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        /*
        String sentence;
        String modifiedSentence;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        InetAddress ipaddr = InetAddress.getByName("192.168.56.1");
        NetworkClient nc = new NetworkClient(ipaddr, 40004);
        nc.run();
        Socket cl = new Socket(ipaddr, 40001);
        DataOutputStream outToServer = new DataOutputStream(cl.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(cl.getInputStream()));
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
        modifiedSentence = inFromServer.readLine();
        System.out.println("FROM SERVER: " + modifiedSentence);
        cl.close();
        */
        if(args.length > 0 && (args[0].contains("-h") || args[0].contains("help")))
        {
            System.out.println("Syntax: <ip>");
            System.out.println("");
            System.out.println("Standard-Werte: 47354");

            return;
        }

        int port = ( args.length > 1 ) ? Integer.parseInt(args[1]) : 47331;
        byte[] ipAddr = new byte[]{127, 0, 0, 1};
        InetAddress addr = InetAddress.getByAddress(ipAddr);
        FDZServerSocket serverSocket = new FDZServerSocket(addr,port);

        long ms = System.currentTimeMillis()/1000;
        System.out.println(ms);

        System.out.println("Warte auf Verbindung");
        serverSocket.awaitConnection();
        Scanner sc = new Scanner(System.in);

        //"STStK0011527162650:000002ro"

        while (true) {
            String s = sc.next();

            switch (s) {
                //empty carriage
                case "1":
                    serverSocket.send("STStK0011527162650:000002ro");
                    break;
                //release carriage
                case "2":
                    serverSocket.send("STStK0021527162650:00000211");
                    break;
                //reposition carriage
                case "3":
                    serverSocket.send("STStK0031527162650:00000411la");
                    break;
                //shutdown
                case "4":
                    serverSocket.send("STStK0041527162650:000000");
                    break;
                //print incomming message
                case "5":
                    System.out.println(serverSocket.receive());
                    break;
            }


            if (s.equals("q")){
                break;
            }
            //System.out.println(serverSocket.receive());

        }


    }

}