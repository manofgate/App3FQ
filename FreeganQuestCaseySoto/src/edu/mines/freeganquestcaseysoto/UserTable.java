/**
* Description: This class sets up the item table for the database.  
*
* @author Craig J. Soto II
* @author Ben Casey
*/

package edu.mines.freeganquestcaseysoto;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class UserTable {
	// Database table column names
	public static final String TABLE_NAME = "users";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_USER_NAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	// Database creation SQL statement
	private static final String TABLE_CREATE = "create table " + TABLE_NAME + "(" + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_NAME + " text not null," + 
			COLUMN_USER_NAME + " text not null, " +
			COLUMN_PASSWORD + " text not null" + ");";

	/**
	* The onCreate method is used to create the items Table by executing the .execSQL command.
	* 
	* @param database - the database object that will be used to create the item Table
	*/
	public static void onCreate( SQLiteDatabase database ) {
		database.execSQL( TABLE_CREATE );
	}

	/**
	* The onUpgrade method is used to update the Items Table in the case where the database version
	* needs to be updated. It will execute the SQL command to drop the table then call onCreate to 
	* create the table again.   
	* 
	* @param database - the database object that will be used to execute SQL commands
	* @param oldVersion - the integer value of the old version of the db (used only of logging purposes)
	* @param newVersion - the integer value of the new version of the db (used only of logging purposes)
	*/
	public static void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
		Log.w( UserTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data." );
		database.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
		onCreate( database );
	}
}
