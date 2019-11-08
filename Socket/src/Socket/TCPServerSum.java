package Socket;

import java.io.*;
import java.net.*;

public class TCPServerSum
{
	public static void main( String[] args ) throws Exception {
		
		/* Inizializza l'input stream da tastiera */
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
		
		/* Leggi porta del server */
		System.out.println("Inserisci porta del server: ");
		int port = Integer.parseInt( inFromUser.readLine() );
		
		/* Inizializza la listening socket del server */
		ServerSocket listeningSocket = new ServerSocket( port );
		
		while( true ) {
			Socket establishedSocket = listeningSocket.accept();
			System.out.println( "Client port: " + establishedSocket.getPort() + " Client address: " + establishedSocket.getInetAddress().toString() );
			
			/* Inizializza lo stream di input dalla socket */
			BufferedReader inFromClient = new BufferedReader( new InputStreamReader( establishedSocket.getInputStream() ) );
			
			/* Inizializza lo stream di output verso la socket */
			DataOutputStream outToClient = new DataOutputStream ( establishedSocket.getOutputStream() );
			
			int result = Integer.parseInt( inFromClient.readLine() ) + Integer.parseInt( inFromClient.readLine() );
			
			outToClient.writeBytes( Integer.toString( result ) + '\n' );
		}		
	}
}