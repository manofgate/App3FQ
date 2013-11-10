package edu.mines.freeganquestcaseysoto;


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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ManagerMain extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, InputDialogFragment.Listener{
	private SimpleCursorAdapter adapter;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int SHOW_LOC_ID = Menu.FIRST + 3;
	private String huntName;
	public static final String HUNT_NAME = "NameOfHunt";
	/*public static final String HW_NAME_TEXT = "NameOfHW";
	public static final String DATE_TEXT = "DueDate";
	public static final String DESC_TEXT = "Description";*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hunts_list);
		this.getListView().setDividerHeight( 4);
		fillData();
		registerForContextMenu( getListView() );
	}

	
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
	        Intent i = new Intent(this, ManagerMain.class);
	        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(i);

	        return true;
	      }
	      default:
	          return super.onOptionsItemSelected(item);
	    }
	  }
	
	public void onDialog(View view){
		Bundle args = new Bundle();
		args.putInt( "dialogID", 1 );
		args.putString( "prompt", getString( R.string.statement ) );

		InputDialogFragment dialog = new InputDialogFragment();
		dialog.setArguments( args );
		dialog.show( getFragmentManager(), "Dialog" );

	}
	/**
	 * Inserts the hunt into the hunt Table.
	 * checks to see if there are now 2 hunt of the same name and deletes the last inserted hunt
	 */
	public void insertNewHunt(){
		ContentValues values = new ContentValues();
		//values.put(huntTable.COLUMN_ID, "idd");
		values.put( ManagerHuntTable.COLUMN_NAME, huntName );
		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		String[] selection = {huntName};
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI, values );
		
		//chgecks to see if that hunt name has already been added
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, "name=?", selection, ManagerHuntTable.COLUMN_ID + " DESC" );
		if(cursor.getCount() >1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +huntName+" hunt!" , Toast.LENGTH_LONG);
			toast.show();
			fillData();
		}
		cursor.close();
		
	}
	
	/**
	 * Updates the hunt Name and it's corresponding homework.
	 * @param newhuntName : used to update the name while huntName is the old hunt name to query
	 */
	public void updateNewHunt(String newHuntName){
		ContentValues values = new ContentValues();
		values.put( ManagerHuntTable.COLUMN_NAME, newHuntName );
		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		String[] selection = {huntName};
		String[] querySelection = {newHuntName};
		//chgecks to see if that hunt name is already in database and adds if not. 
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, "name=?", querySelection, ManagerHuntTable.COLUMN_ID + " DESC" );
		//Log.d("SchoolScheduler::Update Debu", "curosor count : " + cursor.getCount());
		if(cursor.getCount() <1){
			int rowsUpdated = getContentResolver().update( FreeganContentProvider.CONTENT_URI, values, "name=?", selection );
			Log.d("SchoolScechulder::update Debug", rowsUpdated + ": " + this.huntName + ": " +newHuntName );	
			fillData();
			
			String[] selectionC = {huntName};
			String[] projection2 = {ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME};
			
			Cursor cursorC = getContentResolver().query(FreeganContentProvider.CONTENT_URI_H, projection2, "hunt=?", selectionC, null);
			ContentValues valuesC = new ContentValues();
			valuesC.put( ItemTable.COLUMN_HUNT_NAME, newHuntName );
			for(int i=0; i < cursorC.getCount(); ++i){
				rowsUpdated = getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, valuesC, "hunt=?", selectionC );
				
			}
		}
		cursor.close();
		
		
	}
	/**
	 * overriden function from listView that when clicked will open up the Item activity to show the hunts Items.
	 */
	@Override
	protected void onListItemClick( ListView l, View v, int position, long id )
	{
		super.onListItemClick( l, v, position, id );
		Intent i = new Intent( this, ItemActivity.class );
		Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + id );
		String[] projection = { ManagerHuntTable.COLUMN_NAME };

		//gets the uris for the same id, moves it to first position.
		Cursor cursor = getContentResolver().query( huntUri, projection, null, null, null );
		String name= "";
		cursor.moveToFirst();	    
		name = cursor.getString( cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
		cursor.close();
		i.putExtra(HUNT_NAME, name);
		i.putExtra( FreeganContentProvider.CONTENT_ITEM_TYPE, huntUri );
		startActivity( i );
	}

	/**
	 * overridden function from listview, if long pressed will delete or edit the hunt.
	 * The delete, deletes the hunt and deletes the corresponding items from the item table
	 * The edit uses the input Dialog and that changes the hunt name and corresponding items.
	 */
	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Uri uri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + info.id );
			
			//query to get the hunt name that is bieng deleted
			String[] projection2 = { ManagerHuntTable.COLUMN_NAME };
			Cursor cursor2 = getContentResolver().query( uri, projection2, null, null, null );
			String name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();
			this.huntName= name2;
			
			getContentResolver().delete( uri, null, null );
			
			//get all homework associsated with this hunt and delete it.
			String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
			String[] querySelection = { this.huntName };
			//gets the uris for the same id, moves it to first position.
			uri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/");
			Cursor cursor = getContentResolver().query( uri, projection, "hunt=?", querySelection, null );
			cursor.moveToFirst();
			for(int i=0; i < cursor.getCount(); ++i){
				String id =  cursor.getString(cursor.getColumnIndexOrThrow(ItemTable.COLUMN_ID));
				uri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" + id );
				getContentResolver().delete( uri, null, null );
				cursor.moveToNext();
			}
			cursor.close();
			fillData();
			return true;
		case EDIT_ID: 
			info = (AdapterContextMenuInfo)item.getMenuInfo();
			uri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + info.id );
			String[] projection3 = { ManagerHuntTable.COLUMN_NAME };

			//gets the uris for the same id, moves it to first position.
			cursor2 = getContentResolver().query( uri, projection3, null, null, null );
			name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();
			this.huntName= name2;
			Bundle args = new Bundle();
			args.putInt( "dialogID", 2 );
			args.putString( "prompt", getString( R.string.statement ) );

			InputDialogFragment dialog = new InputDialogFragment();
			dialog.setArguments( args );
			dialog.show( getFragmentManager(), "Dialog" );
			return true;
		case SHOW_LOC_ID:
			info = (AdapterContextMenuInfo)item.getMenuInfo();
			uri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + info.id );
			String[] projection4 = { ManagerHuntTable.COLUMN_NAME };

			//gets the uris for the same id, moves it to first position.
			cursor2 = getContentResolver().query( uri, projection4, null, null, null );
			name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();

			Intent i = new Intent(this, LocationActivity.class);
			i.putExtra(HUNT_NAME, name2);
			startActivity(i);
		}
		return super.onContextItemSelected( item );
	}
	/**
	 * onCreateLoader loads the initial hunt table with anything that follows the projection in the database.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME };
		CursorLoader cursorLoader = new CursorLoader( this, FreeganContentProvider.CONTENT_URI, projection, null, null, null );
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		this.adapter.swapCursor( arg1 );
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		this.adapter.swapCursor( null );
	}
	
	/**
	 * the main aspect of updated the listview, so that the insertion, deletion, and editing shows up. 
	 * the adapter also adds background color for odd- even rows.
	 */
	private void fillData()
	{
		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { ManagerHuntTable.COLUMN_NAME };

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label };

		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );

		// Note the last parameter to this constructor (zero), which indicates the adaptor should
		// not try to automatically re-query the data ... the loader will take care of this.
		this.adapter = new SimpleCursorAdapter( this, R.layout.list_row, null, from, to, 0 ){
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
	 * overrides the dialog fragment for inputting hunt. depending on eit or inserting. 
	 * @param dialogID : the id returned to see if it is an insert or edit.
	 * @param input : the returned string.
	 */
	@Override
	public void onInputDone( int dialogID, String input )
	{
		Log.d( "School_Scheduler", "\"" + input + "\" received from input dialog with id =" + dialogID );

		if(dialogID == 1){
			this.huntName = input;
			insertNewHunt();
		}
		else if(dialogID == 2){
			updateNewHunt(input);
			
			
		}

	}

	/**
	 * Callback method from InputDialogFragment when the user clicks Cancel.
	 * 
	 * @param dialogID The dialog producing the callback.
	 */
	@Override
	public void onInputCancel( int dialogID )
	{
		Log.d( "School_Scheduler", "No input received from input dialog with id =" + dialogID );
	}

	/** The menu displayed on a long touch. */
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		menu.add( 0, DELETE_ID, 0, R.string.menu_delete );
		menu.add( 0, EDIT_ID, 0, R.string.menu_edit );
		menu.add(0, SHOW_LOC_ID, 0, R.string.menu_show_loc);
	}
}
