import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UAFittingRoomServ {

    private static final String host = "localhost";
    private static final int port = 35555; //Our Port
    private Socket cs;


    //Setting up our logger

    public UAFittingRoomServ() {
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

    int numRooms;
    int numSeats;
    int numCustomers;
    Semaphore seatController;
    Semaphore roomController;
    long systemTime;

    static UAFittingRoomServ store = new UAFittingRoomServ();


    //Simply starts the server
    public static void main(String[] args) {

//        store.systemTime = Long.parseLong(args[0]) * 1000;
//        store.numSeats = Integer.parseInt(args[1]) * 2;
//        store.numRooms = Integer.parseInt(args[1]);
//        store.numCustomers = store.numSeats + store.numRooms;


        store.seatController = new Semaphore(store.numSeats);
        store.roomController = new Semaphore(store.numRooms);


        Scanner sc = new Scanner(System.in);

        while(true){
            String userRequest = sc.nextLine();
            if (userRequest.equalsIgnoreCase("exit")) {
                break;
            }
        }

        store.close();
        sc.close();
    }



    public void getRoom(int customerID) throws InterruptedException {
        roomController.acquire();
        freeSeat();
        System.out.println("\t\tCustomer #" + customerID + " enters the changing room. ");

    }

    public void freeRoom(int customerID) throws InterruptedException {
        roomController.release();
        System.out.println("\t\t\tCustomer #" + customerID + " leaves the changing room.");

    }

    public void getSeat(int customerID) throws InterruptedException {

        if (seatController.tryAcquire()) {
            System.out.println("\tCustomer #" + customerID + " enters the waiting area and has a seat.");

        } else {
            System.out.println("\tCustomer #" + customerID + " could not find a seat and leaves in frustration.");

        }


    }

    public void freeSeat() {
        seatController.release();
    }

    class Customer extends Thread {
        int customerID;

        public Customer(int customerID) {
            this.customerID = customerID;
        }

        @Override
        public void run() {
            System.out.println("Customer #" + customerID + " enters the system");
            try {

                store.getSeat(customerID);
                store.getRoom(customerID);
                Thread.sleep(new Random().nextInt(1000));
                store.freeRoom(customerID);


            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }

    }

}
