package Socket;

import java.io.*;
import java.net.*;

public class TCPClient
{
	public static void main( String[] args ) throws Exception {

		String sentence;
		String modified;
		
		/* Inizializza l'input stream da tastiera */
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
		for( int i = 0; i < 3; i++ ) {
			/* Inizializza una socket client connessa al server */
			try {
		        Socket clientSocket = new Socket("localhost", 6789);
					
				/* Inizializza lo stream di output verso la socket */
				DataOutputStream outToServer = new DataOutputStream( clientSocket.getOutputStream() );
				
				/* Inizializza lo stream di input dalla socket */
				BufferedReader inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
				
				/* Legge input da utente */
				System.out.print("Write something TCP: ");
				sentence = inFromUser.readLine();
				
				/* Invia la riga al server */
				outToServer.writeBytes( sentence + '\n' );
			
				/* Legge la riga inviata dal server */
				modified = inFromServer.readLine();
				
				System.out.println( "From Server TCP: " + modified );
				
				clientSocket.close();
			}
			catch( IOException e ) {
				System.err.println(e);
			}
		}
	}
}