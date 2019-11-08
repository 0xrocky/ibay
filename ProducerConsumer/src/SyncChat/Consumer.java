package SyncChat;

public class Consumer implements Runnable
{
	private final String id;
	private final Queue queue;
	
	public Consumer( String id, Queue queue ) {
		this.id = id;
		this.queue = queue;
	}
	
	public void consume( String msg ) {
		System.out.println("Cons. " + id + " prelevo " + msg);
	}
	
	public void run() {
		while( true ) {
			consume( queue.take() );
		}
	}
	
}
