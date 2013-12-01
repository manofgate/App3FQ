package edu.mines.freeganquestcaseysoto;

/**
 * Description: This is the MainActivity class. It is the starting point of the app and will direct the app through
 *  the other activities as needed.
 * 
 * Description of intermediate submission. There is a manager mode button in the action bar. Once clicked, it will bring up managerMain
 *  which is a list of hunts. Can click on add button and it does bring up an inputDialog. This adds hunts. Can click on each hunt and add
 *  items to it. The addItemActivity allows to add name, location, description, and weather the answer should be word or picture.
 *  Also can long tap on each hunt and can delete it, edit it, or show the locations of it.
 *  
 * Description of final submission. The player can now play a hunt, submit answers, and view results. The player can take pictures and enter
 * in text for their answers.  
 * 
 * Documentation Statement: We worked on this Android App all on our own. We did not receive 
 * 	any help on this project from any other student enrolled or not enrolled in the CSCI498 
 * 	class. 
 * 
 * @author Ben Casey
 * @author Craig Soto II
 * 
 * point distribution: Ben - 57% : Craig - 43%
 */
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, InputDialogFragment.Listener{

	private Spinner mHunts;
	private SimpleCursorAdapter adapter;
	private String hName = "";
	public static final String HUNT_NAME = "NameofHunt";
	public static final String ABOUT_INFO = "\t \tTeam 2:42 \n Authors: \n \t Criag S. --> Software E. "
			+ "\n	\t Ben C. --> Software E. , Marketing \n \n \t \t Version 1.5 \n \t 11/29/13";
	public static final String HELP_INFO = "Getting Started:\n To get started select a Hunt from the drop down, then click \"Start\" \n\n"
			+ "Adding Hunt:\n To add a Hunt, start Manager Mode by clicking on the Manager Folder at the top.\n\n"
			+ "See Results:\n To see the results of a hunt, select a Hunt from the list, then click \"Results\"\n\n";
	public static final String MANAGER_HELP_INFO = "Going Back to Home Screen: \n When in Manager Mode, press the back button to go to Home Screen.\n\n" 
			+ "Adding a Hunt: \n To add a Hunt, click the \"Add Hunt\" button. \n Enter the name of the Hunt and click \"Done\".\n\n"
			+ "Adding Items to a Hunt: \n Select a Hunt from the list of Hunts. If none are present add a new Hunt. \n To add an Item, click the \"Add Item\" button. \n Enter in the Item's name, location, and description and click \"Submit\". \n\n"
			+ "Edit/Delete Hunt: \n Press and hold the Hunt you wish to edit or delete. \n Select the option from the list. \n\n" 
			+ "Edit Item: \nPress the Item you wish to edit. \n Make changes, then click \"Submit\". \n\n"
			+"Delete Item: \nPress and hold the Item you wish to delete.\nSelect the option from the list.";
	public static final String PLAYER_HELP_INFO = "";
	private ArrayList<String> arrayList1 = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHunts = (Spinner)findViewById( R.id.selectHunts );

		ArrayList<String> arrayList1 = new ArrayList<String>();

		arrayList1.add("Select a Hunt...");

		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, null, null, ManagerHuntTable.COLUMN_ID + " ASC" );
		//cursor.moveToFirst();
		for(int i=0; i < cursor.getCount(); i++){
			cursor.moveToPosition(i);
			hName = cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ));
			arrayList1.add(hName);
		}
		cursor.close();

		//arrayList1.add("Delhi");
		ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,arrayList1);
		mHunts.setAdapter(adp);

		mHunts.setVisibility(View.VISIBLE);
		//Set listener Called when the item is selected in spinner
		mHunts.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,int position, long arg3) {
				//String huntN = "The quest is " + parent.getItemAtPosition(position).toString();
				//Toast.makeText(parent.getContext(), city, Toast.LENGTH_LONG).show();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	@Override
	protected void onResume()
	{
		super.onResume(); // Must do this or app will crash!
		arrayList1.clear();
		arrayList1.add("Select a Hunt...");

		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, null, null, ManagerHuntTable.COLUMN_ID + " ASC" );
		//cursor.moveToFirst();
		for(int i=0; i < cursor.getCount(); i++){
			cursor.moveToPosition(i);
			hName = cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME ));
			arrayList1.add(hName);
		}
		cursor.close();

		//arrayList1.add("Delhi");
		ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,arrayList1);
		mHunts.setAdapter(adp);
	}


	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
		case R.id.action_manage:
		{
			//Intent i = new Intent(this, ManagerMain.class);
			Intent i = new Intent(this, ManagerFragment.class);
			startActivity(i);

			return true;
		}
		case R.id.about_settings:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("About");
	        builder.setMessage(ABOUT_INFO);
	        builder.setPositiveButton("OK", null);
	        AlertDialog dialog = builder.show();
	        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
	        messageText.setGravity(Gravity.CENTER);
	        return true;
		}
		case R.id.help_settings: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Help");
	        builder.setMessage(HELP_INFO);
	        builder.setPositiveButton("OK", null);
	        AlertDialog dialog = builder.show();
	        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
	        messageText.setGravity(Gravity.CENTER);
	        return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onStartHunt(View view){
		Intent i = new Intent(this, HuntPlayFragment.class);
		String hName =arrayList1.get(mHunts.getSelectedItemPosition());
		i.putExtra(HUNT_NAME, arrayList1.get(mHunts.getSelectedItemPosition()));
		if(!hName.equals("Select a Hunt...")){
			startActivity(i);
			finish();
		}
		
	}
	
	public void onResults(View view){
		Intent i = new Intent(this, ResultsActivity.class);
		String hName = arrayList1.get(mHunts.getSelectedItemPosition());
		i.putExtra(HUNT_NAME, arrayList1.get(mHunts.getSelectedItemPosition()));
		if(!hName.equals("Select a Hunt...")){
			startActivity(i);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mm, menu);
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public void onInputDone(int dialogID, String input) {
	}

	@Override
	public void onInputCancel(int dialogID) {
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		this.adapter.swapCursor( arg1 );

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		this.adapter.swapCursor( null );

	}

}
