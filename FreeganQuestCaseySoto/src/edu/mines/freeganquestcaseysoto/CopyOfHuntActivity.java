/**
 * Description: This class displays the list of items based on which hunt was selected. 
 * They can tap on the item and add an answer to it.
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
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CopyOfHuntActivity extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private SimpleCursorAdapter adapter; //helps assist with database interactions 
	public static final String HW_NAME = "NameOfitem";
	private String huntName; //receives and passes on hunt name from the MainActivity


	OnHeadlineSelectedListener mCallback;
	// The container Activity must implement this interface so the frag can deliver messages
	public interface OnHeadlineSelectedListener {
		/** Called by HeadlinesFragment when a list item is selected */
		public void onArticleSelected(String position);
	}
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
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
				R.layout.hunts_frag : R.layout.hunts_frag;

		Bundle args = getArguments();
		if (args != null) {
			// Set article based on argument passed in
			huntName = args.getString(MainActivity.HUNT_NAME);
		}


		getLoaderManager().restartLoader(0, null, this); 
		// Create an array adapter for the list view, using the Ipsum headlines array
		fillData();	
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
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

		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_DISPLAY };
		String[] selection = {HuntPlayFragment.huntName};
		CursorLoader cursorLoader = new CursorLoader( getActivity(), FreeganContentProvider.CONTENT_URI_I, projection, "hunt=?", selection, null );

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
		String[] from = new String[] { ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_ANSWER};

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.description_player, R.id.answer_player };
		
		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );
		
		// Note the last parameter to this constructor (zero), which indicates the adaptor should
		// not try to automatically re-query the data ... the loader will take care of this.
		this.adapter = new SimpleCursorAdapter( this.getActivity(), R.layout.item_list_row_player, null, from, to, 0 ){
			//Change the color of each ListItem to help the user
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				if (position %2 ==1) {
					v.setBackgroundColor(Color.argb(120, 100, 100, 100));
				} else {
					v.setBackgroundColor(Color.argb(120, 170, 170, 170)); //or whatever was original
				}

				return v;
			}

		};
		
		// Let this ListActivity display the contents of the cursor adapter.
		setListAdapter( this.adapter );
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
		//Intent i = new Intent( this, AddAnswerActivity.class );

		//Query the database for the necessary information
		Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/" + id );
		String[] projection = { ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
		Cursor cursor = getActivity().getContentResolver().query( huntUri, projection, null, null, null );

		//Retrieve the information from the database. 
		cursor.moveToFirst();	    
		//String name = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
		String descName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
		cursor.close();
		mCallback.onArticleSelected("fruit bat");

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);

		//Set the variables that will be used in the AdditemActivity
		//i.putExtra(ManagerMain.ITEM_NAME_TEXT, name);

		//i.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);

		//startActivity( i );

	}

	@Override
	public void onStart() {
		super.onStart();
		// When in two-pane layout, set the listview to highlight the selected list item
		// (We do this during onStart because at the point the listview is available.)
		if (getFragmentManager().findFragmentById(R.id.answers_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(this.getListView());
	}


}
