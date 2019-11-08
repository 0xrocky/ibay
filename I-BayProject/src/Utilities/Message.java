package Utilities;

import java.io.*;
import java.util.*;
import Item.ItemID;

/**
 * Classe che definisce il formato dei messaggi che viaggiano via socket tra le entità dell'applicazione
 */

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	/* Identifica un messaggio di primo contatto dal buyer al seller */
	public static final int SYN = 0;
	/* Identifica un messaggio di risposta dal seller al buyer */
	public static final int SYNACK = 1;
	/* Identifica un messaggio di notifica dell'avvio di un'asta */
	public static final int START = 2;
	/* Identifica un messaggio di aggiudicamento di un bene messo all'asta. É implicito che l'asta è finita */
	public static final int WINNER = 3;
	/* Identifica un messaggio di notifica degli acquirenti già confermati e in attesa acquirenti */
	public static final int ATTEND = 4;
	/* Identifica un messaggio di notifica nuovo acquirente */
	public static final int NEWBUYER = 5;
	/* Identifica un messaggio di una nuova offerta/rialzo */
	public static final int OFFERT = 6;
	/* Identifica un messaggio di countdown */
	public static final int TIMEOUT = 7;
	/* Identifica un messaggio di offerta validata a seguito di LOCK */
	public static final int UPDATEOFFERT = 8;
	
	private int command; // il codice del messaggio, una delle costanti prima definite
	private String name; // nome del buyer, o del seller: usato da SYN, SYNACK, OFFERT
	private ItemID itemID; // ID dell'item selezionato, usato per OFFERT
	private double offert; // offerta per l'item selezionato, usato per OFFERT
	private ArrayList< String > buyerList; // lista degli acquirenti che attendono l'avvio dell'asta
	private int time;
	
	public Message( int c ) {
		command = c;
		name = null;
		itemID = null;
		offert = -1;
		buyerList = null;
		time = -1;
	}
	
	public Message( int c, String n ) {
		command = c;
		name = n;
		itemID = null;
		offert = -1;
		buyerList = null;
		time = -1;
	}

	public Message( int c, String n, ItemID i, double p ) {
		command = c;
		name = n;
		itemID = i;
		offert = p;
		buyerList = null;
		time = -1;
	}
	
	public Message( int c, ArrayList< String> t ) {
		command = c;
		name = null;
		itemID = null;
		offert = -1;
		buyerList = t;
		time = -1;
	}
	
	public Message( int c, String t, double d ) {
		command = c;
		name = t;
		itemID = null;
		offert = d;
		buyerList = null;
		time = -1;
	}
	
	public Message( int c, int i, double d ) {
		command = c;
		name = null;
		itemID = null;
		offert = d;
		buyerList = null;
		time = i;
	}
	
	public static Message syn( String n ) {
		return new Message( SYN, n );
	}
	
	public static Message synAck( String n ) {
		return new Message( SYNACK, n );
	}
	
	public static Message start() {
		return new Message( START );
	}
	
	public static Message offert( String buyer, ItemID item, double offert ) {
		return new Message( OFFERT, buyer, item, offert );
	}
	
	public static Message attend( ArrayList< String > t ) {
		return new Message( ATTEND, t );
	}
	
	public static Message newBuyer( String newBuyer ) {
		return new Message( NEWBUYER, newBuyer );
	}
	
	public static Message winner( String w, double d ) {
		return new Message( WINNER, w, d );
	}
	
	public static Message timeout( int i, double d ) {
		return new Message( TIMEOUT, i, d );
	}
	
	public static Message updateOffert( String w, double d ) {
		return new Message( UPDATEOFFERT, w, d );
	}

	public int getCommand() {
		return command;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemID getItem() {
		return itemID;
	}

	public double getOffert() {
		return offert;
	}
	
	public ArrayList< String> getBuyers() {
		return buyerList;
	}
	
	public int getTime() {
		return time;
	}
}