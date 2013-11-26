package edu.mines.freeganquestcaseysoto;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CopyOfManagerMain extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private SimpleCursorAdapter adapter;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int SHOW_LOC_ID = Menu.FIRST + 3;
	private String huntName;
	public static final String HUNT_NAME = "NameOfHunt";
	public static final String ITEM_NAME_TEXT = "NameOfItem";
	public static final String LOC_TEXT = "DueDate";
	public static final String DESC_TEXT = "Description";

	 OnHeadlineSelectedListener mCallback;
	    // The container Activity must implement this interface so the frag can deliver messages
	    public interface OnHeadlineSelectedListener {
	        /** Called by HeadlinesFragment when a list item is selected */
	        public void onArticleSelected(String position);
	    }
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//getActivity().setContentView(R.layout.hunts_frag);
		
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                R.layout.hunts_frag : R.layout.hunts_frag;
		
		
		//Set up ListView
		//this.getListView().setDividerHeight( 2 );
		//registerForContextMenu( getListView() );
				
				
        // Create an array adapter for the list view, using the Ipsum headlines array
        fillData();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    registerForContextMenu(this.getListView());
	}
	/*
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		 // We need to use a different list item layout for devices older than Honeycomb
        int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1 : android.R.layout.simple_list_item_1;

        // Create an array adapter for the list view, using the Ipsum headlines array
        fillData();
        
		//View view = inflater.inflate(R.layout.hunts_list,
		        //container, false);
		fillData();
		return view;
	}
	*/
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

	@Override
    public void onStart() {
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.items_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

	
	/**
	 * Inserts the hunt into the hunt Table.
	 * checks to see if there are now 2 hunt of the same name and deletes the last inserted hunt
	 */
	

	
	/**
	 * overriden function from listView that when clicked will open up the Item activity to show the hunts Items.
	 */
	@Override
	public void onListItemClick( ListView l, View v, int position, long id )
	{
		super.onListItemClick( l, v, position, id );
		//Intent i = new Intent( this, ItemActivity.class );
		Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + id );
		String[] projection = { ManagerHuntTable.COLUMN_NAME };

		//gets the uris for the same id, moves it to first position.
		Cursor cursor = getActivity().getContentResolver().query( huntUri, projection, null, null, null );
		String name= "";
		cursor.moveToFirst();	    
		name = cursor.getString( cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
		cursor.close();
		mCallback.onArticleSelected(name);
        
        // Set the item as checked to be highlighted when in two-pane layout
        getListView().setItemChecked(position, true);
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
			Cursor cursor2 = getActivity().getContentResolver().query( uri, projection2, null, null, null );
			String name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();
			this.huntName= name2;
			

			getActivity().getContentResolver().delete( uri, null, null );

			//get all homework associsated with this hunt and delete it.
			String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME };
			String[] querySelection = { this.huntName };
			//gets the uris for the same id, moves it to first position.
			uri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/");
			Cursor cursor = getActivity().getContentResolver().query( uri, projection, "hunt=?", querySelection, null );
			cursor.moveToFirst();
			for(int i=0; i < cursor.getCount(); ++i){
				String id =  cursor.getString(cursor.getColumnIndexOrThrow(ItemTable.COLUMN_ID));
				uri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" + id );
				getActivity().getContentResolver().delete( uri, null, null );
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
			cursor2 = getActivity().getContentResolver().query( uri, projection3, null, null, null );
			name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();
			this.huntName= name2;
			ManagerFragment.huntName = name2;
			Bundle args = new Bundle();
			args.putInt( "dialogID", 2 );
			args.putString( "prompt", getString( R.string.statement ) );
			
			Log.d("FREEGANQUEST: " , "Name before dialog: " + name2);
			InputDialogFragment dialog = new InputDialogFragment();
			dialog.setArguments( args );
			dialog.show( getActivity().getFragmentManager(), "Dialog" );
			return true;
		case SHOW_LOC_ID:
			info = (AdapterContextMenuInfo)item.getMenuInfo();
			uri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" + info.id );
			String[] projection4 = { ManagerHuntTable.COLUMN_NAME };

			//gets the uris for the same id, moves it to first position.
			cursor2 = getActivity().getContentResolver().query( uri, projection4, null, null, null );
			name2= "";
			cursor2.moveToFirst();	    
			name2 = cursor2.getString( cursor2.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ) );
			cursor2.close();
			Log.d("FREEQUEST: ", "name is: " + name2);

			Intent i = new Intent(getActivity(), LocationActivity.class);
			
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
		CursorLoader cursorLoader = new CursorLoader( getActivity(), FreeganContentProvider.CONTENT_URI, projection, null, null, null );
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
		this.adapter = new SimpleCursorAdapter( getActivity(), R.layout.list_row, null, from, to, 0 ){
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
	 * overrides the dialog fragment for inputting hunt. depending on eit or inserting. 
	 * @param dialogID : the id returned to see if it is an insert or edit.
	
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

