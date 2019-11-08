package Utilities;

import java.util.*;

/**
 * Coda esclusiva del SellerServer, usata per stampare i suoi messaggi a video:
 * - il consumatore è il thread SelelrPrinter
 * - i produttori sono i vari SellerThread e il Timer
 */

public class Queue
{
	public ArrayList< Message > buffer = new ArrayList< Message >();
	
	public synchronized void put( Message msg ) {
		buffer.add( msg );
		/* Sveglio il produttore nel caso fosse in attesa di messaggi */
		notify();
	}
	
	public synchronized Message take() {
		Message msg = null;
		/* Evito polling mettendo il consumatore in wait se non ho messaggi nel buffer */
		while( buffer.size() == 0 ) {
			try { wait(); }
			catch( InterruptedException e ) {
				e.printStackTrace();
			}
		}
		if( buffer.size() > 0 ){
			msg = buffer.get( 0 );
			buffer.remove( 0 );
		}
		return msg;
	}
}