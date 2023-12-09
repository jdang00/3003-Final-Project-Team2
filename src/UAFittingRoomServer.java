import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class UAFittingRoomServer {

    private static final int port = 35555;

    Socket centralSocket;

    AtomicReference<PrintWriter> out = new AtomicReference<>();
    AtomicReference<BufferedReader> in = new AtomicReference<>();

    AtomicInteger serverID = new AtomicInteger();

    public UAFittingRoomServer(String ipAddress){

        try{
            centralSocket = new Socket(ipAddress, port);
            out.set(new PrintWriter(centralSocket.getOutputStream(), true)) ;
            in.set(new BufferedReader(new InputStreamReader(centralSocket.getInputStream())));
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public void acceptClients(){
        try{
            String line;
            while ((line = in.get().readLine()) != null) {
                if(line.contains("Metadata")){
                    String[] data = line.split(",");
                    serverID.set(Integer.parseInt(data[1]));
                    System.out.println("Server " + serverID.get() + " assigned and connected to UACentralServer.");
                }else{
                    new Thread(new Customer(Integer.parseInt(line), this)).start();
                }
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        UAFittingRoomServer store = new UAFittingRoomServer("localhost");

        store.systemTime = Long.parseLong(args[0]) * 1000;
        store.numSeats = Integer.parseInt(args[1]);
        store.numRooms = Integer.parseInt(args[1]) * 2;
        store.numCustomers = store.numSeats + store.numRooms;

        store.seatController = new Semaphore(store.numSeats);
        store.roomController = new Semaphore(store.numRooms);


        new Thread(store::acceptClients).start();

    }

    int numRooms;
    int numSeats;
    int numCustomers;
    Semaphore seatController;
    Semaphore roomController;
    long systemTime;
    int waiting;
    int changing;

    /*
     * <p>
     * The getRoom() method pulls the roomController semaphore and lets the user enter a fitting room
     * </P>
     * @param customerID the identification number of the client/customer
     * @param store the server that the client/customer is located in
     * @param waiting decreases the count in this instance as the client/customer is no longer waiting in a chair and has now obtained a fitting room
     * @param changing increases the count in this instance as the client/customer is now changing
     */
    public void getRoom(int customerID,UAFittingRoomServer store) throws InterruptedException, UnknownHostException {
        waiting--;
        changing++;
        roomController.acquire();
        freeSeat();
        System.out.println("\t\tCustomer #" + customerID + " enters the Fitting Room located at <Server "+serverID.get()+": "+ InetAddress.getLocalHost() +">");
        out.get().println("\t\tCustomer #" + customerID + " enters the Fitting Room located at <Server "+serverID.get()+": "+InetAddress.getLocalHost()+">");
        System.out.println("\t\tWe have "+ waiting + " waiting and "+changing+" changing");
        out.get().println("\t\tWe have "+ waiting + " waiting and "+changing+" changing");
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
    public void freeRoom(int customerID,UAFittingRoomServer store) throws InterruptedException {
        roomController.release();
        changing--;
        System.out.println("\t\t\tCustomer #" + customerID + " leaves the  Fitting Room.");
        out.get().println("\t\t\tCustomer #" + customerID + " leaves the  Fitting Room.");

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
    public void getSeat(int customerID,UAFittingRoomServer store) throws InterruptedException, UnknownHostException {

        if (seatController.tryAcquire()) {
            waiting++;
            System.out.println("\tCustomer #" + customerID + " enters the waiting area on <Server "+serverID.get()+": "+ InetAddress.getLocalHost()+"> and has a seat.");
            out.get().println("\tCustomer #" + customerID + " enters the waiting area on <Server "+serverID.get()+": "+ InetAddress.getLocalHost()+"> and has a seat.");

            System.out.println("\tWe have " + waiting + " waiting on <Server "+serverID.get()+">: "+InetAddress.getLocalHost());
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
        UAFittingRoomServer store;

        public Customer(int customerID,UAFittingRoomServer store) {
            this.customerID = customerID;
            this.store = store;
        }

        /*
         * the run() method allows the separate threads to run the system
         */
        @Override
        public void run() {
            System.out.println("Customer #" + customerID + " enters the system");
            out.get().println("Customer #" + customerID + " enters the system");
            try {

                store.getSeat(customerID,store);
                store.getRoom(customerID,store);
                Thread.sleep(new Random().nextInt(1000));
                store.freeRoom(customerID,store);


            } catch (InterruptedException | UnknownHostException e) {
                throw new RuntimeException(e);
            }


        }

    }

}

