package Seller;

import java.io.*;
import java.util.*;
import Utilities.*;
import Utilities.Queue;
import java.net.*;

/**
 * SellerThread avviato dal SellerServer per ogni connessione che riceve da un buyer.
 * È responsabile di:
 * - instaurare una connessione con il buyer, ricevendo la sua identità e dandogli quella del seller
 * - dire al buyer quali sono gli acquirenti prima di lui
 * - dire a tutti che c'è un nuovo buyer, e aggiungere la connessione del buyer alla lista degli acquirenti partecipanti all'asta
 * - ricevere offerte dai buyer.
 * Quando riceve un offerta è il SellerThread che acquisice il LOCK, secondo il pattern Singleton, e registrare l'offerta.
 * Se questa ha esito positivo, lo comunica a tutti.
 */

public class SellerThread extends Thread
{
	SellerID seller = null; // identificativo del venditore
	private Socket connectionSocket = null;
	private ObjectInputStream inFromClient = null;
	private ObjectOutputStream outToClient = null;
	private ArrayList< String > buyerList; // lista dei buyers
	private ArrayList< ObjectOutputStream > buyerConnections; // lista degli stream dei buyers sulla socket
	private Auction currentAuction; // oggetto asta corrente
	private Queue mexForSeller; // coda ogni thread memorizza messaggi per il Seller da stampare a video
	
	public SellerThread( Socket s, SellerID sell, ArrayList< String > t, ArrayList< ObjectOutputStream > obj, Auction a, Queue q ) {
		connectionSocket = s;
		seller = sell;
		try {
			/* Inizializza lo stream di output verso la socket */
			outToClient = new ObjectOutputStream( connectionSocket.getOutputStream() );
			/* Inizializza lo stream di input dalla socket */
			inFromClient = new ObjectInputStream( connectionSocket.getInputStream() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		buyerList = t;
		buyerConnections = obj;
		currentAuction = a;
		mexForSeller = q;
	}
	
	public void run() {
		Message msg;
		String buyer = null;
		try {
			/* ####################### ASTA NON INIZIATA: FASE DI CONNESSIONE CON IL BUYER ################################# */
			/* Leggo un messaggio SYN dal client */
			msg = Utilities.read( inFromClient );
			if( msg.getCommand() == Message.SYN ) {
				buyer = msg.getName();
				// System.out.println("Nuovo acquirente: "+ buyer );
				Utilities.write( outToClient, Message.synAck( seller.getName() ) ); // invio messaggio di SYNACK
				Utilities.write( outToClient, Message.attend( buyerList ) ); // invio messaggio di ATTEND con lista dei buyer
				buyerList.add( buyer );
			}
			/* Invio messaggio di NEWBUYER sugli stream di tutti i buyer già presenti: il nuovo buyer è proprio quello connesso
			 * con il corrente SellerThread */
			msg = Message.newBuyer( buyer );
			for( ObjectOutputStream outToAllClient : buyerConnections )
				Utilities.write( outToAllClient, msg );
			/* Metto il messaggio di NEWBUYER anche in coda per stamparlo su video del del SellerServer */
			mexForSeller.put( msg );
			/* Aggiungo alla lista degli stream dei buyer quello del buyer corrente */
			buyerConnections.add( outToClient );
			/* Sveglio il server, che è in wait su currentAuction: se sono l'ultimo SellerThread allora il SellerServer si
			 * sveglierà davvero e l'asta inizierà */
			currentAuction.wakeUpSeller();
			/* ########################À## ASTA INIZIATA: FASE DI GESTIONE OFFERTE DEL BUYER ################################# */
			/* A questo punto assumo che l'asta per i sellerThread sia già iniziata, perché il SellerServer avrà inviato ai buyers
			 * un messaggio di asta iniziata */
			while( true ) {
				/* Esci a seguito di chiusura della socket */
				if( msg == null || inFromClient == null ) break;
				/* Il thread è bloccato su un messaggio di offerta dal buyer */
				msg = Utilities.read( inFromClient );
				/* Se ricevo un messaggio di offerta... */
				if( msg.getCommand() == Message.OFFERT ) {
					/* Acquisisco LOCK: siccome c'è un unico lock nell'applicazione distribuita, secondo il pattern Singleton,
					 * facendo un blocco di operazioni su synchronized(LOCK), sono sicuro che quel blocco di operazioni
					 * saranno eseguite atomicamente */
					synchronized( SingletonLock.getInstance() ) {
						/* Setto l'offerta nell'asta */
						if( currentAuction.newOffert( msg.getOffert(), msg.getName() ) ) {
							msg = Message.updateOffert( msg.getName(), msg.getOffert() );
							/* invio messaggio di NEWBUYER sugli stream di tutti i buyer già presenti */
							for( ObjectOutputStream outToAllClient : buyerConnections )
								Utilities.write( outToAllClient, msg );
							mexForSeller.put( msg );
						}
					}
				}
			}
			//connectionSocket.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}