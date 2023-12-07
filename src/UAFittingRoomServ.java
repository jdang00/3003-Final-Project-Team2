import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


public class UAFittingRoomServ {

    private static final String host = "localhost";
    private static final int port = 35555; //Our Port

    Socket cs;

    public UAFittingRoomServ(int serverId) {
        this.serverId = serverId;
        try {
            cs = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(Socket cs) {
        try {
            cs.close();
            System.out.println("Connection Closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int numRooms;
    int numSeats;
    int numCustomers;
    Semaphore seatController;
    Semaphore roomController;
    long systemTime;
    int waiting;
    int changing;
    int serverId;

    BufferedReader in;

    public void acceptClients(){
        try{
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Server #" + serverId + " now " + line);
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    //Simply starts the server
    public static void main(String[] args) {

//        store.systemTime = Long.parseLong(args[0]) * 1000;
//        store.numSeats = Integer.parseInt(args[1]) * 2;
//        store.numRooms = Integer.parseInt(args[1]);
//        store.numCustomers = store.numSeats + store.numRooms;

        ArrayList<UAFittingRoomServ> serverList = new ArrayList<>();

        for(int i = 1 ; i <= 3; i++){
            UAFittingRoomServ store = new UAFittingRoomServ(i);
            serverList.add(store);

            Thread serverThread = new Thread(store::acceptClients);
            serverThread.start();
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

    public void getRoom(int customerID,UAFittingRoomServ store) throws InterruptedException {
        waiting--; 
        changing++;
        roomController.acquire();
        freeSeat();
        System.out.println("\t\tCustomer #" + customerID + " enters the Fitting Room located at <Server "+store+": "+cs.getInetAddress().getHostAddress()+">");
        System.out.println("\t\tWe have "+ waiting + "waiting and "+changing+"changing");
    }

    public void freeRoom(int customerID,UAFittingRoomServ store) throws InterruptedException {
        roomController.release();
        changing--;
        System.out.println("\t\t\tCustomer #" + customerID + " leaves the  Fitting Room.");

    }

    public void getSeat(int customerID,UAFittingRoomServ store) throws InterruptedException {

        if (seatController.tryAcquire()) {
            waiting++;
            System.out.println("\tCustomer #" + customerID + " enters the waiting area on <Server "+store+": "+cs.getInetAddress().getHostAddress()+">"+"and has a seat.");
            System.out.println("\tWe have " + waiting + "waiting on <Server "+store+": "+cs.getInetAddress().getHostAddress());
        } else {
            System.out.println("\tCustomer #" + customerID + " could not find a seat and leaves in frustration.");

        }


    }


    public void freeSeat() {
        seatController.release();
    }

    class Customer extends Thread {
        int customerID;
        UAFittingRoomServ store;

        public Customer(int customerID,UAFittingRoomServ store) {
            this.customerID = customerID;
            this.store = store;
        }

        @Override
        public void run() {
            System.out.println("Customer #" + customerID + " enters the system");
            try {

                store.getSeat(customerID,store);
                store.getRoom(customerID,store);
                Thread.sleep(new Random().nextInt(1000));
                store.freeRoom(customerID,store);


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

    }

}
