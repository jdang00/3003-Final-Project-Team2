import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class UACentServ{
    public static final int port = 35000;
    private static final String logFile = "serverLog.txt";

    private ServerSocket ss;
    private ServerSocket fs;
    private Logger logger;

    static private int numFitServ;

    //Setting up our logger
    public void setupLogger() {
        try {
            logger = Logger.getLogger(UAServer.class.getName());
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Creates our Server
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


    //This is the client handler and is set up to handle inputs
    public void clientHandler(Socket cs) {

        new Thread(() -> fittingRoomServerHandler(FitRoomServersList.get(loadBalancer()), loadBalancer())).start();


    }


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

    public int loadBalancer(){

        if(roundRobinCounter == FitRoomServersList.size()){
            roundRobinCounter = 1;
            return 0;
        }else {
            return roundRobinCounter++;
        }

    }



    //Simply starts the server
    public static void main(String[] args) {
        numFitServ = 3;
        UACentServ server = new UACentServ();

        server.start();

    }


}



