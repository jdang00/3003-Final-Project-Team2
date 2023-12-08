import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UACentralServer {

    public static final int port = 35000;

    private ServerSocket ClientServerSocket;
    private ServerSocket FittingRoomServerSocket;
    ArrayList<FittingRoomConnection> fittingRoomServerConnectionsList = new ArrayList<>();
    ArrayList<FittingRoomConnection> clientConnectionsList = new ArrayList<>();

    private Logger logger;
    private static final String logFile = "serverLog.txt";


    AtomicInteger clientCount = new AtomicInteger();
    public void setupLogger() {
        try {
            logger = Logger.getLogger(UACentServ.class.getName());
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (IOException e) {
            logger.warning("Error at setupLogger(): " + e.getMessage());
        }
    }

    public synchronized void start() {
        new Thread(this::connectFittingRoom);
        new Thread(this::connectClient);
    }

    public synchronized void connectFittingRoom(){
        while(true){
            FittingRoomConnection connection = new FittingRoomConnection();
            fittingRoomServerConnectionsList.add(connection);
        }
    }

    public synchronized void connectClient(){
        while(true){
            FittingRoomConnection connection = new FittingRoomConnection();
            clientConnectionsList.add(connection);
        }
    }


    class ClientConnection{
        int clientID;
        int IPAddress;
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean isConnected;

        public ClientConnection(){
            try{
                socket = ClientServerSocket.accept();
                isConnected = true;
                clientID = clientCount.incrementAndGet();
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                IPAddress = Integer.parseInt(socket.getInetAddress().getHostAddress());
                logger.info("New fitting room server connection from IP address " + IPAddress);


            }catch(IOException ex){
                logger.warning("Fitting Room Server refused to connect on address  " + IPAddress + " -> " + ex.getMessage());
            }
        }
    }

    class FittingRoomConnection{

        int serverID;
        int IPAddress;
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean isConnected;

        public FittingRoomConnection(){
            try{
                socket = FittingRoomServerSocket.accept();
                isConnected = true;
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                IPAddress = Integer.parseInt(socket.getInetAddress().getHostAddress());
                logger.info("New fitting room server connection from IP address " + IPAddress);


            }catch(IOException ex){
                logger.warning("Fitting Room Server refused to connect on address  " + IPAddress + " -> " + ex.getMessage());
            }
        }

    }




}
