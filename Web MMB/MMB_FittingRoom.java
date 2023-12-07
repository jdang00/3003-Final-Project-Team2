package FINAL;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class MMB_FittingRoom {
    private int time;
    private int numFittingRooms;
    private int numChairs;
    private int numCustomers;
	private Semaphore seatSem;
	private Semaphore roomSem;
	private int[] customersInFittingRoom;
    private ReentrantLock lock = new ReentrantLock();
    private MMB_Customer c;

    public MMB_FittingRoom(int time, int numFittingRooms, int numChairs, int numCustomers) {
    	this.time = time;
    	this.numFittingRooms = numFittingRooms;
    	this.numChairs = numChairs;
    	this.numCustomers = numCustomers;
        this.seatSem = new Semaphore(numChairs, true); 
        this.roomSem = new Semaphore(numFittingRooms, true); 
    }
    
    public void enterFittingRoom(int customerID) throws InterruptedException {
        seatSem.acquire();
        roomSem.acquire();

        lock.lock();
        try {
            for (int i = 0; i < numFittingRooms; i++) {
                if (customersInFittingRoom[i] == 0) {
                    customersInFittingRoom[i] = customerID;
                    System.out.println("Customer #" + customerID + " enters fitting room #" + (i + 1));
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void exitFittingRoom(int customerID) {
        lock.lock();
        try {
            for (int i = 0; i < numFittingRooms; i++) {
                if (customersInFittingRoom[i] == customerID) {
                    customersInFittingRoom[i] = 0;
                    System.out.println("Customer #" + customerID + " leaves fitting room #" + (i + 1));
                    break;
                }
            }
        } finally {
            lock.unlock();
        }

        seatSem.release();
        roomSem.release();
    }


	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getNumFittingRooms() {
		return numFittingRooms;
	}

	public void setNumFittingRooms(int numFittingRooms) {
		this.numFittingRooms = numFittingRooms;
	}

	public int getNumChairs() {
		return numChairs;
	}

	public void setNumChairs(int numChairs) {
		this.numChairs = numChairs;
	}

	public int getNumCustomers() {
		return numCustomers;
	}

	public void setNumCustomers(int numCustomers) {
		this.numCustomers = numCustomers;
	}

	public Semaphore getSeatSem() {
		return seatSem;
	}

	public void setSeatSem(Semaphore seatSem) {
		this.seatSem = seatSem;
	}

	public Semaphore getRoomSem() {
		return roomSem;
	}

	public void setRoomSem(Semaphore roomSem) {
		this.roomSem = roomSem;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public void setLock(ReentrantLock lock) {
		this.lock = lock;
	}

	public int[] getCustomersInFittingRoom() {
		return customersInFittingRoom;
	}

	public void setCustomersInFittingRoom(int[] customersInFittingRoom) {
		this.customersInFittingRoom = customersInFittingRoom;
	}

	public MMB_Customer getC() {
		return c;
	}

	public void setC(MMB_Customer c) {
		this.c = c;
	}

	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.out.println("Invalid Arguments:  java FittingRoom [Time(in seconds)]  [Number of Fitting Rooms]");
			System.exit(100);
		}

		// time - in seconds - that customers are allowed to arrive in the waiting area for
		int time = Integer.parseInt(args[0]);
		// number of fitting rooms
		int numFittingRooms = Integer.parseInt(args[1]);
		// number of chairs in waiting area
		int numChairs = numFittingRooms * 2;
		// number of customers
		int numCustomers = numChairs + numFittingRooms;
		
		
		
		// Output
		System.out.println("Using arguments from command line");
		System.out.println("Sleep time = " + time);
		System.out.println("Fitting Rooms = " + numFittingRooms);
		System.out.println("Number of chairs in the waiting area = " + numChairs);
		System.out.println("Number of customers = " + numCustomers + "\n");
		


	}

}
