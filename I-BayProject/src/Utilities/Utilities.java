package Utilities;

import java.io.*;
import java.net.*;
import java.rmi.server.*;

/**
 * Classe che raccoglie una serie di metodi statici utili per operazioni generali
 */

public class Utilities
{
	public static final int MAX_BUYER = 3; //numero massimo di acquirenti per far partire l'sta

	/* Valida il parametro se è un numero */
	public static boolean checkIfNumber( String in ) {  
		try {
			Integer.parseInt( in );
		} catch( NumberFormatException ex ) {
			return false;
		}
		return true;
	}
	
	/* Valida il parametro se è un double: tipicamente utilizzato per controllare le offerte in input */
	public static boolean checkIfOffer( String in ) {  
		try {
			Double.parseDouble( in );
		} catch( NumberFormatException ex ) {
			return false;
		}
		return true;
	}
	
	/* Risolve indirizzo IP di un client RMI */
	public static InetAddress RMIIPresolution() {  
		try {
			return InetAddress.getByName( RemoteServer.getClientHost() );
		} catch( UnknownHostException e ) {
			e.printStackTrace();
		} catch( ServerNotActiveException e1 ) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/* Risolve indirizzo IP di un client da locale */
	public static InetAddress LocalIPresolution() {  
		try {
			return InetAddress.getLocalHost();
		} catch( UnknownHostException e ) {
			e.printStackTrace();
		}
		return null;
	}
	/* Write di un oggetto su stream via socket e flush del canale */
	public static synchronized void write( ObjectOutputStream objOut, Message msg ) {
		try {
			objOut.writeObject( msg );
			objOut.flush();
		} catch( EOFException o ) {
			System.exit( 0 );
		} catch( IOException e ){
			e.printStackTrace();
		}
	}
	
	/* Read di un oggetto su stream via socket */
	public static Message read( ObjectInputStream objIn ) {
		Object obj = null;
		Message msg = null;
		try {
			obj = objIn.readObject();
		} catch( EOFException o ) {
			System.exit( 0 );
		} catch( IOException e ) {
			e.printStackTrace();
		} catch( ClassNotFoundException e1 ) {
			e1.printStackTrace();
		}
		if( obj instanceof Message ) msg = (Message) obj;
		return msg;
	}
}