package SyncChat;

import java.io.BufferedReader;
import java.io.*;

public class Producer implements Runnable
{
	private final String id;
	private final Queue queue;
	private int counter = 0;
	private BufferedReader inFromUser;
	
	public Producer( String id, Queue queue ) {
		this.id = id;
		this.queue = queue;
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public synchronized String produce() {
		counter++;	
		try {
			return inFromUser.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}
	
	public void run() {
		while( true ) {
			String msg = produce();
			System.out.println( "Prod. " + id + " inserisco " + msg );
			queue.put( msg );
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}