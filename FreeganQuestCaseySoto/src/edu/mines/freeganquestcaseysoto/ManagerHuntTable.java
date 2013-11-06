/** * Description: This class is for the course portion of the database. It holds the creation of the table and columns. 
 * 
 * @author Ben Casey
 * 
 *  also used the TODO Demo from class
 */


package edu.mines.freeganquestcaseysoto;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ManagerHuntTable
{
  // Database table
  public static final String TABLE_NAME = "hunts";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_NAME = "name";

  /**
   * creates the database strings for the columns of the table.
   */
  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + 
	       COLUMN_ID + " integer primary key autoincrement, " + 
	       COLUMN_NAME + " text not null" + ");";
  public static void onCreate( SQLiteDatabase database )
  {
    database.execSQL( DATABASE_CREATE );
  }

  /**
   * upgrades the database with the new version if it already exists.
   * @param database
   * @param oldVersion
   * @param newVersion
   */
  public static void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion )
  {
	 Log.w( ManagerHuntTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data." );
    database.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME );
    onCreate( database );
  }
}
