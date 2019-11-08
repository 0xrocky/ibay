package Socket;

import java.io.*;
import java.net.*;

public class UDPServer
{
	public static void main( String[] args ) {
		
		/* Inizializza "datagram socket" specificando porta di ascolto */
		try {
			DatagramSocket serverSocket = new DatagramSocket( 9876 );
			
			byte[] receivedData	= new byte[ 1024 ];
			byte[] sendData	= new byte[ 1024 ];

			while( true ) {
				/* Prepara struttura dati per contenere pacchetto in ricezione */
				DatagramPacket receivedPck = new DatagramPacket( receivedData, receivedData.length );
				
				/* Riceve pacchetto dal client */
				serverSocket.receive( receivedPck );
				
				String sentence = new String( receivedPck.getData() );
				
				/* Ottiene dal pacchetto info sul mittente */
				InetAddress IPAddress = receivedPck.getAddress();
				int port = receivedPck.getPort();
				
				String modified = sentence.toUpperCase() + '\n';
				sendData = modified.getBytes();
			
				/* Prepara pacchetto da spedire specificando contenuto, indirizzo e porta destinatario */
				DatagramPacket sendPck = new DatagramPacket( sendData, sendData.length, IPAddress, port );
				
				serverSocket.send( sendPck );				
			}
		}
		catch( IOException e ) {
			System.err.println(e);
		}
	}
}