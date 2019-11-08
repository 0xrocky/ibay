package Seller;

import Utilities.*;

/**
 * Thread avviato dal SellerServer per consumare i messaggi che vengono prodotti e messi in coda dai SellerThread e dal Timer. 
 */

public class SellerPrinter extends Thread
{
	private Queue mexForSeller;
	
	public SellerPrinter( Queue q ) {
		mexForSeller = q;
	}
	
	public void run() {
		while( true ) {
			/* Preleva il prossimo messaggio dalla coda condivisa...*/
			Message msg = mexForSeller.take();
			switch( msg.getCommand() ) {
			/*... è un messaggio di un nuovo compratore? */
				case Message.NEWBUYER : System.out.println( "Nuovo acquirente: " + msg.getName() ); break;
				/*... è un messaggio di un'offerta validata con successo? */
				case Message.UPDATEOFFERT : System.out.println( msg.getName() + " offre " + msg.getOffert() + " euro!" ); break;
				/*... è un messaggio di conto alla rovescia dal timer? */
				case Message.TIMEOUT :
					int countdown = 0;
					switch( msg.getTime() ) {
						case 10 : 
							System.out.println( "Niente offerte per 20 secondi" );
							countdown = 1;
							System.out.println( msg.getOffert() + " euro e " + countdown + "!" );
							System.out.print( "Attendo 5 secondi..." );
							break;
						case 5 :
							countdown = 2;
							System.out.println( msg.getOffert() + " euro e " + countdown + "!" );
							System.out.print( "Attendo 5 secondi..." );
							break;
						case 0 :
							countdown = 3;
							System.out.println( msg.getOffert() + " euro e " + countdown + "!" );
							break;
					}
			}
		}	
	}
}
