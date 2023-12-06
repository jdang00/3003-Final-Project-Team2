import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class UACentServ {
    public static final int port = 35000;
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

    //Creates our Server
    public UACentServ() {
        try {
            ss = new ServerSocket(port);
            setupLogger();
            logger.info("Server listening on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //This is the client handler and is set up to handle inputs
    public void client() {
        String host = "localhost";
        int port = 35555;

        try (Socket toFitServ = new Socket(host, port);
        ) {
            Scanner sc = new Scanner(System.in);

            while(true){
                String userRequest = sc.nextLine();
                if (userRequest.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            toFitServ.close();
            System.out.println("Connection closed");
            sc.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //This start method pushes the client connection and sends it to the client() method being passed a socket
    public void start() {
        while (true) {
            try {
                Socket cs = ss.accept();
                logger.info("New client connection from IP address " + cs.getInetAddress().getHostAddress());

                new Thread(this::client).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Simply starts the server
    public static void main(String[] args) {
        UACentServ server = new UACentServ();
        server.start();
    }

}
