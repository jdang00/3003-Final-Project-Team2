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
    public UAClient() {
        try {
            cs = new Socket(host, port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            cs.close();
            System.out.println("Connection Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        for(int i = 0; i < 15; i++){
            UAClient client = new UAClient();
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
