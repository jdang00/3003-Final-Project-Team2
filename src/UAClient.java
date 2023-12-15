import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
/**
 * The UAClient class just obtains our user input stream and initalizes the amount of threads to be ran through the FittingRoomServer
 * @author Justin Dang
 * @author Doyle McHaffie
 * @author Morgan Ballard
 */
public class UAClient {
    //Creating our Socket,Host, and Port
    private static final String host = "localhost";
    private static final int port = 35000; //Our Port



    //Creating the Client and obtaining the users input stream

    /**
     * the UAClient() method will bind the socket to a host and port 
     * @param host will be our local host
     * @param port is the default assigned port of 35000
     */
    public UAClient() {
        try {
            Socket cs = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * the main() method will take one command line input of the number of clients to be ran through UAFittingRoomServer and starts those threads up. 
     */
    public static void main(String[] args) {
        int nums = Integer.parseInt(args[0]);
        for(int i = 0; i < nums; i++){
            UAClient client = new UAClient();
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
