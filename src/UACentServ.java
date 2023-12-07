import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * @author Justin Dang
 * @author Doyle McHaffie
 * @author Morgan
 * <p>
 * The UACentServ is the central server for this project, and handles the logger, client handler, and load balancing
 * </p>
 */
public class UACentServ{
    public static final int port = 35000;
    private static final String logFile = "serverLog.txt";

    private ServerSocket ss;
    private ServerSocket fs;
    private Logger logger;

    static private int numFitServ;

    /*
     * <p>
     * The setupLogger() method initializes the logger for the central server and places the logs on a text file
     * </p>
     * @param logFile The logFile that all server information goes to which is serverLog.txt
     * @param logger
     */
    public void setupLogger() {
        try {
            logger = Logger.getLogger(UACentServ.class.getName());
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * <p>
     * This UACentServ() constructor creates the logger and bounds two sockets to two different ports
     * </p>
     * @param setupLogger() in this instance is created for the server to start logging 
     * @param fs is a socket bound to port 35555
     * @param ss is a socket bound to port 35000 but is dynamic 
     */
    public UACentServ() {
        try {
            setupLogger();

            fs = new ServerSocket(35555);
            logger.info("Server listening on port 35555");

            ss = new ServerSocket(port);
            logger.info("Server listening on port " + port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * Client handler method
     */
    public void clientHandler(Socket cs) {

        new Thread(() -> fittingRoomServerHandler(FitRoomServersList.get(loadBalancer()), loadBalancer())).start();


    }


    /*
     * fittingRoomServerHandler takes in a socket and server number and currently handles each client incoming
     */
    public void fittingRoomServerHandler(Socket cs, int servNum){

        try{
            PrintWriter pw = PrintStream.get(servNum);
            pw.println("moving Customer #" + clientCounter++);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    ArrayList<Socket> FitRoomServersList = new ArrayList<>();
    ArrayList<PrintWriter> PrintStream = new ArrayList<>();
    int roundRobinCounter = 1;

    int clientCounter = 0;

    //This start method pushes the client connection and sends it to the client() method being passed a socket
    /*
     * <p>
     * The start method accepts a connection for a server and adds it to a =n ArrayList of servers
     * The start method additionally accepts client connections and starts the thread with the client 
     * </p>
     * @param FitRoomServerList an array list of servers to manage clients/customers
     * @param clientServ an instance of the client that is pushed to boot up
     */
    public void start() {

        try {

            for(int i = 0; i < numFitServ; i++){
                System.out.println(i);
                Socket fitServ = fs.accept();
                FitRoomServersList.add(fitServ);
                PrintWriter out = new PrintWriter(fitServ.getOutputStream(), true);
                PrintStream.add(out);
                logger.info("New fitting room server connection from IP address " + fitServ.getInetAddress().getHostAddress());

            }

        }catch (IOException ex){
            ex.printStackTrace();
        }


        while (true) {

            try {

                Socket clientServ = ss.accept();
                logger.info("New client connection from IP address " + clientServ.getInetAddress().getHostAddress());
                new Thread(() -> clientHandler(clientServ)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * <p>
     * The loadBalancer() method returns an integer and handles the load balancing for which server a client will be connected to 
     * </p>
     */
    public int loadBalancer(){

        if(roundRobinCounter == FitRoomServersList.size()){
            roundRobinCounter = 1;
            return 0;
        }else {
            return roundRobinCounter++;
        }

    }



    /*
     * <p>
     * The main() method establishes the number of fitting room servers and starts the central server
     * </p>
     * @param numFitServ The specified amount of servers 
     * @param server the creation of the central server
     */
    public static void main(String[] args) {
        numFitServ = 3;
        UACentServ server = new UACentServ();

        server.start();

    }


}
