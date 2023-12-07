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


    }

    public void fittingRoomServerHandler(Socket cs){

        Client c = new Client(new Random().nextInt(200));

        try{

            OutputStream outputStream = cs.getOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(outputStream);

            output.writeObject(c);
            output.close();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    ArrayList<Socket> FitRoomServersList = new ArrayList<>();
    int roundRobinCounter = 0;

    //This start method pushes the client connection and sends it to the client() method being passed a socket
    public void start() {
        while (true) {
            try {


                for(int i = 0; i < numFitServ; i++){
                    Socket fitServ = fs.accept();
                    FitRoomServersList.add(fitServ);
                    logger.info("New server connection from IP address " + fitServ.getInetAddress().getHostAddress());
                    new Thread(() -> fittingRoomServerHandler(fitServ)).start();
                }


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


class Client implements Serializable {
    private int id;
    boolean checkedOut = false;

    public Client(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
