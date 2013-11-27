/**
 * Description: This class displays the list of items based on which hunt was selected. The 
 * 	user will be able to either add an item (touching the add button), edit an item (touching
 * 	the item in the list), and deleting an item (long press item in list). 
 *
 * @author Craig J. Soto II
 * @author Ben Casey
 */

/*
 * Web pages that helped contribute to writing the code for this class:
 * 
 * http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 */
package edu.mines.freeganquestcaseysoto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class HuntActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter adapter; //helps assist with database interactions 
	public static final String HW_NAME = "NameOfitem";
	private String huntName; //receives and passes on hunt name from the MainActivity
	private long startTime = 0L;
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;
	private TextView timerValue;
	private Handler customHandler = new Handler();

	/**
	 * The onCreate method retrieves any saved instances and sets the content view layout. It
	 * retrieves and sets the hunt name from the ManagerMain, fills the item table with the 
	 * respective hunt's item, and sets up the context menu.   
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values 
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.hunts_list_player );

		//Get/set the huntName in the activity
		huntName = getIntent().getStringExtra( MainActivity.HUNT_NAME);
		TextView mhuntText = (TextView)findViewById(R.id.huntName);
		mhuntText.setText(huntName);

		timerValue = (TextView) findViewById(R.id.timeView);
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);


		//Set up ListView
		this.getListView().setDividerHeight( 2 );
		registerForContextMenu( getListView() );

		//Fill the Listview table
		fillData();
	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			timerValue.setText(getTime(timeInMilliseconds, timeSwapBuff, updatedTime));
			customHandler.postDelayed(this, 0);
		}
	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mm, menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
		case R.id.action_manage:
		{
			Intent i = new Intent(this, ManagerFragment.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			return true;
		}
		case R.id.about_settings:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("About");
	        builder.setMessage(MainActivity.ABOUT_INFO);
	        builder.setPositiveButton("OK", null);
	        AlertDialog dialog = builder.show();
	        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
	        messageText.setGravity(Gravity.CENTER);
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	/**
	 * The onCreateLoader loads the item specific to the hunt. This makes sure we only see the 
	 * item for that hunt and no others. 
	 * 
	 * @param arg0 - unused but needed for abstract method
	 * @param arg1 - unused but needed for abstract method
	 * 
	 * @return cursorLoader - the item information from the database
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		//Retrieve item info from database
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
		String[] selection = {huntName};
		CursorLoader cursorLoader = new CursorLoader( this, FreeganContentProvider.CONTENT_URI_H, projection, "hunt=?", selection, null );

		return cursorLoader;
	}

	/**
	 * The onLoadFinished is a needed abstract method to help load the data in the ListView
	 * 
	 * @param arg0 - unused but needed for abstract method
	 * @param arg1 - used when swaping cursor
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		this.adapter.swapCursor( arg1 );
	}

	/**
	 * The onLoadReset is a needed abstract method to help load the data in the ListView
	 * 
	 * @param arg0 - unused but needed for abstract method
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		this.adapter.swapCursor( null );
	}

	/**
	 * The fillData method fills the ListView with the item. 
	 */
	private void fillData() {
		//Fields in the DB from which we map 
		String[] from = new String[] { ItemTable.COLUMN_ID, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION };

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.idName, R.id.loc, R.id.descrption };

		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );

		// Note the last parameter to this constructor (zero), which indicates the adaptor should
		// not try to automatically re-query the data ... the loader will take care of this.
		this.adapter = new SimpleCursorAdapter( this, R.layout.item_list_row_player, null, from, to, 0 ){
			//Change the color of each ListItem to help the user
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				if (position %2 ==1) {
					v.setBackgroundColor(Color.argb(TRIM_MEMORY_MODERATE, 100, 100, 100));
				} else {
					v.setBackgroundColor(Color.argb(TRIM_MEMORY_MODERATE, 170, 170, 170)); //or whatever was original
				}

				return v;
			}

		};

		// Let this ListActivity display the contents of the cursor adapter.
		setListAdapter( this.adapter );
	}

	/**
	 * The additemToList method starts the AdditemActivity. It also sets the needed elements
	 * used in that activity. 
	 * 
	 * @param view - this is necessary for the button to interact with the activity
	 */
	public void addItemToList(View view) {
		//Intent intent = new Intent(this, AddItemActivity.class);
		//intent.putExtra(ManagerMain.HUNT_NAME, huntName);
		//Set these to empty strings to prevent null point exception and prevent filling changeable
		//elements in the next activity. 
		/*intent.putExtra(MainActivity.HW_NAME_TEXT, "");
		intent.putExtra(MainActivity.DATE_TEXT, "");
		intent.putExtra(MainActivity.DESC_TEXT, "");*/
		//startActivity(intent);
	}

	/**
	 * The onCreateContextMenu method sets up the menu displayed on a long touch.
	 * 
	 * @param menu - the menu that is created to hold menu options
	 * @param v - view that will interact with the UI
	 * @param menuInfo - information needed for the menu
	 */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );

	}

	/**
	 * The onCreateContextItemSelected method handles the logic when an item in the context menu
	 * is selected. 
	 * 
	 * @param item - item that was selected
	 * 
	 * @return super.onContextItemSelected(item) - returns the item that was deleted. 
	 */
	@Override
	public boolean onContextItemSelected( MenuItem item ) {

		//When the delete option is selected delete the item row from the db

		return super.onContextItemSelected( item );
	}

	/**
	 * The onListItemClick method retrieves the information from the list, queries the database, 
	 * sets the respective variables with that info, then starts the AdditemActivity for editing
	 * purposes. 
	 * 
	 * @param l - the list from the Activity
	 * @param v - the view from the Activity
	 * @param position - the list item position in the list
	 * @param id - the id of the list item
	 */
	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		super.onListItemClick( l, v, position, id );

		//Get the AdditemActivity intent
		Intent i = new Intent( this, AddAnswerActivity.class );

		//Query the database for the necessary information
		Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" + id );
		String[] projection = { ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
		Cursor cursor = getContentResolver().query( huntUri, projection, null, null, null );

		//Retrieve the information from the database. 
		cursor.moveToFirst();	    
		//String name = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
		String huntName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_HUNT_NAME ) );
		cursor.close();

		//Set the variables that will be used in the AdditemActivity
		//i.putExtra(ManagerMain.ITEM_NAME_TEXT, name);

		i.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);

		startActivity( i );

	}

	@Override
	public void onBackPressed() {
		Toast.makeText(getApplicationContext(), "Must finish Quest before going back", Toast.LENGTH_LONG).show();
	}

	
	/**
	 * The onSaveInstanceState method saves all the global variables used within the activity. It does 
	 * this when the phone changes its orientation so that the variables can maintain the same values 
	 * when rotating the phone/emulator. It stores these values in the savedInstanceState.  
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		//Save the game scores, round scores, and tosses amounts for the respective teams
		savedInstanceState.putLong("START_TIME", startTime);
	}

	/**
	 * The onRestoreInstanceState method restores all the global variables that were saved when the phone
	 * changed orientation. 
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		//Sets the respective values for game information
		startTime = savedInstanceState.getLong("START_TIME");
	}
	
	public void onDialog(View view){
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);
		startActivity(i);		
		
		String finalTime = getTime(SystemClock.uptimeMillis() - startTime, 0L, 0L);
		
		insertTimer(finalTime);
		
		finish();
	}
	/**
	 * Inserts the hunt into the hunt Table.
	 * checks to see if there are now 2 hunt of the same name and deletes the last inserted hunt
	 */
	public void insertTimer(String finalTime){
		ContentValues values = new ContentValues();

		values.put( TimerTable.COLUMN_HUNT_NAME, huntName );
		values.put(TimerTable.COLUMN_TIME, finalTime);
		
		String[] projection = { TimerTable.COLUMN_ID, TimerTable.COLUMN_HUNT_NAME, TimerTable.COLUMN_TIME};
		String[] selection = {huntName, finalTime};
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI_T, values );

		//checks to see if that hunt name has already been added
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_T, projection, "name=?", selection, TimerTable.COLUMN_ID + " DESC" );
		if(cursor.getCount() >1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_T + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( TimerTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +huntName+" hunt!" , Toast.LENGTH_LONG);
			toast.show();
			fillData();
		}
		cursor.close();

	}
	
	private String getTime(long milliTime, long locTimeSwapBuff, long locUpdatedTime ){
		String time = "";
		
		locUpdatedTime = locTimeSwapBuff + milliTime;
		int secs = (int) (locUpdatedTime / 1000);
		int mins = secs / 60;
		secs = secs % 60;
		int milliseconds = (int) (locUpdatedTime % 1000);
		
		time = "" + mins + ":"
				+ String.format("%02d", secs) + ":"
				+ String.format("%03d", milliseconds);
		
		return time;
	}
}
