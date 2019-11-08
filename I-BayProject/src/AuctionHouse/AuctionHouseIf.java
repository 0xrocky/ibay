package AuctionHouse;

import Item.ItemID;
import Seller.SellerID;

/** 
 * Definizione dell'interfaccia remota
 */

public interface AuctionHouseIf extends java.rmi.Remote
{
	/* Il metodo getOpenAuctions restituisce l'elenco di tutti gli oggetti in vendita la cui asta non è ancora iniziata	*/
	public ItemID[] getOpenAuctions() throws java.rmi.RemoteException;
	
	/* Il metodo joinAuction permette ad un acquirente di partecipare all'asta di un oggetto */
	public SellerID joinAuction( int itemID, String BuyerName, int port ) throws java.rmi.RemoteException;
	
	/* Il metodo AddItemToSell viene invocato dal venditore per mettere in vendita un oggetto */
	public boolean addItemToSell( String description, String sellerName, int port, double minimumPrice ) throws java.rmi.RemoteException;
	
	public ItemID returnItem( SellerID seller ) throws java.rmi.RemoteException;
}