import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class UAClient {
    //Creating our Socket,Host, and Port
    private static final String host = "localhost";
    private static final int port = 35000; //Our Port
    private Socket cs;

    //Creating the Client and obtaining the users input stream
    
    /* 
     * <p>
     * The UAClient() method pushes a host and a port to the cs socket
     * </p>
     * 
     * @param cs a Socket
     */
    public UAClient() {
        try {
            cs = new Socket(host, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * <p>
     * The close() method closes the connection to the socket
     * </p>
     */
    public void close() {
        try {
            cs.close();
            System.out.println("Connection Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * <p>
     * The main method of UAClient will wait for the user to exit the program as well as add the connecting clients to a ArrayList called clientList
     * </p>
     * @param clientList an ArrayList of UAClients to manage the incoming client traffic 
     */
    public static void main(String[] args) {

        ArrayList<UAClient> clientList = new ArrayList<>();

        for(int i = 0; i < 50; i++){
            UAClient client = new UAClient();
            clientList.add(client);
        }



        Scanner sc = new Scanner(System.in);

        while(true){
            String userRequest = sc.nextLine();
            if (userRequest.equalsIgnoreCase("exit")) {
                break;
            }
        }

        sc.close();
    }
}
