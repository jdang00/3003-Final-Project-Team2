import java.io.*;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UAServer {
	public static final int port = 32005;
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
	public UAServer() {
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
		try (BufferedReader br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				PrintWriter pw = new PrintWriter(cs.getOutputStream(), true)) {
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				logger.info("Received command from client " + cs.getInetAddress().getHostAddress() + ": " + inputLine);
				String[] request = inputLine.split(" ");
        String response;
        //The requests variable is used to identify what type of request is being input we could use something like
        //if(request[0] == "Enter Shop") for example we could have a response be
        //response = "Client " cs.getInetAddress().getHostAddress() + " has entered the shop"

        //pw.print(response);
        
				pw.println(request[0]);
				logger.info("Request Made From Client " + cs.getInetAddress().getHostAddress() + ": " + request[0]);
			}

			logger.info("Client " + cs.getInetAddress().getHostAddress() + " disconnected");

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

				new Thread(() -> client(cs)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

  //Simply starts the server
	public static void main(String[] args) {
		UAServer server = new UAServer();
		server.start();
	}

}
