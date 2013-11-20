/** * Description: This class is for the database interaction with the item, hunt tables on insert, delete, update, and query 
 * 
 * @author Ben Casey
 * @author Craig Soto II
 * 
 *  also used the TODO Demo from class
 */
package edu.mines.freeganquestcaseysoto;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;


public class FreeganContentProvider extends ContentProvider
{
	private FreeganDatabaseHelper database;

	// Used for the UriMacher
	private static final int HUNTS = 7;
	private static final int HUNTS_ID = 8;
	private static final int ITEMS = 21;
	private static final int ITEMS_ID = 22;

	private static final String AUTHORITY = "edu.mines.freeganquestcaseysoto.freeganquestcontentprovider";

	private static final String BASE_PATH = "hunts";
	private static final String BASE_PATH_H = "items";
	public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + BASE_PATH );
	public static final Uri CONTENT_URI_H = Uri.parse( "content://" + AUTHORITY + "/" + BASE_PATH_H );

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/hunts";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/hunts";

	public static final String CONTENT_TYPE_H = ContentResolver.CURSOR_DIR_BASE_TYPE + "/items"; // TODO
	public static final String CONTENT_ITEM_TYPE_H = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/items"; // TODO

	private static final UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH );

	static
	{
		//quests
		sURIMatcher.addURI( AUTHORITY, BASE_PATH, HUNTS );
		sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/#", HUNTS_ID );

		//items/objects
		sURIMatcher.addURI( AUTHORITY, BASE_PATH_H, ITEMS );
		sURIMatcher.addURI( AUTHORITY, BASE_PATH_H + "/#", ITEMS_ID );
	}

	@Override
	public boolean onCreate()
	{
		database = new FreeganDatabaseHelper( getContext() );
		return false;
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder )
	{
		// Using SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exist
		int tableName = checkColumns( projection );

		// Set the table
		if(tableName == HUNTS){
			queryBuilder.setTables( ManagerHuntTable.TABLE_NAME );
		} else if(tableName == ITEMS){
			queryBuilder.setTables( ItemTable.TABLE_NAME );
		}


		int uriType = sURIMatcher.match( uri );
		switch( uriType )
		{
		case HUNTS:
			break;
		case ITEMS:
			break;
		case HUNTS_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere( ManagerHuntTable.COLUMN_ID + "=" + uri.getLastPathSegment() );
			break;
		case ITEMS_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere( ItemTable.COLUMN_ID + "=" + uri.getLastPathSegment() );
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query( db, projection, selection, selectionArgs, null, null, sortOrder );

		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri( getContext().getContentResolver(), uri );

		return cursor;
	}

	@Override
	public String getType( Uri uri )
	{
		return null;
	}

	@Override
	public Uri insert( Uri uri, ContentValues values )
	{
		int uriType = sURIMatcher.match( uri );
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		//int rowsDeleted = 0;
		long id = 0;
		switch( uriType )
		{
		case HUNTS:
			id = sqlDB.insert( ManagerHuntTable.TABLE_NAME, null, values );
			break;
		case ITEMS:
			id = sqlDB.insert( ItemTable.TABLE_NAME, null, values );
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return Uri.parse( BASE_PATH + "/" + id );
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs )
	{
		int uriType = sURIMatcher.match( uri );
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		String id = uri.getLastPathSegment();

		switch( uriType )
		{
		case HUNTS:
			rowsDeleted = sqlDB.delete( ManagerHuntTable.TABLE_NAME, selection, selectionArgs );
			break;
		case ITEMS:
			rowsDeleted = sqlDB.delete( ItemTable.TABLE_NAME, selection, selectionArgs );
			break;
		case HUNTS_ID:
			if( TextUtils.isEmpty( selection ) )
			{
				rowsDeleted = sqlDB.delete( ManagerHuntTable.TABLE_NAME, ManagerHuntTable.COLUMN_ID + "=" + id, null );
			}
			else
			{
				rowsDeleted = sqlDB
						.delete( ManagerHuntTable.TABLE_NAME, ManagerHuntTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs );
			}
			break;
		case ITEMS_ID:
			if( TextUtils.isEmpty( selection ) )
			{
				rowsDeleted = sqlDB.delete( ItemTable.TABLE_NAME, ItemTable.COLUMN_ID + "=" + id, null );
			}
			else
			{
				rowsDeleted = sqlDB
						.delete( ItemTable.TABLE_NAME, ItemTable.COLUMN_ID + "=" + id + " and " + selection, selectionArgs );
			}
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return rowsDeleted;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs )
	{

		int uriType = sURIMatcher.match( uri );
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch( uriType )
		{
		case HUNTS:
			rowsUpdated = sqlDB.update( ManagerHuntTable.TABLE_NAME, values, selection, selectionArgs );
			break;
		case HUNTS_ID:
			String id = uri.getLastPathSegment();
			if( TextUtils.isEmpty( selection ) )
			{
				rowsUpdated = sqlDB.update( ManagerHuntTable.TABLE_NAME, values, ManagerHuntTable.COLUMN_ID + "=" + id, null );
			}
			else
			{
				rowsUpdated = sqlDB.update( ManagerHuntTable.TABLE_NAME, values, ManagerHuntTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs );
			}
			break;
		case ITEMS:
			rowsUpdated = sqlDB.update( ItemTable.TABLE_NAME, values, selection, selectionArgs );
			break;
		case ITEMS_ID:
			id = uri.getLastPathSegment();
			if( TextUtils.isEmpty( selection ) )
			{
				rowsUpdated = sqlDB.update( ItemTable.TABLE_NAME, values, ItemTable.COLUMN_ID + "=" + id, null );
			}
			else
			{
				rowsUpdated = sqlDB.update( ItemTable.TABLE_NAME, values, ItemTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs );
			}
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return rowsUpdated;
	}

	private int checkColumns( String[] projection )
	{
		int tableName = 0;

		String[] availableHunts = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		String[] availableItems = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };

		if( projection != null )
		{
			HashSet<String> requestedColumns = new HashSet<String>( Arrays.asList( projection ) );
			HashSet<String> availableColumnsHunts = new HashSet<String>( Arrays.asList( availableHunts ) );
			HashSet<String> availableColumnsItems = new HashSet<String>( Arrays.asList( availableItems ) );
			// Check if all columns which are requested are available
			if( !availableColumnsHunts.containsAll( requestedColumns )  &&  !availableColumnsItems.containsAll( requestedColumns ) )
			{
				throw new IllegalArgumentException( "Unknown columns in projection" );
			} else if( availableColumnsHunts.containsAll( requestedColumns ) ){
				tableName = HUNTS;
			} else {
				tableName = ITEMS;
			}
		}
		return tableName;
	}  

}