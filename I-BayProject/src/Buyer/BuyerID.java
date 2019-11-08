package Buyer;

import java.net.*;
import java.io.*;

/**
 * La classe BuyerID implementa la descrizione di un acquirente in una asta on-line.
 */

public class BuyerID implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private InetAddress IPAddress;
	private int port;
	
	public BuyerID( String n, InetAddress IP, int p ) {
		name = n;
		IPAddress = IP;
		port = p;
	}
	
	public String getName() { return name; }
	public InetAddress getIPAddress() { return IPAddress; }
	public int getPort() { return port; }
	
	public void changeName( String t ) { name = t; }
	public void changeIPAddress( InetAddress t ) { IPAddress = t; }
	public void changePort( int t ) { port = t; }
	
	public String toString() {
		return new String( name + " -- " + IPAddress + " -- " + port );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ( ( IPAddress == null ) ? 0 : IPAddress.hashCode() );
		result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( !( obj instanceof BuyerID ) ) return false;
		BuyerID other = (BuyerID) obj;
		if( IPAddress == null ) {
			if( other.IPAddress != null )
				return false;
		} else if( !IPAddress.equals( other.IPAddress ) )
			return false;
		if( name == null ) {
			if( other.name != null )
				return false;
		} else if( !name.equals(other.name) )
			return false;
		if( port != other.port )
			return false;
		return true;
	}
}