import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UAFittingRoomServer {

    private static final int port = 35555;

    Socket centralSocket;

    PrintWriter out;
    BufferedReader in;

    public UAFittingRoomServer(String ipAddress){

        try{
            centralSocket = new Socket(ipAddress, port);
            out = new PrintWriter(centralSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(centralSocket.getInputStream()));
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public void acceptClients(){
        try{
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        UAFittingRoomServer store = new UAFittingRoomServer("localhost");
    }
}
