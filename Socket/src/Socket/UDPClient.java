package Socket;

import java.io.*;
import java.net.*;

public class UDPClient
{
	public static void main( String[] args ) throws Exception {
		
		/* Inizializza l'input stream da tastiera */
		BufferedReader inFromUser = new BufferedReader ( new InputStreamReader ( System.in ) );
		
		/* Inizializza una "datagram socket" su una porta disponibile*/
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			
			/* Ottieni l'indirizzo IP dell'hostname specificato, contattando eventualmente il DNS */
			InetAddress IPAddress = InetAddress.getByName( "localhost" );
			
			byte[] sendData	= new byte[ 1024 ];
			byte[] receivedData	= new byte[ 1024 ];
			
			/* Leggi da tastiera */
			System.out.print("Write something UDP: ");
			String sentence = inFromUser.readLine() + '\n';
			sendData = sentence.getBytes();
			
			/* Prepapara il pacchetto da spedire, specificando contenuto, indirizzo e porta del server */
			DatagramPacket sendPck = new DatagramPacket( sendData, sendData.length, IPAddress, 9876 );
			
			/* Invia il pacchetto attraverso la socket */
			clientSocket.send( sendPck );
			
			/* Prepara la struttura dati utilizzata per contenere il pck in ricezione */
			DatagramPacket receivedPck = new DatagramPacket( receivedData, receivedData.length );
			
			/* Ricevi il pacchetto dal server */
			clientSocket.receive( receivedPck );
			
			String modified = new String( receivedPck.getData() );
			
			System.out.println( "From Server UDP: " + modified );
			clientSocket.close();
		}
		catch( IOException e ) {
			System.err.println(e);
		}
	}
}