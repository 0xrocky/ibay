package Buyer;

/**
 * Applicazione Buyer: si occupa di interagire con la AuctionHouse via RMI e con i Seller via Socket per:
 * - ricevere la lista di oggetti all'asta
 * - scegliere un oggetto desiderato
 * - contattarne il venditore e gestire la contrattazione
 */

public class BuyerMain
{
	public static void main( String[] args ) throws Exception {
		BuyerThread buyer = new BuyerThread();
		buyer.start();
	}
}