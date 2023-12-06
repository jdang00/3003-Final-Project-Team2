import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class UACentServ {
    public static final int port = 35000;
    private static final String logFile = "serverLog.txt";

    private ServerSocket ss;
    private ServerSocket fs;
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
    public void clientHandler() {



    }

    public void fittingRoomServerHandler(){

    }

    //This start method pushes the client connection and sends it to the client() method being passed a socket
    public void start() {
        while (true) {
            try {

                Socket fitServ = fs.accept();
                logger.info("New server connection from IP address " + fitServ.getInetAddress().getHostAddress());

                Socket client = ss.accept();
                logger.info("New client connection from IP address " + client.getInetAddress().getHostAddress());


                new Thread(this::clientHandler).start();
                new Thread (this::fittingRoomServerHandler).start();
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
