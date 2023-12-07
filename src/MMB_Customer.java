package FINAL;

import java.util.Random;

public class MMB_Customer extends Thread {
	
	private int customerNum;
	private int ipaddress;
	private int serverNum;
	
	// number of customers waiting on a seat
	private int customersWaiting;
	
	// seatNumber
	private int seatNum;
	
	// number of customers waiting
	private int numCustomersWaiting;
	
	// number of customers changing
	private int numCustomersChanging;
	
	// number of 

	Random rand = new Random();
	
	public MMB_Customer(int customerNum, int ipaddress, int serverNum) {
		this.customerNum = customerNum;
		this.ipaddress = ipaddress;
		this.serverNum = serverNum;
		
		// customer thread is created
		System.out.println("Customer " + customerNum + ": enters the system");
	}
	
	public void run() {
		
		try {
			Thread.sleep(rand.nextInt(1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	// customer enters the waiting area
	public void enterWaitingArea() {
		System.out.println("\tCustomer " + customerNum + ": enters the waiting area on <Server " + serverNum + ": " + ipaddress + "> and has a seat.");
	}
	
	// customer leaves the waiting area
	public void leavesWaitingArea() {
		System.out.println("\tCustomer " + customerNum + ": leaving waitining area  " + ipaddress);
	}
	
	// number of customers waiting on a seat
	public void customersWaitingOnSeat() {
		System.out.println("\tWe have " + customersWaiting + " waiting on <Server " + serverNum + ": " + ipaddress + "> seat " + seatNum);
	}
	
	// customer enters a fitting room
	public void enterFittingRoom() {
		System.out.println("\tCustomer #" + customerNum + " enters fitting room located at <Server " + serverNum + ": " + ipaddress + ">");
	}
	
	// current num of customers in fitting room and num waiting
	public void roomStatus() {
		System.out.println("\tWe have " + numCustomersChanging + " changing and " + numCustomersWaiting + " waiting");
	}
	
	// customer exits the fitting room
	public void exitFittingRoom() {
		System.out.println("\t\tCustomer #" + customerNum + "leaves fitting room");
	}

	
	/*Each customer thread should print messages indicating when they enter the system,
enter/leave the waiting area, enter a fitting room, and leave the fitting room. 
Furthermore, these messages should incorporate details regarding the fitting room’s 
location, specifically indicating its IP address.*/
	
	/* In the context of a multi-threaded and socket-based implementation, each customer
thread should communicate with the server using sockets.*/
	
	public int getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(int customerNum) {
		this.customerNum = customerNum;
	}

	public int getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(int ipaddress) {
		this.ipaddress = ipaddress;
	}

	public int getServerNum() {
		return serverNum;
	}

	public void setServerNum(int serverNum) {
		this.serverNum = serverNum;
	}
}
