import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UAFittingRoomServ {

    public static final int port = 35555;
    private static final String logFile = "serverLog.txt";

    private ServerSocket ss;
    private Logger logger;

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
    public UAFittingRoomServ() {
        try {
            ss = new ServerSocket(port);
            setupLogger();
            logger.info("Server listening on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This is the client handler and is set up to handle inputs
    public void client(Socket cs) {


    }

    //This start method pushes the client connection and sends it to the client() method being passed a socket
    public void start() {
        while (true) {
            try {
                Socket cs = ss.accept();
                logger.info("New client connection from IP address " + cs.getInetAddress().getHostAddress());

                new Thread(() -> client(cs)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Simply starts the server
    public static void main(String[] args) {
        UAFittingRoomServ server = new UAFittingRoomServ();
        server.start();
    }

}
