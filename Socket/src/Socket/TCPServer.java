package Socket;

import java.io.*;
import java.net.*;

public class TCPServer
{	
	public static void main( String[] args ) throws Exception {

		String clientSentence;
		String modifiedSentence;
		
		/* Inizializza una "listening socket" sulla porta specificata */
		try {
	        ServerSocket welcomeSocket = new ServerSocket( 6789 );
			while( true ) {
				/* 
				 * La chiamata accept è bloccante. All'arrivo di una nuova connessione, crea una nuova "established socket"
				 * */
				Socket connectionSocket = welcomeSocket.accept();
			
				/* Inizializza lo stream di input dalla socket */
				BufferedReader inFromClient = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream() ) );
				
				/* Inizializza lo stream di output verso la socket */
				DataOutputStream outToClient = new DataOutputStream ( connectionSocket.getOutputStream() );
				
				/* Legge riga dal client */
				clientSentence = inFromClient.readLine();
				/* Modifica la stringa e la rimanda */
				modifiedSentence = clientSentence.toUpperCase() + '\n';
				
				outToClient.writeBytes( modifiedSentence );
			}
		}
		catch( IOException e ) {
			System.err.println("C'è un server già attivo alla porta 6789");
		}
	}
}