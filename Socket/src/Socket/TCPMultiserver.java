package Socket;

import java.io.*;
import java.net.*;

public class TCPMultiserver
{
	public static void main( String[] args ) throws Exception {

		/* Inizializza una "listening socket" sulla porta specificata */
		try {		      
			ServerSocket welcomeSocket = new ServerSocket( 6789 );
			while( true ) {
				/* 
				 * La chiamata accept è bloccante. All'arrivo di una nuova connessione, crea una nuova "established socket"
				 * */
				Socket connectionSocket = welcomeSocket.accept();
				
				/* Creazione di un thread e passaggio della established socket */
				TCPServerThread thread = new TCPServerThread( connectionSocket );
								
				/* Avvio del thread */
				thread.start();
			}
		}
		catch( IOException e ) {
			System.err.println("C'è un server già attivo alla porta 6789");
		}
	}
}