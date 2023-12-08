import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
            logger.warning("Error at setupLogger()");
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
    public UACentServ(int numFitServ) {
        UACentServ.numFitServ = numFitServ;
        try {
            setupLogger();

            fs = new ServerSocket(35555);
            logger.info("Server listening on port 35555");

            ss = new ServerSocket(port);
            logger.info("Server listening on port " + port);

        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Error on UACentServ(int numFitServ) constructor ports are 35555 and " + port);
        }
    }



    /*
     * Client handler method
     */
    public void clientHandler(Socket cs) {

        int balance = loadBalancer();
        new Thread(() -> fittingRoomServerHandler(FitRoomServersList.get(balance), balance)).start();
        logger.info("Client " + cs.getInetAddress().getHostAddress() + " is being balanced");


    }


    /*
     * fittingRoomServerHandler takes in a socket and server number and currently handles each client incoming
     */
    public void fittingRoomServerHandler(Socket cs, int servNum){

        try{
            PrintWriter pw = PrintStream.get(servNum);
            pw.println(clientCounter++);
            logger.info("Current State of Client Counter "+ clientCounter);
            String line;
            while ((line = ReaderStream.get(servNum).readLine()) != null){
                System.out.println(line);
            }


        }catch (Exception ex){
            ex.printStackTrace();
            logger.warning("Error occured on fittingRoomServerHandler() from server "+ servNum + " located at " + cs.getInetAddress().getHostAddress());
        }
    }


    ArrayList<Socket> FitRoomServersList = new ArrayList<>();
    ArrayList<PrintWriter> PrintStream = new ArrayList<>();
    ArrayList<BufferedReader> ReaderStream = new ArrayList<>();
    int roundRobinCounter = 1;

    int clientCounter = 1;

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
                Socket fitServ = fs.accept();
                FitRoomServersList.add(fitServ);
                PrintWriter out = new PrintWriter(fitServ.getOutputStream(), true);
                BufferedReader in = new BufferedReader (new InputStreamReader(fitServ.getInputStream()));
                ReaderStream.add(in);
                PrintStream.add(out);

                logger.info("New fitting room server connection from IP address " + fitServ.getInetAddress().getHostAddress());

            }

        }catch (IOException ex){
            ex.printStackTrace();
            logger.warning("Error at start() method on the fitServ socket from IP Adress");
        }


        while (true) {

            try {

                Socket clientServ = ss.accept();
                logger.info("New client connection from IP address " + clientServ.getInetAddress().getHostAddress());
                new Thread(() -> clientHandler(clientServ)).start();

            } catch (IOException e) {
                e.printStackTrace();
                logger.warning("Error at start() method on the clientServ socket");
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

        UACentServ centralServer = new UACentServ(3);
        centralServer.start();



    }

}
