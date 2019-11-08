package Item;

import java.io.*;

/**
 * La classe ItemID implementa la descrizione di un oggetto da vendere in una asta on-line.
 * I campi della classe sono:
 * - un ID incrementato automaticamente
 * - una descrizione sommaria
 * - una base d'asta
 * - il nome del venditore, con la sua porta
 * - un flag che indica se, per il dato oggetto, l'asta sia già iniziata o meno
 */

public class ItemID implements Serializable
{
	private static final long serialVersionUID = 1L;
	private int ID;
	private String description;
	private double minimumPrice;
	
	public ItemID( int id, String descr, double min ) {
		ID = id;
		description = descr;
		minimumPrice = min;
	}
	
	public int getID() { return ID; }
	public String getDescription() { return description; }
	public synchronized double getMinimumPrice() { return minimumPrice; }
	
	public void changeID( int t ) { ID = t; }
	public void changeDescription( String t ) { description = t; }
	public synchronized boolean changeMinimumPrice( double t ) {
		if( t > minimumPrice ) {
			minimumPrice = t;
			return true;
		}
		else return false;
	}

	public String toString() {
		return new String( ID + ". " + description + ", prezzo base " + minimumPrice );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result
				+ ( ( description == null ) ? 0 : description.hashCode() );
		long temp;
		temp = Double.doubleToLongBits( minimumPrice );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) return true;
		if( obj == null ) return false;
		if( !( obj instanceof ItemID ) ) return false;
		ItemID other = (ItemID) obj;
		if( ID != other.ID ) return false;
		if( description == null ) {
			if( other.description != null )
				return false;
		} else if( !description.equals( other.description ) )
			return false;
		if( Double.doubleToLongBits( minimumPrice ) != Double.doubleToLongBits( other.minimumPrice ) )
			return false;
		return true;
	}
}