/**
 * Description: This class displays the list of homework based on which course was selected. The 
 * 	user will be able to either add a homework (touching the add button), edit a homework (touching
 * 	the homework in the list), and deleting a homework (long press homework in list). 
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
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint("NewApi")
public class ItemActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

	private static final int DELETE_ID = Menu.FIRST + 1; //integer id for the delete option for long press
	private SimpleCursorAdapter adapter; //helps assist with database interactions 
	public static final String HW_NAME = "NameOfHomework";
	private String courseName; //receives and passes on course name from the MainActivity

	/**
	 * The onCreate method retrieves any saved instances and sets the content view layout. It
	 * retrieves and sets the course name from the MainActivity, fills the homework table with the 
	 * respective course's homework, and sets up the context menu.   
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values 
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.item_list );

		//Get/set the courseName in the activity
		courseName = getIntent().getStringExtra( ManagerMain.HUNT_NAME);
		TextView mCourseText = (TextView)findViewById(R.id.courseNameMid);
		mCourseText.setText(courseName);

		//Set up ListView
		this.getListView().setDividerHeight( 2 );
		registerForContextMenu( getListView() );

		//Fill the Listview table
		fillData();
	}

	/**
	 * The onCreateLoader loads the homework specific to the course. This makes sure we only see the 
	 * homework for that course and no others. 
	 * 
	 * @param arg0 - unused but needed for abstract method
	 * @param arg1 - unused but needed for abstract method
	 * 
	 * @return cursorLoader - the homework information from the database
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.d("SchoolScheduler::OnlyCourse", "This Course name is "+ courseName);

		//Retrieve homework info from database
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_DATE, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_COURSE_NAME };
		String[] selection = {courseName};
		CursorLoader cursorLoader = new CursorLoader( this, SchedulerContentProvider.CONTENT_URI_H, projection, "course=?", selection, null );

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
	 * The fillData method fills the ListView with the homework. 
	 */
	private void fillData() {
		//Fields in the DB from which we map 
		String[] from = new String[] { ItemTable.COLUMN_NAME, ItemTable.COLUMN_DATE, ItemTable.COLUMN_DESCRIPTION };

		// Fields on the UI to which we map
		int[] to = new int[] { R.id.hwName, R.id.date, R.id.descrption };

		// Ensure a loader is initialized and active.
		getLoaderManager().initLoader( 0, null, this );

		// Note the last parameter to this constructor (zero), which indicates the adaptor should
		// not try to automatically re-query the data ... the loader will take care of this.
		this.adapter = new SimpleCursorAdapter( this, R.layout.item_list_row, null, from, to, 0 ){
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
	 * The addHomeworkToList method starts the AddHomeworkActivity. It also sets the needed elements
	 * used in that activity. 
	 * 
	 * @param view - this is necessary for the button to interact with the activity
	 */
	public void addHomeworkToList(View view) {
		Intent intent = new Intent(this, AddItemActivity.class);
		intent.putExtra(ManagerMain.HUNT_NAME, courseName);
		//Set these to empty strings to prevent null point exception and prevent filling changeable
		//elements in the next activity. 
		/*intent.putExtra(MainActivity.HW_NAME_TEXT, "");
		intent.putExtra(MainActivity.DATE_TEXT, "");
		intent.putExtra(MainActivity.DESC_TEXT, "");*/
		startActivity(intent);
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
		//When the delete option is selected delete the homework row from the db
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			Uri uri = Uri.parse( SchedulerContentProvider.CONTENT_URI_H + "/" + info.id );
			getContentResolver().delete( uri, null, null );
			fillData();
			return true;
		}
		return super.onContextItemSelected( item );
	}

	/**
	 * The onListItemClick method retrieves the information from the list, queries the database, 
	 * sets the respective variables with that info, then starts the AddHomeworkActivity for editing
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

		//Get the AddHomeworkActivity intent
		Intent i = new Intent( this, AddItemActivity.class );

		//Query the database for the necessary information
		Uri courseUri = Uri.parse( SchedulerContentProvider.CONTENT_URI_H + "/" + id );
		String[] projection = { ItemTable.COLUMN_NAME, ItemTable.COLUMN_DATE, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_COURSE_NAME };
		Cursor cursor = getContentResolver().query( courseUri, projection, null, null, null );

		//Retrieve the information from the database. 
		cursor.moveToFirst();	    
		String name = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
		String date = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DATE ) ).replace("-", "");
		String desc = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
		String courseName = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_COURSE_NAME ) );
		cursor.close();

		//Set the variables that will be used in the AddHomeworkActivity
		/*i.putExtra(MainActivity.HW_NAME_TEXT, name);
		i.putExtra(MainActivity.DATE_TEXT, date);
		i.putExtra(MainActivity.DESC_TEXT, desc);
		i.putExtra(MainActivity.COURSE_MNAME, courseName);*/

		startActivity( i );
	}
}
