package Socket;

import java.io.*;
import java.net.*;

public class TCPClientSum
{
	public static void main( String[] args ) throws Exception {
		
		/* Inizializza l'input stream da tastiera */
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
		
		/* Leggi i due numeri da sommare */
		System.out.println("Inserisci i due numeri: ");
		String x = inFromUser.readLine();
		String y = inFromUser.readLine();
		
		/* Leggi indirizzo e porta del server */
		System.out.println("Inserisci indirizzo e porta del server: ");
		InetAddress address = InetAddress.getByName( inFromUser.readLine() );
		int port = Integer.parseInt( inFromUser.readLine() );
		
		/* Inizializza una socket client connessa al server */
		Socket clientSocket = new Socket( address, port );
		
		/* Inizializza lo stream di output verso la socket */
		DataOutputStream outToServer = new DataOutputStream( clientSocket.getOutputStream() );
		
		/* Inizializza lo stream di input dalla socket */
		BufferedReader inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
		
		/* Manda dati al server */
		outToServer.writeBytes( x + '\n' );
		outToServer.writeBytes( y + '\n' );
		
		int result = Integer.parseInt( inFromServer.readLine() );
		
		System.out.println( "x + y = " + result );
		clientSocket.close();
	}
}