package Socket;

import java.io.*;
import java.net.*;

public class TCPServerThread extends Thread
{	
	private Socket connectionSocket = null;
	private BufferedReader inFromClient = null;
	private DataOutputStream outToClient = null;
	
	public TCPServerThread( Socket s ) {
		
		connectionSocket = s;
		try {
			/* Inizializza lo stream di input dalla socket */
			inFromClient = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream() ) );
			
			/* Inizializza lo stream di output verso la socket */
			outToClient = new DataOutputStream ( connectionSocket.getOutputStream() );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		String clientSentence;
		String modifiedSentence;
		try {
			/* Legge riga dal client */
			clientSentence = inFromClient.readLine();
			/* Modifica la stringa e la rimanda */
			modifiedSentence = clientSentence.toUpperCase() + '\n';
				
			outToClient.writeBytes( modifiedSentence );
			connectionSocket.close();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}