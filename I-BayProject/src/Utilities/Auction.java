package Utilities;

import Item.*;
import Seller.*;

/**
 * Classe che implementa l'asta corrente: serve tipicamente a registrare cose come il tempo rimanente, l'ultima offerta e
 * xhi l'ha fatta...
 */

public class Auction
{
	private boolean terminated;
	private SellerID seller;
	private ItemID item;
	private String winner;
	private int readyBuyers;
	private Timer myTimer;
	
	public Auction( SellerID s, ItemID i ){
		terminated = false;
		seller = s;
		item = i;
		winner = "BOH";
		readyBuyers = 0;
		myTimer = null;
	}
	
	public boolean getTerminated() {
		return terminated;
	}
	
	public SellerID getSeller() {
		return seller;
	}
	
	public ItemID getItem() {
		return item;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public int getReady() {
		return readyBuyers;
	}
	
	public Timer getTimer() {
		return myTimer;
	}
	
	/* Usato dal timer per terminare l'asta allo scadere del timer */
	public synchronized void setFinished(){
		terminated = true;
	}
	
	/* Registra l'identità dell'ultima offerta validata */
	public synchronized void setWinner( String w )  {
		winner = w;
	}
	
	/* Utilizzato per mettere l'istanza di un timer dentro l'asta, ma contemporaneamente mi serve che l'oggetto timer
	 * mi dica quando l'asta è finita con setFinished() */
	public void setTimer( Timer t ) {
		myTimer = t;
	}
	
	/* Metodo che, a seguito dell'acquisizione del LOCK, registra l'ultima offerta effettuata, chi l'ha fatta e fa restart del
	 * timer nell'asta a seguito di un'offerta da un buyer, invocato dai SellerThread */
	public synchronized boolean newOffert( double offert, String t) {
		/* Devo essere sicuro che l'asta non sia ancora terminata, quindi terminated non è a true e ho ancora tempo e l'offerta
		 * sia valida */
		if( ( !terminated ) && ( myTimer.getTime() > 0 ) && ( offert > item.getMinimumPrice() ) ) {
			/* Viene fatto il controllo se l'offerta fatta è maggiore dell'ultimo prezzo d'asta registrato */
			if( item.changeMinimumPrice( offert ) ) {
				setWinner( t );
				myTimer.restart();
				return true;
			}
		}
		return false;
	}
	
	/* Wait personalizzata, usata dal SellerServer per aspettare su currentAuction */
	public synchronized void myWait() throws InterruptedException {
		wait();
	}
	
	/* Notify personalizzata */
	public synchronized void myNotify() throws InterruptedException {
		notify();
	}
	
	/* Metodo invocato dai SellerThread per svegliare il SellerServer, che sta in wait in attesa che si raggiunga il numero di
	 * buyers prefisso */
	public synchronized void wakeUpSeller() {
		readyBuyers++;
		if( readyBuyers == Utilities.MAX_BUYER )
			try {
				myNotify();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}