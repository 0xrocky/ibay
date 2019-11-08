package Seller;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;

import Utilities.*;
import Utilities.Queue;
import Utilities.Timer;
import AuctionHouse.AuctionHouseIf;
import Item.ItemID;

/**
 * Applicazione Seller: si occupa di interagire con la AuctionHouse via RMI e con i Buyer via Socket per:
 * - ha il compito di aggiungere l’oggetto da vendere alla AuctionHouse
 * - deve dare inizio all'asta
 * - deve chiudere l'asta, tenendo conto del tempo
 * Il SellerMain è un pò il Seller che fa da Server e avvia i vari SellerThread quando a lui si connettono i buyer.
 */

public class SellerMain
{
	public static void main( String[] args ) throws Exception {
		String identity, host, name, temp, description;
		int port, portRMI, buyers = 0;
		InetAddress IP = Utilities.LocalIPresolution();
		double minPrice;
		boolean flag;
		ItemID item = null;
		// lista che contiene i nomi dei probabili acquirenti
		ArrayList< String > buyerList = new ArrayList< String >( Utilities.MAX_BUYER );
		/* Lista degli stream di scrittura sulla socket dei probabili acquirenti, usata per comunicare con tutti i buyer */
		ArrayList< ObjectOutputStream > buyerConnections = new ArrayList< ObjectOutputStream >();
		/* Stream di input da tastiera */
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
		/* Asta che sta per essere creata */
		Auction currentAuction = null;
		/* Timer del conto alla rovescia */
		Timer timer = null;
		/* Coda dedicata ai messaggi per la console video del SellerServer */
		Queue mexForSeller = new Queue();
		/* Thread che si occupa di printare i messaggi per il SellerServer dai vari SellerThread */
		SellerPrinter printer = new SellerPrinter( mexForSeller );
		
		System.out.println( "Quale nome vuoi usare?" );
		temp = inFromUser.readLine();
		if( temp.isEmpty() ) { System.out.println( "Devi inserire un nome." ); return; }
		else identity = temp;
		System.out.println( "Quale porta vuoi usare?" );
		temp = inFromUser.readLine();
		port = Utilities.checkIfNumber(temp) ? Integer.parseInt( temp ) : 0;
		if( port < 1025 || port > 65535 ) { System.out.println( "Hai scelto una porta non valida" ); return; }
		SellerID seller = new SellerID( identity, IP, port );
		System.out.println( "A quale server (RMI) ti vuoi connettere (lascia vuoto per usare localhost)?" );
		temp = inFromUser.readLine();
		host = temp.isEmpty()? "localhost" : temp;
		System.out.println( "A quale porta del server (RMI) ti vuoi connettere (lascia vuoto per usare 1099)?" );
		temp = inFromUser.readLine();
		if( !( temp.isEmpty() ) && !( Utilities.checkIfNumber( temp ) ) )
			{ System.out.println( "La porta non è un numero" ); return; }
		else portRMI = temp.isEmpty()? 1099 : Integer.parseInt( temp );
		
		System.out.println( "Inserisci la descrizione dell'oggetto:" );
		description = inFromUser.readLine();
		if( description.isEmpty() ) { System.out.println( "Inserisci la descrizione dell'oggetto!!!" ); return; }
		System.out.println( "Inserisci la base d'asta:" );
		temp = inFromUser.readLine();
		if( temp.isEmpty() || !( Utilities.checkIfOffer( temp ) ) ) { System.out.println( "Inserisci un prezzo!!!" ); return; }
		else minPrice = Double.parseDouble( temp );
		if( minPrice <= 0 ) { System.out.println( "Inserisci un prezzo positivo!!!" ); return;  }
		try {
			// Recupero oggetto remoto per accedere ai metodi pubblicati nell'interfaccia
			name = "//" + host + "/auctionHouse";
			Object obj = Naming.lookup(name);
			AuctionHouseIf house = (AuctionHouseIf) obj;
			
			flag = house.addItemToSell( description, identity, port, minPrice );
			if ( !flag ) 
				{ System.out.println("Errore addItemToSell"); return; }

			System.out.println( "\nAttendo acquirenti..." );
			/* Ottengo l'oggetto creato, creo l'asta, creo il timer per l'asta e setto il timer dentro l'asta */
			item = house.returnItem( seller );
			currentAuction = new Auction( seller, item );
			timer = new Timer( currentAuction, buyerConnections, mexForSeller );
			currentAuction.setTimer( timer );
			/* Avvio del thread printer per stampare a video i messaggi per il seller */
			printer.start();
			
			// conto gli acquirenti e do il via all'asta
			ServerSocket welcomeSocket = new ServerSocket( port );
			while( buyers < Utilities.MAX_BUYER ) {
				buyers++;
				/* La chiamata accept è bloccante. All'arrivo di una nuova connessione, crea una nuova "established socket" */
				Socket connectionSocket = welcomeSocket.accept();
				/* Creazione di un thread e passaggio della established socket */ 
				SellerThread thread = new SellerThread( connectionSocket, seller, buyerList, buyerConnections, currentAuction, mexForSeller );
				/* Avvio del thread */
				thread.start();
				// System.out.println( "Partito thread " + buyers );
			}
			/* Attendo che tutti i SellerThread siano pronti per fare partire l'asta, mettendo in wait il Seller sull'oggetto asta */
			// System.out.println( "Server va in wait" );
			currentAuction.myWait();
			/* #########################################À ASTA INIZIATA ################################################# */
			/* Qui tutti sono pronti! Faccio partire il timer e dico a tutti dell'avvio dell'asta */
			timer.start();
			for( ObjectOutputStream outToAllClient : buyerConnections )
				Utilities.write( outToAllClient, Message.start() );
			System.out.println( "Via dell'asta!" );
			/* Mi metto in attesa sul timer: qui il timer sarà a 0 quando mi risveglierà, quindi l'asta sarà finita */
			timer.myWait();
			/* #########################################À ASTA FINITA ################################################# */
			/* Mando a tutti un messaggio di WIN per sapere chi ha vinto e a quanto ha comprato l'articolo */
			for( ObjectOutputStream outToAllClient : buyerConnections )
				Utilities.write( outToAllClient, Message.winner( currentAuction.getWinner(), currentAuction.getItem().getMinimumPrice() ) );
			
			System.out.println( "Chiudo l'asta!" );
			/* Controllo se l'asta si è conclusa con un compratore o meno */
			if( !( currentAuction.getWinner().equals("BOH") )  ) {
				System.out.println( "Hai venduto il tuo oggetto per " + currentAuction.getItem().getMinimumPrice() + " euro!" );
				System.out.println( currentAuction.getWinner() + " si aggiudica l'oggetto: " + currentAuction.getItem().getDescription() );
			}
			else System.out.println( "NON Hai venduto il tuo oggetto. Forse dovresti abbassare il prezzo!" );
			welcomeSocket.close();
		}
		catch( EOFException o ) {
			System.err.println( "FINE" );
			System.exit( 0 );
		}
		catch( Exception e ) {
			System.err.println( "RMI exception: " + e.getMessage() );
			e.printStackTrace();
			System.exit( 0 );
	    }
	}
}