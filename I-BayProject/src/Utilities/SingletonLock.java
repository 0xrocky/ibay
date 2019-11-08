package Utilities;

/**
 * Classe che implementa il pattern Singleton, per costruire un unico lock nell'applicazione distribuita, condiviso da tutti i
 * thread.
 * Il lock viene acquisito dai SellerThread, a seguito di un messaggio di offerta dal BuyerUserInputThread, per registrare
 * atomicamente un'offerta e l'identità di chi l'ha fatta.
 */

public class SingletonLock
{	
	  private static SingletonLock singl = null;
	  
	  public synchronized static SingletonLock getInstance() {
	    if( singl == null )
	      singl = new SingletonLock();
	    return singl;
	 }
	  //Il costruttore private impedisce l’istanza di oggetti da parte di classi esterne
	  private SingletonLock() {}
}