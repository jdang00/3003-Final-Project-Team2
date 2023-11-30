import java.io.*;
import java.net.*;
import java.util.Scanner;

public class UAFittingRoom {
   //Creating our Socket,Host, and Port 
	private static final String host = "localhost"; 
	private static final int port = 32005; //Our Port 
	
	private Socket cs;
	private PrintWriter pw;
	private BufferedReader br;
	
	
	//Creating the Client and obtaining the users input stream
	public UAFittingRoom() {
		try {
			cs = new Socket(host,port);
			pw = new PrintWriter(cs.getOutputStream(),true);
			br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//Takes a request from the user 
	public void sendRequest(String request) {
		try {
			pw.println(request);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public String response() {
		try {
			return br.readLine();
		}catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void close() {
		try {
			cs.close();
			System.out.println("Connection Closed.");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args){
	    UAFittingRoom client = new UAFittingRoom();
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Welcome to the UAFittinRoom Terminal Interface!");
        System.out.println("Please enter your command or type 'exit' to quit.");
        System.out.println("Available Commands:");
        System.out.println("- IP: Retrieve client’s IP Address");
        
        while(true) {
        	System.out.println("Enter Command: ");
        	String userRequest = sc.nextLine();
        	
        	if(userRequest.equalsIgnoreCase("exit")) {
        		break;
        	}
        	
        	client.sendRequest(userRequest);
        	String response = client.response();
        	
        	if(response!= null) {
        		System.out.println("Server Response: " + response);
        	}else {
        		System.out.println("Error in Server Communication!");
        		break;
        	}
        }
        
        client.close();
        sc.close();
}
