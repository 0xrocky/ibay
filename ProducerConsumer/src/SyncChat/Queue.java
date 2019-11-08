package SyncChat;

import java.util.ArrayList;

public class Queue
{
	public ArrayList< String > buffer = new ArrayList< String >();
	
	public synchronized void put( String msg ) {
		buffer.add( msg );
		/* Sveglio il produttore nel caso fosse in attesa di messaggi */
		notify();
	}
	
	public synchronized String take() {
		String msg = null;
		/* Evito polling mettendo il consumatore in wait se non ho messaggi nel buffer */
		while( buffer.size() == 0 ) {
			try { wait(); }
			catch( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		if( buffer.size() > 0 ){
			msg = buffer.get( 0 );
			buffer.remove( 0 );
		}
		return msg;
	}
}