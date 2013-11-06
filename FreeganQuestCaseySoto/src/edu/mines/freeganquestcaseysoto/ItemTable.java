/**
* Description: This class sets up the homework table for the database.  
*
* @author Craig J. Soto II
* @author Ben Casey
*/

package edu.mines.freeganquestcaseysoto;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ItemTable {
	// Database table column names
	public static final String TABLE_NAME = "homework";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_DESCRIPTION = "desc";
	public static final String COLUMN_COURSE_NAME = "course";

	// Database creation SQL statement
	private static final String TABLE_CREATE = "create table " + TABLE_NAME + "(" + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_NAME + " text not null," + 
			COLUMN_DATE + " text not null," +
			COLUMN_DESCRIPTION + " text not null," +
			COLUMN_COURSE_NAME + " text not null" + ");";

	/**
	* The onCreate method is used to create the Homework Table by executing the .execSQL command.
	* 
	* @param database - the database object that will be used to create the Homework Table
	*/
	public static void onCreate( SQLiteDatabase database ) {
		database.execSQL( TABLE_CREATE );
	}

	/**
	* The onUpgrade method is used to update the Homework Table in the case where the database version
	* needs to be updated. It will execute the SQL command to drop the table then call onCreate to 
	* create the table again.   
	* 
	* @param database - the database object that will be used to execute SQL commands
	* @param oldVersion - the integer value of the old version of the db (used only of logging purposes)
	* @param newVersion - the integer value of the new version of the db (used only of logging purposes)
	*/
	public static void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
		Log.w( ItemTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data." );
		database.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
		onCreate( database );
	}
}
