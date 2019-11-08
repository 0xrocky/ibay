package Buyer;

import java.io.*;
import java.net.*;
import java.rmi.*;
import AuctionHouse.*;
import Item.*;
import Seller.*;
import Utilities.*;

/**
 * Thread che identifica un buyer. Viene eseguito ogni volta che viene lanciato BuyerMain. Si preoccupa di:
 * - gestire l'identità dell'acquirente, acquisendo l'input
 * - far scegliere l'articolo in vendita comunicando via RMI con la AuctionHouse
 * - instaurare una comunicazione con il seller ( che verrà mantenuta da un thread del seller specifico )
 * - eseguire due thread diversi, uno per la gestione delle offerte dell'utente da tastiera, e un secondo per gestire la
 * 	comunicazione con il thread del seller
 */

public class BuyerThread extends Thread
{
	public BuyerThread() {} 
	public void run() {
		String identity, host, name, temp;
		int port, portRMI, chosen = 0;
		InetAddress IP = Utilities.LocalIPresolution();
		SellerID sellerOfChosen = null; // il venditore dell'articolo con ID = 'chosen'
		ItemID[] itemList = null; // array che inizializzerà l'AuctionHouse
		ItemID chosenItem = null; // articolo con ID = 'chosen'
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
		
		System.out.println( "Quale nome vuoi usare?" );
		try {
			temp = inFromUser.readLine();
			if( temp.isEmpty() ) { System.out.println( "Devi inserire un nome." ); return; }
			else identity = temp;
			System.out.println( "Quale porta vuoi usare?" );
			temp = inFromUser.readLine();
			port = Utilities.checkIfNumber(temp) ? Integer.parseInt( temp ) : 0;
			if( port < 1025 || port > 65535 ) { System.out.println( "Hai scelto una porta non valida" ); return; }
			BuyerID buyer = new BuyerID( identity, IP, port );
			System.out.println( "A quale server (RMI) ti vuoi connettere (lascia vuoto per usare localhost)?" );
			temp = inFromUser.readLine();
			host = temp.isEmpty()? "localhost" : temp;
			System.out.println( "A quale porta del server (RMI) ti vuoi connettere (lascia vuoto per usare 1099)?" );
			temp = inFromUser.readLine();
			if( !( temp.isEmpty() ) && !( Utilities.checkIfNumber( temp ) ) )
				{ System.out.println( "La porta non è un numero" ); return; }
			else portRMI = temp.isEmpty()? 1099 : Integer.parseInt( temp );
			// Recupero oggetto remoto per accedere ai metodi pubblicati nell'interfaccia
			name = "//" + host + "/auctionHouse";
			Object obj = Naming.lookup(name);
			AuctionHouseIf house = (AuctionHouseIf) obj;
			
			System.out.print( "\nOttengo lista di oggetti dal server..." );
			itemList = house.getOpenAuctions();
			System.out.println( "fatto!" );
			if( itemList == null )
				System.out.println( "Lista di oggetti all'asta vuota!" );
			else {
				System.out.println( "Seleziona l'oggetto che ti interessa:" );
				for( int i = 0; i < itemList.length; i++ )
					System.out.println( itemList[ i ].toString() );
				temp = inFromUser.readLine();
				if( Utilities.checkIfNumber( temp ) )
					chosen = Integer.parseInt( temp );
				else { System.out.println( "l'oggetto non esiste!" ); return; }
				if( chosen < 1 || chosen > itemList.length )
					{ System.out.println( "l'oggetto non esiste!" ); return; }
				else {
					sellerOfChosen = house.joinAuction( chosen, identity, port );
					if( sellerOfChosen == null ) System.out.println( "Asta iniziata od oggetto inesistente!" );
					else {
						chosenItem = house.returnItem( sellerOfChosen );
						System.out.println( "Hai scelto " + chosenItem.toString() );
						Socket clientSocket = new Socket( sellerOfChosen.getIPAddress(), sellerOfChosen.getPort() );
						ObjectOutputStream outToSeller = new ObjectOutputStream( clientSocket.getOutputStream() );
						ObjectInputStream inFromSeller = new ObjectInputStream( clientSocket.getInputStream() );
						Message msg;
						/* invio un messaggio di SYN al Seller */
						Utilities.write( outToSeller, Message.syn( identity ) );
						/* attendo messaggio di risposta SYNACK dal seller per sapere il suo nome */
						msg = Utilities.read( inFromSeller );
						if( msg.getCommand() == Message.SYNACK ) {
							System.out.println( "Venditore: " + msg.getName() );
							/* attendo lista di probabili acquirenti con un messaggio ATTEND */
							msg = Utilities.read( inFromSeller );
							if( msg.getCommand() == Message.ATTEND ) {
								System.out.print( "Acquirenti iscritti: " );
								if( msg.getBuyers().size() == 0 ) System.out.println( "Sei il primo acquirente!" );
								else System.out.println( msg.getBuyers() );
								if( msg.getBuyers().size() < Utilities.MAX_BUYER-1 )
									System.out.println( "In attesa di altri acquirenti..." );
							}
						}
						/* a questo punto mi arrivano solo messaggi di nuovi acquirenti NEWBUYER o di avvio dell'asta START,
						stampo a video il nome dei nuovi compratori partecipanti all'asta e termino quando
						il seller invia un messaggio di avvio dell'asta */
					    while( true ) {
					    	msg = Utilities.read( inFromSeller );
					    	if( msg.getCommand() == Message.START ) break;
					    	else if( msg.getCommand() == Message.NEWBUYER )
						    		System.out.println( "Nuovo acquirente: " + msg.getName() );
						    else System.out.println("Errore nella ricezione di un NEWBUYER");
					    }
					    System.out.println("Via dell'asta!");
					    /* Qui l'asta è iniziata! Avvio un thread per l'input utente che gestisca le offerte */
					    BuyerUserInputThread buyerUserInput = new BuyerUserInputThread( identity, chosenItem, inFromUser, outToSeller );
					    /* Avvio un thread per la comunicazione con seller */					    
					    BuyerNetworkSocketThread buyerNetworkSocket = new BuyerNetworkSocketThread( identity, chosenItem, inFromSeller );
					    buyerUserInput.start();
					    buyerNetworkSocket.start();
					    if( inFromSeller == null ) clientSocket.close();
					}
				}
			}
		}
		catch( EOFException o ) {
			System.err.println( "FINE" );
			System.exit( 0 );
		}catch (IOException e1) {
			System.err.println( "Errore in fase di lettura di input " + e1.getMessage() );
			e1.printStackTrace();
		} catch( Exception e ) {
			System.err.println( "RMI eccezione: " + e.getMessage() );
			e.printStackTrace();
		}
	}
}