import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


public class UAFittingRoomServ {

    private static final String host = "localhost";
    private static final int port = 35555; //Our Port

    Socket cs;

    /*
     * <p>
     * -The Constructor UAFittingRoom will set the server id
     * -bound the cs socket to a host and port
     * -Sets up the BufferedReader
     * </p>
     */
    public UAFittingRoomServ(int serverId) {

        this.serverId = serverId;
        try {
            cs = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pw = new PrintWriter(cs.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * <p>
     * The close() method closes the connection to the socket
     * </p>
     */
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
    PrintWriter pw;

    /*
     * <p>
     * The acceptClients() method reads an input of clients and accepts them
     * </p>
     */
    public void acceptClients(){
        try{
            String line;
            while ((line = in.readLine()) != null) {
                Thread custOp = new Thread(new Customer(Integer.parseInt(line), this));
                custOp.start();
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    /*
     * <p>
     * The main method takes in two argument line parameters 
     * -System running time args[0]
     * -Number of seats in the fitting room args[1]
     * </p>
     * @param args[0] The alloted time for the whole system to run
     * @param args[1] The number of designated waiting seats in the fittingRoom
     * @param serverList An array list of servers to be started
     * @param serverThread the thread that represents the server and allows for multiple servers
     */
    public static void main(String[] args) {

        int numServers = 3;


        for(int i = 1 ; i <= numServers; i++){

            UAFittingRoomServ store = new UAFittingRoomServ(i);

            // Checks to see if server needs an extra room / seat based off of the remainders
            int roomsPerServer = (Integer.parseInt(args[1]) - 1) % 3 == i - 1 ? (Integer.parseInt(args[1]) - 1) / 3 + 1 : (Integer.parseInt(args[1]) - 1) / 3;
            int seatsPerServer = (2 * Integer.parseInt(args[1]) - 1) % 3 == i - 1 ? (2 * Integer.parseInt(args[1]) - 1) / 3 + 1 : (2 * Integer.parseInt(args[1]) - 1) / 3;


            store.systemTime = Long.parseLong(args[0]) * 1000;
            store.numSeats = seatsPerServer;
            store.numRooms = roomsPerServer;
            store.numCustomers = store.numSeats + store.numRooms;

            store.seatController = new Semaphore(store.numSeats);
            store.roomController = new Semaphore(store.numRooms);

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
    
    /*
     * <p>
     * The getRoom() method pulls the roomController semaphore and lets the user enter a fitting room 
     * </P>
     * @param customerID the identification number of the client/customer
     * @param store the server that the client/customer is located in
     * @param waiting decreases the count in this instance as the client/customer is no longer waiting in a chair and has now obtained a fitting room
     * @param changing increases the count in this instance as the client/customer is now changing
     */
    public void getRoom(int customerID,UAFittingRoomServ store) throws InterruptedException {
        waiting--;
        changing++;
        roomController.acquire();
        freeSeat();
        System.out.println("\t\tCustomer #" + customerID + " enters the Fitting Room located at <Server "+serverId+": "+cs.getInetAddress().getHostAddress()+">");
        pw.println("\t\tCustomer #" + customerID + " enters the Fitting Room located at <Server "+serverId+": "+cs.getInetAddress().getHostAddress()+">");
        System.out.println("\t\tWe have "+ waiting + " waiting and "+changing+" changing");
        pw.println("\t\tWe have "+ waiting + " waiting and "+changing+" changing");
    }

    /*
     * <p>
     * -The freeRoom() method releases the fitting room(roomController.release()) and lets the client/customer exit the fitting room
     * </p>
     * @param customerID the identification number of the client/customer
     * @param store the server that the client/customer is located in
     * @param changing decreases the count as the client/customer has exited the fitting room 
     * @param roomController Fitting Room semaphore to be released
     */
    public void freeRoom(int customerID,UAFittingRoomServ store) throws InterruptedException {
        roomController.release();
        changing--;
        System.out.println("\t\t\tCustomer #" + customerID + " leaves the  Fitting Room.");
        pw.println("\t\t\tCustomer #" + customerID + " leaves the  Fitting Room.");

    }

    /*
     * <p>
     * The getSeat() method will obtain the waiting chair
     * </p>
     * @param customerID the identification number of the client/customer
     * @param store the server that the client/customer is located in
     * @param waiting increases as the customer has not entered a fitting room and is waiting to enter a fitting room
     * @param seatController waitingRoom chairs semaphore to be acquired
     */
    public void getSeat(int customerID,UAFittingRoomServ store) throws InterruptedException {

        if (seatController.tryAcquire()) {
            waiting++;
            System.out.println("\tCustomer #" + customerID + " enters the waiting area on <Server "+serverId+": "+ cs.getInetAddress().getHostAddress()+"> and has a seat.");
            pw.println("\tCustomer #" + customerID + " enters the waiting area on <Server "+serverId+": "+ cs.getInetAddress().getHostAddress()+"> and has a seat.");

            System.out.println("\tWe have " + waiting + " waiting on <Server "+serverId+": "+cs.getInetAddress().getHostAddress());
        } else {
            System.out.println("\tCustomer #" + customerID + " could not find a seat and leaves in frustration.");

        }


    }


    /*
     * The freeSeat method is called to release the waiting room seatController semapgore
     */
    public void freeSeat() {
        seatController.release();
    }

    /*
     * <p>
     * The Customer class represents the client/customer and includes a constructor and a run() method
     * </p>
     */
    class Customer extends Thread {
        int customerID;
        UAFittingRoomServ store;

        public Customer(int customerID,UAFittingRoomServ store) {
            this.customerID = customerID;
            this.store = store;
        }

        /*
         * the run() method allows the separate threads to run the system
         */
        @Override
        public void run() {
            System.out.println("Customer #" + customerID + " enters the system");
            pw.println("Customer #" + customerID + " enters the system");
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
