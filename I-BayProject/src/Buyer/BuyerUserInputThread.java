package Buyer;

import java.io.*;
import Item.*;
import Utilities.*;

/**
 * La classe BuyerUserInputThread implementa un thread per la gestione della comunicazione con l'utente che fa offerte di rialzo,
 * attraverso uno stream BufferedReader, e poi spedisce queste offerte (se maggiori del prezzo minimo d'asta o l'ultima offerta
 * effettuata) sullo stream di scrittura sulla socket.
 */

public class BuyerUserInputThread extends Thread
{	
	String buyerName; // identità del buyer
	ItemID itemID; // numero dell'oggetto da comprare
	private BufferedReader inFromUser;
	private ObjectOutputStream outToSeller;
	
	public BuyerUserInputThread( String n, ItemID i, BufferedReader r, ObjectOutputStream o ) {
		buyerName = n;
		itemID = i;
		inFromUser = r;
		outToSeller = o;
	}
	
	public void run() {
		String temp = null;
		double offer = 0;
		while( true ) {
			try {
				temp = inFromUser.readLine();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			if( temp == null ) return;
			if( Utilities.checkIfOffer( temp ) )
				offer = Double.parseDouble( temp );
			if( offer <= itemID.getMinimumPrice() )
				System.out.println( "Devi inserire un'offerta maggiore del prezzo minimo!" );
			// Genera un messaggio di offerta per il SellerThread
			else {
				Utilities.write( outToSeller, Message.offert( buyerName, itemID, offer ) );
			}
		}
	}
}