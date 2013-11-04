/** * Description: This class is for the course portion of the database. it is te helper with sql to actually make the database 
 * 
 * @author Ben Casey
 * 
 *  also used the TODO Demo from class
 */
package edu.mines.freeganquestcaseysoto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchedulerDatabaseHelper extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "coursetable.db";
  private static final int DATABASE_VERSION = 1;

  public SchedulerDatabaseHelper( Context context )
  {
    super( context, DATABASE_NAME, null, DATABASE_VERSION );
  }

  /** Method is called during creation of the database */
  @Override
  public void onCreate( SQLiteDatabase database )
  {
    CourseTable.onCreate( database );
    HomeworkTable.onCreate(database);
  }

  /** Method is called during an upgrade of the database, e.g. if you increase the database version. */
  @Override
  public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion )
  {
    CourseTable.onUpgrade( database, oldVersion, newVersion );
  }
}
