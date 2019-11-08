package AuctionHouse;

import java.rmi.*;
import java.net.MalformedURLException;

/**
 * L'esecuzione di questa classe richiede che venga definito un 
 * "codebase", ovvero una variabile utilizzata dal server RMI
 * per localizzare le classi utilizzate dall'oggetto remoto es:
 * java -D java.rmi.server.codebase=file:/Users/dario/RMI/RMIMain
 */

public class RunAuctionHouse
{
	public static void main( String[] args ) throws Exception {
		/* Definiamo il nome canonico che verrà utilizzato dal registry 
		 * per far riferimento all'oggetto che stiamo per rendere
		 * disponibile. Il client dovrà usare questo nome per ottenere
		 * il riferimento all'oggetto remoto dal registry */
		String ObjectName = "//localhost/auctionHouse";
		try {
			java.rmi.registry.LocateRegistry.createRegistry( 1099 );
			AuctionHouseIf obj = new AuctionHouse();
			/**
			 * Invoca il metodo rebind della classe Naming per associare il
			 * servizio dell’oggetto remoto AuctionHouse al registry RMI
			 * con il nome specificato.
			 */
			Naming.rebind( ObjectName, obj );
		}
		catch( RemoteException ex ) { ex.printStackTrace(); }
		catch( MalformedURLException ex ) { ex.printStackTrace(); }
	}	
}