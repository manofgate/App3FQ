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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

@SuppressLint("NewApi")
public class CopyOfItemActivity extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int DELETE_ID = Menu.FIRST + 1; //integer id for the delete option for long press
	private SimpleCursorAdapter adapter; //helps assist with database interactions 
	public static final String HW_NAME = "NameOfitem";
	private String huntName = ""; //receives and passes on hunt name from the MainActivity

	/**
	 * The onCreate method retrieves any saved instances and sets the content view layout. It
	 * retrieves and sets the hunt name from the ManagerMain, fills the item table with the 
	 * respective hunt's item, and sets up the context menu.   
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values 
	 */
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            huntName = savedInstanceState.getString(CopyOfManagerMain.HUNT_NAME);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.item_list_frag, container, false);
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    registerForContextMenu(this.getListView());
	}
	/*@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		getActivity().setContentView( R.layout.item_list );

		//Get/set the huntName in the activity
		//huntName = getActivity().getIntent().getStringExtra( ManagerMain.HUNT_NAME);
		//TextView mhuntText = (TextView)getActivity().findViewById(R.id.huntName);
		//mhuntText.setText(huntName);
		
		//Set up ListView
		this.getListView().setDividerHeight( 2 );
		registerForContextMenu( getListView() );

		//Fill the Listview table
		fillData();
	}
	
	*/
	 @Override
	    public void onStart() {
	        super.onStart();
	        Log.d("FQ::COIA", "hunt name is" + huntName);
	        // During startup, check if there are arguments passed to the fragment.
	        // onStart is a good place to do this because the layout has already been
	        // applied to the fragment at this point so we can safely call the method
	        // below that sets the article text.
	        updateArticleView(huntName);
	        Bundle args = getArguments();
	        if (args != null) {
	            // Set article based on argument passed in
	            updateArticleView(args.getString(CopyOfManagerMain.HUNT_NAME));
	        } else if (huntName != "") {
	        	
	            // Set article based on saved instance state defined during onCreateView
	            updateArticleView(huntName);
	        }
	    }
	 
	 
	    public void updateArticleView(String position) {
	    	huntName = position;
	    	getLoaderManager().restartLoader(0, null, this); 
	        fillData();
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
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
		String[] selection = {huntName};
		CursorLoader cursorLoader = new CursorLoader( getActivity(), FreeganContentProvider.CONTENT_URI_H, projection, "hunt=?", selection, null );

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
		String[] from = new String[] { ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION };

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.hwName, R.id.date, R.id.descrption };

		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );

		// Note the last parameter to this constructor (zero), which indicates the adaptor should
		// not try to automatically re-query the data ... the loader will take care of this.
		this.adapter = new SimpleCursorAdapter( getActivity(), R.layout.item_list_row, null, from, to, 0 ){
			//Change the color of each ListItem to help the user
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				if (position %2 ==1) {
					v.setBackgroundColor(Color.argb(5, 100, 100, 100));
				} else {
					v.setBackgroundColor(Color.argb(5, 170, 170, 170)); //or whatever was original
				}

				return v;
			}

		};
		
		// Let this ListActivity display the contents of the cursor adapter.
		setListAdapter( this.adapter );
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
		menu.add( 0, DELETE_ID, 0, R.string.menu_delete );
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
		switch( item.getItemId() ) {
		//When the delete option is selected delete the item row from the db
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Uri uri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" + info.id );
			getActivity().getContentResolver().delete( uri, null, null );
			fillData();
			return true;
		}
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
	public void onListItemClick( ListView l, View v, int position, long id ) {
		super.onListItemClick( l, v, position, id );

		//Get the AdditemActivity intent
		Intent i = new Intent( getActivity(), AddItemActivity.class );

		//Query the database for the necessary information
		Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" + id );
		String[] projection = { ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
		Cursor cursor = getActivity().getContentResolver().query( huntUri, projection, null, null, null );

		//Retrieve the information from the database. 
		cursor.moveToFirst();	    
		String name = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
		String date = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_LOCATION ) ).replace("-", "");
		String desc = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
		String huntName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_HUNT_NAME ) );
		cursor.close();

		//Set the variables that will be used in the AdditemActivity
		i.putExtra(ManagerMain.ITEM_NAME_TEXT, name);
		i.putExtra(ManagerMain.LOC_TEXT, date);
		i.putExtra(ManagerMain.DESC_TEXT, desc);
		i.putExtra(ManagerMain.HUNT_NAME, huntName);

		startActivity( i );
	}
}
