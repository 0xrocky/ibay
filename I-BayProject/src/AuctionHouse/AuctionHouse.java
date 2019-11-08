package AuctionHouse;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

import Buyer.*;
import Item.*;
import Seller.*;
import Utilities.*;

/**
 * La classe AuctionHouse è l'implementazione dell'oggetto remoto.
 * Estende la classe UnicastRemoteObject per ottenere le funzionalità
 * di base necessarie per tutti gli oggetti remoti. Inoltre implementa l’interfaccia AuctionHouseIf.
 */

public class AuctionHouse extends UnicastRemoteObject implements AuctionHouseIf
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList< ItemID > itemList = null;
	private int incrementalItemID;
	private Hashtable< ItemID, SellerID > sellerList = null;
	private Hashtable< ItemID, ArrayList< BuyerID > > buyerList = null;
	
	/* Costruisce l'oggetto AuctionHouse */
	public AuctionHouse() throws RemoteException {
		super(); // invoca il costruttore standard della classe UnicastRemoteObject
		itemList = new ArrayList< ItemID >();
		incrementalItemID = 1; // ID contatore degli oggetti all'asta - può essere usato anche come ID dei seller
		sellerList = new Hashtable< ItemID, SellerID >();
		buyerList = new Hashtable< ItemID, ArrayList< BuyerID > >();
	}
	
	/* Il metodo getOpenAuctions restituisce l'elenco di tutti gli oggetti in vendita la cui asta non è ancora iniziata
	 *  o null se non ci sono oggetti disponibili */
	public ItemID[] getOpenAuctions() {
		int cont = itemList.size();
		if( cont > 0 ) { //la lista non è vuota: ci sono oggetti all'asta, ma magari è già iniziata
			ArrayList< ItemID > copy = new ArrayList< ItemID >();
			for( int i = 0; i < cont; i++ ) {
				ItemID temp = itemList.get( i );
				if( buyerList.get(temp).size() < Utilities.MAX_BUYER )
					copy.add( temp );
			}
			//ItemdID[] itemArray = new ItemdID[ copy.size() ];
			//itemArray = copy.toArray( itemArray );
			//return itemArray;
			int length = copy.size(); //solo se ho oggetti ad asta non ancora avviata
			if( length > 0 ) return copy.toArray( new ItemID[ length ] );
		}
		return null;
	}
	
	/* Il metodo joinAuction permette ad un acquirente di partecipare all'asta di un oggetto. I parametri del metodo sono:
	 * - l'ID che identifica l'oggetto
	 * - il nome dell'acquirente e la relativa porta sulla quale rimane in attesa di connessioni
	 * L'indirizzo IP viene ottenuto automaticamente dall'ActionHouse. Il metodo restituisce null a seguito di errori,
	 * come asta già iniziata */
	public synchronized SellerID joinAuction( int itemID, String buyerName, int port ) {
		ItemID temp = null;
		ArrayList<BuyerID> buyerOfThisItem = null;
		for( int i = 0; i < itemList.size(); i++ ) {
			temp = itemList.get( i );
			if( temp.getID() == itemID ) {
				buyerOfThisItem = buyerList.get( temp );
				if( buyerOfThisItem.size() < Utilities.MAX_BUYER ) { // asta non ancora iniziata per l'oggetto temp
					buyerOfThisItem.add( new BuyerID( buyerName, Utilities.RMIIPresolution(), port ) );
					return sellerList.get(temp);
				}
			}
		}
		return null; // asta iniziata o item con ID non esistente
	}
	
	/* Il metodo AddItemToSell viene invocato dal venditore per mettere in vendita un oggetto. I parametri del metodo sono:
	 * - una descrizione dell'oggetto da vendere
	 * - il nome del venditore e la relativa porta sulla quale rimane in attesa di connessioni
	 * - la base d'asta
	 * Compito dell'AuctionHouse è assegnare un ID univoco ad ogni oggetto in vendita */
	public boolean addItemToSell( String description, String sellerName, int port, double minimumPrice ) {
		ItemID newItem = new ItemID( incrementalItemID, description, minimumPrice );
		if ( itemList.add( newItem ) ) {
			incrementalItemID++;
			//associa univocamente oggetto a venditore
			sellerList.put( newItem, new SellerID( sellerName, Utilities.RMIIPresolution(), port ) );
			ArrayList<BuyerID> buyerOfThisItem = new ArrayList<BuyerID>();
			buyerList.put( newItem, buyerOfThisItem ); //crea la lista vuota di compratori per questo oggetto
			return true;
		}
		return false;
	}
	
	/* Metodi di servizio */
	public ItemID returnItem( SellerID seller ) {
		ItemID itemID = null;
		SellerID temp = null;
		for( int i = 0; i < itemList.size(); i++ ) {
			itemID = itemList.get( i );
			temp = sellerList.get( itemID ); 
			if( seller.equals(temp) )
					break;
		}
		return itemID;
	}
	
}