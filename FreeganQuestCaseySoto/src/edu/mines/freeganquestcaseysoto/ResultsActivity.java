package edu.mines.freeganquestcaseysoto;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ResultsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	private LazyAdapter adapter; //helps assist with database interactions 
	public static final String HW_NAME = "NameOfitem";
	private String huntName; //receives and passes on hunt name from the MainActivity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		//Get/set the huntName in the activity
		huntName = getIntent().getStringExtra( MainActivity.HUNT_NAME);
		TextView mhuntText = (TextView)findViewById(R.id.huntName);
		mhuntText.setText(huntName);

		//Set up ListView
		this.getListView().setDividerHeight( 2 );
		registerForContextMenu( getListView() );

		//Fill the Listview table
		fillFinalTime();
		fillData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results, menu);
		return true;
	}

	/**
	 * The fillData method fills the ListView with the item. 
	 */
	private void fillData() {
		//Fields in the DB from which we map 
		String[] from = new String[] { ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_DESCRIPTION };

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.name, R.id.loc, R.id.answer, R.id.descrption };

		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );

		ArrayList<HashMap<String, Object>> d = new ArrayList<HashMap<String, Object>>();
		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_DISPLAY, ItemTable.COLUMN_ANSWER_PIC };
		String[] selection = {huntName};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, "hunt=?", selection, null );
			for(int i=0; i< cursor.getCount(); ++i){
					cursor.moveToNext();
					HashMap<String, Object> items = new HashMap<String, Object>();
					String ans = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER ) );
					String  desc = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
					String locName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_LOCATION ) );
					String disp =  cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DISPLAY ) );
					String itemName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
					//Log.d("FREEGAN::CHA", "the name of the item is :" + itemName);
					byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER_PIC ));
					items.put(ItemTable.COLUMN_ANSWER, ans);
					items.put(ItemTable.COLUMN_DESCRIPTION, desc);
					items.put(ItemTable.COLUMN_LOCATION, locName);
					items.put(ItemTable.COLUMN_DISPLAY, disp);
					items.put(ItemTable.COLUMN_NAME, itemName);
					items.put(ItemTable.COLUMN_ANSWER_PIC, b);
					
					d.add(items);
			}
			cursor.close();
		this.adapter = new LazyAdapter( this,  d, R.layout.item_list_row_results);
		// Let this ListActivity display the contents of the cursor adapter.
		setListAdapter( this.adapter );
	}

	private void fillFinalTime(){
		String[] projection = { TimerTable.COLUMN_ID, TimerTable.COLUMN_HUNT_NAME, TimerTable.COLUMN_TIME};
		String[] selection = {huntName};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_T, projection, "hunt=?", selection, ItemTable.COLUMN_ID + " DESC" );
		if(cursor.moveToFirst()){
			String time = cursor.getString(2);
			TextView timerView = (TextView)findViewById(R.id.timeView);
			timerView.setText(time);
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
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_NAME, ItemTable.COLUMN_DISPLAY };
		String[] selection = {huntName};
		CursorLoader cursorLoader = new CursorLoader( this, FreeganContentProvider.CONTENT_URI_I, projection, "hunt=?", selection, null );

		return cursorLoader;
	}

	/**
	 * The onLoadFinished is a needed abstract method to help load the data in the ListView
	 * 
	 * @param arg0 - unused but needed for abstract method
	 * @param arg1 - used when swapping cursor
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		ArrayList<HashMap<String, Object>> d = new ArrayList<HashMap<String, Object>>();
		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_DISPLAY, ItemTable.COLUMN_ANSWER_PIC };
		String[] selection = {huntName};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, "hunt=?", selection, null );
			for(int i=0; i< cursor.getCount(); ++i){
					cursor.moveToNext();
					HashMap<String, Object> items = new HashMap<String, Object>();
					String ans = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER ) );
					String  desc = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
					String locName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_LOCATION ) );
					String disp =  cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DISPLAY ) );
					String itemName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
					//Log.d("FREEGAN::CHA", "the name of the item is :" + itemName);
					byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER_PIC ));
					items.put(ItemTable.COLUMN_ANSWER, ans);
					items.put(ItemTable.COLUMN_DESCRIPTION, desc);
					items.put(ItemTable.COLUMN_LOCATION, locName);
					items.put(ItemTable.COLUMN_DISPLAY, disp);
					items.put(ItemTable.COLUMN_NAME, itemName);
					items.put(ItemTable.COLUMN_ANSWER_PIC, b);
					
					d.add(items);
			}
			cursor.close();
		this.adapter = new LazyAdapter( this,  d, R.layout.item_list_row_results);
		// Let this ListActivity display the contents of the cursor adapter.
		setListAdapter( this.adapter );
	}

	/**
	 * The onLoadReset is a needed abstract method to help load the data in the ListView
	 * 
	 * @param arg0 - unused but needed for abstract method
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		//this.adapter.swapCursor( null );
	}

}
