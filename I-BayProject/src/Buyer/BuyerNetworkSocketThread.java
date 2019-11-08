package Buyer;

import java.io.*;
import Utilities.*;
import Item.*;

/**
 * La classe BuyerNetworkSocketThread implementa un thread per la gestione della comunicazione con il seller, attraverso
 * lo stream di lettura sulla socket, e relativa stampa a video. In particolare, riceve messaggi di:
 * - offerta registrata da un thread seller (e quindi può essere del buyer proprietario di questo thread o di un altro buyer)
 * - timeout (quindi conto alla rovescia per chiusura dell'asta) - N.B.: questi sono gli unici messaggi non inviati dai
 * SellerThread, bensì da un timer
 * - chiusura dell'asta ( e consenguente vittoria o perdita dell'asta)
 */

public class BuyerNetworkSocketThread extends Thread
{
	String buyerName; // identità del buyer
	ItemID itemID; // oggetto da comprare
	private ObjectInputStream inFromSeller;
	
	public BuyerNetworkSocketThread( String n, ItemID i, ObjectInputStream in ) {
		buyerName = n;
		itemID = i;
		inFromSeller = in;
	}
	
	public void run() {
		Message msg;
		while( true ) {
			msg = Utilities.read( inFromSeller );
			/* Esci dal ciclo a seguito della chisura della socket dal SellerServer */
			if( msg == null || inFromSeller == null ) return;
			/* Gestione di un messaggio di offerta convalidata */
			else if( msg.getCommand() == Message.UPDATEOFFERT ) {
				if( msg.getName().equals( buyerName ) )
					System.out.println( "Hai fatto un'offerta di " + msg.getOffert() + " euro!" );
				else System.out.println( msg.getName() + " offre " + msg.getOffert() + " euro!" );
			}
			/* Gestione di un messaggio di conto alla rovescia */
			else if( msg.getCommand() == Message.TIMEOUT ) {
				int countdown = 0;
				switch( msg.getTime() ) {
					case 10 : countdown = 1; break;
					case 5 : countdown = 2; break;
					case 0 : countdown = 3; break;
				}
				System.out.println( msg.getOffert() + " euro e " + countdown + "!" );
			}
			/* Gestione di un messaggio di chiusura dell'asta */
			else if( msg.getCommand() == Message.WINNER ) {
				if( msg.getName().equals( buyerName ) )
					System.out.println( "Ti sei aggiudicato l'oggetto " + itemID.getDescription() + " per " + msg.getOffert() + " euro." );
				else if( msg.getName().equals( "BOH" ) ) {
					System.out.println( "L'asta si è conclusa senza un compratore!" );
				}
				else {
					System.out.println( "Peccato, non hai vinto l'asta." );
					System.out.println( msg.getName() + " si aggiudica l'oggetto: " + itemID.getDescription() + " per " + msg.getOffert() + " euro." );
				}
			}
		}
	}
}