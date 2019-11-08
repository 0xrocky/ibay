package Utilities;

import java.io.*;
import java.util.*;

/**
 * Classe che implementa un oggetto timer, che appartiene all'istanza della classe Auction.
 * Ha a sua volta un campo currentAuction, perché è responsabile di settare un flag che decreta la fine dell'asta allo scadere
 * del timer.
 * La classe Timer, inoltre, genera messaggi di timeout per tutti: buyers e printerThread (mettendoli nella coda mexForSeller) 
 */

public class Timer extends Thread
{
	private int time;
	private Auction currentAuction;
	ArrayList< ObjectOutputStream > buyerConnections;
	private Queue mexForSeller;
	
	public Timer( Auction a, ArrayList< ObjectOutputStream > obj, Queue q ) {
		time = 30;
		currentAuction = a;
		buyerConnections = obj;
		mexForSeller = q;
	}

	/* Metodo utilizzato seguito di un'offerta, quando devo far ripartire il timer */
	public void restart(){
		time = 30;
	}
	
	public int getTime() {
		return time;
	}
	
	/* Versione personalizzata di wait, utilizzata dal server per aspettare che il timer vada a 0 */
	public synchronized void myWait() throws InterruptedException {
		wait();
	}
	
	/* Versione personalizzata di notify, utilizzata per svegliare il server */
	public synchronized void myNotify() throws InterruptedException {
		notify();
	}
	
	public void run() {
		while( time != 0 ) {
			/* Decrementa la variabile time ad ogni secondo, con sleep */
			time--;
			try {
				Thread.sleep( 1000 );
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/* Se la variabile time è minore di 10, genera 3 messaggi...*/
			if( time <= 10 ) {
				/*...Quando il timer è a 10, a 5 e a 0 */
				if( time == 10 || time == 5 || time == 0 ) {
					/* Manda un messaggio di timeout ai vari buyers */
					for( ObjectOutputStream outToAllClient : buyerConnections )
						Utilities.write( outToAllClient, Message.timeout( getTime(), currentAuction.getItem().getMinimumPrice() ) );
					mexForSeller.put( Message.timeout( getTime(), currentAuction.getItem().getMinimumPrice() ) );
					// System.out.println( currentAuction.getItem().getMinimumPrice() + " euro e " + time );
					if( time == 0 ) {
						/* l'oggetto timer deve dire quando time è a 0, cioè quando l'asta è finita con setFinished() */
						currentAuction.setFinished();
						/* Sveglia il server, perché il timer è finito, quindi l'asta deve essere conclusa */
						try {
							myNotify();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}