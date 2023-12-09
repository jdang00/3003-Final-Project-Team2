import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UACentralServer {

    public static final int ClientPort = 35000;
    public static final int FittingPort = 35555;


    private ServerSocket ClientServerSocket;
    private ServerSocket FittingRoomServerSocket;

    List<FittingRoomConnection> fittingRoomServerConnectionsList = Collections.synchronizedList(new ArrayList<>());
    List<ClientConnection> clientConnectionsList = Collections.synchronizedList(new ArrayList<>());
    private Logger logger;
    private static final String logFile = "serverLog.txt";


    AtomicInteger clientCount = new AtomicInteger(1);
    AtomicInteger fittingRoomServerCount = new AtomicInteger();
    AtomicInteger balancer = new AtomicInteger();

    static volatile boolean acceptConnections = true;
    public static void main(String[] args) {
        UACentralServer central = new UACentralServer();
        central.start();


    }

    public synchronized void setupLogger() {
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

    public UACentralServer(){
        try{

            setupLogger();
            FittingRoomServerSocket = new ServerSocket(FittingPort);
            logger.info("Central Server listening on port 35555 for Fitting Room Servers");

            ClientServerSocket = new ServerSocket(ClientPort);
            logger.info("Central Server listening on port 35000 for clients");


        }catch (IOException ex){
            logger.warning("Error accepting connections -> " + ex.getMessage());

        }
    }

    public void userInput(){
        Scanner sc = new Scanner(System.in);

        while (acceptConnections){
            if(sc.nextLine().equalsIgnoreCase("EXIT")){

                acceptConnections = false;
                logger.info("Central server no longer listening to connections.");
            }
        }

        }


    public void start() {
        new Thread(this::connectFittingRoom).start();
        new Thread(this::connectClient).start();
        new Thread(this::userInput).start();

    }

    public void fittingRoomServerHandler(FittingRoomConnection connection, int clientID){

        connection.out.println(clientID);

        String line;

        try{
            while((line = connection.in.readLine()) != null){
                System.out.println(line);
            }
        }catch(Exception ex){
            logger.warning("Error occurred on fittingRoomServerHandler() from server " + connection.serverID + " located at " + connection.socket.getInetAddress().getHostAddress());

        }

    }

    public void clientHandler(ClientConnection connection){

        int calculateServerNumber = balancer();
        new Thread(()-> fittingRoomServerHandler(fittingRoomServerConnectionsList.get(calculateServerNumber), connection.clientID)).start();
        logger.info("Customer " + connection.socket.getInetAddress().getHostAddress() + " is being assigned to FittingRoom Server " + calculateServerNumber + 1);

    }

    public synchronized int balancer(){
        if(fittingRoomServerCount.get() != 1){
            if(balancer.get() == fittingRoomServerCount.get()){
                balancer.set(1);
                return 0;
            }else{
                return balancer.getAndIncrement();
            }
        }else{
            return balancer.get();

        }
    }

    public void connectFittingRoom(){

        while(acceptConnections){
            FittingRoomConnection connection = new FittingRoomConnection();
            fittingRoomServerConnectionsList.add(connection);
        }

    }

    public void connectClient(){
        while(acceptConnections){
            ClientConnection connection = new ClientConnection();
            clientConnectionsList.add(connection);

            if(fittingRoomServerCount.get() != 0){
                new Thread(() -> clientHandler(connection)).start();
            }

        }

    }


    public class ClientConnection{
        int clientID;
        String IPAddress;
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean isConnected;

        boolean isProcessed;

        public ClientConnection(){
            try{
                socket = ClientServerSocket.accept();
                isConnected = true;
                clientID = clientCount.incrementAndGet();
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                IPAddress = socket.getInetAddress().getHostAddress();
                logger.info("New client connection from IP address " + IPAddress);


            }catch(IOException ex){
                logger.warning("Fitting Room Server refused to connect on address  " + IPAddress + " -> " + ex.getMessage());
            }
        }
    }

    public class FittingRoomConnection{

        int serverID;
        String IPAddress;
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        boolean isConnected;

        public FittingRoomConnection(){
            try{
                socket = FittingRoomServerSocket.accept();
                serverID = fittingRoomServerCount.incrementAndGet();
                isConnected = true;
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                IPAddress = socket.getInetAddress().getHostAddress();
                logger.info("New fitting room server connection from IP address " + IPAddress);

                out.println("Metadata,"+serverID+","+IPAddress+","+isConnected);


            }catch(IOException ex){
                logger.warning("Fitting Room Server refused to connect on address  " + IPAddress + " -> " + ex.getMessage());
            }
        }

    }




}
