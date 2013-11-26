/**
 * Description: This class takes in user input for the item's name, due date, and description. 
 * 	Once the user submits this data it validates and normalizes the input and adds it to the database. 
 * 	The class handles adding new item as well as updating item.  
 *
 * @author Craig J. Soto II
 * @author Ben Casey
 */


/*
 * Web pages that helped contribute to writing the code for this class:
 * 
 * http://stackoverflow.com/questions/7200108/android-gettext-from-edittext-field
 * http://stackoverflow.com/questions/6021836/getting-listitem-data-from-a-list
 * http://stackoverflow.com/questions/18480633/java-util-date-format-conversion-yyyy-mm-dd-to-mm-dd-yyyy
 * http://stackoverflow.com/questions/9629636/get-todays-date-in-java
 * http://thinkandroid.wordpress.com/2010/01/09/simplecursoradapters-and-listviews/
 * http://stackoverflow.com/questions/18967790/listview-that-shows-multiple-columns-of-one-row-instead-of-multiple-rows
 * http://kahdev.wordpress.com/2010/09/27/android-using-the-sqlite-database-with-listview/
 */

package edu.mines.freeganquestcaseysoto;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class AddItemActivity extends Activity {

	private final static int DESC_MAX = 140; //used to limit the users description to 140 characters
	private boolean update = false; //used to help determine if item is being updated or not
	private String hwName = ""; //used for checking if name needs to be updated
	private String location = ""; //used for checking if date needs to be updated
	private String description = ""; //used for checking if description needs to be updated
	private String answerDisp = "word";

	/**
	 * The onCreate method retrieves any saved instances and sets the content view layout. It
	 * retrieves and sets the hunt name,item name, due date, and description (if present
	 * from the itemActivity, fills the respective TextViews.    
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);

		//Retrieve strings passed in from the itemActivity
		String message = getIntent().getStringExtra( ManagerMain.HUNT_NAME);
		if(getIntent().getStringExtra( ManagerMain.ITEM_NAME_TEXT) != null){
			hwName = getIntent().getStringExtra( ManagerMain.ITEM_NAME_TEXT);
		}
		location = getIntent().getStringExtra( ManagerMain.LOC_TEXT);
		description = getIntent().getStringExtra( ManagerMain.DESC_TEXT);

		//Get the TextView item to be updated
		TextView mhuntText = (TextView) findViewById(R.id.huntName);
		TextView hwNameText = (TextView) findViewById(R.id.itemNameInput);
		TextView dateText = (TextView) findViewById(R.id.locationInput);
		TextView descText = (TextView) findViewById(R.id.descriptionInput);

		//Set the TextView item to the new text form the itemActivity
		mhuntText.setText(message);

		//If itemActivity will be updating info, the hwName won't be empty nor will description
		//or date guaranteed (we normalize the info put into the db so we will always have this). We
		//also set the update value to true to follow update logic. 
		if(!hwName.equals("")){
			((TextView) hwNameText).setText(hwName);
			((TextView) descText).setText(description);
			((TextView) dateText).setText(location);
			update = true;
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
	
	/**
	 * The submit method retrieves the EditText content for the name, due date, and description from
	 * the activity. It also validates and normalizes the user input, updates or inserts the input,
	 * then finishes the activity.   
	 * 
	 * @param view - this is necessary for the button to interact with the activity
	 */
	public void submit(View view){
		//Retrieve the user input
		EditText nameInput = (EditText) findViewById(R.id.itemNameInput);
		EditText descriptionInput = (EditText) findViewById(R.id.descriptionInput);
		EditText dateInput = (EditText) findViewById(R.id.locationInput);

		String hwName = nameInput.getText().toString();
		String desc = descriptionInput.getText().toString();
		String loc = dateInput.getText().toString();
		String hunt = getIntent().getStringExtra( ManagerMain.HUNT_NAME);

		//Make sure the name and desc have content, if not give it generic information. 
		hwName = hwName.length() > 0 ? hwName : "Untitled";
		loc = loc.length() > 0 ? loc : "Unkown";
		desc = desc.length() > 0 ? desc : "None";

		//Trim the desc to 140 characters
		if(desc.length() > DESC_MAX) {
			desc = desc.substring(0, DESC_MAX);
		}

		//Call the respective method based on what the user is doing
		if(update){
			updateHW(hwName, loc, desc);
		} else {
			insertNewHW(hwName, loc, desc, hunt);
		}

		finish();
	}
		
	/**
	 * The updateHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it. 
	 * 
	 * @param name - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 */
	private void updateHW(String name, String loc, String desc) {
		int rowsUpdated = 0;
		
		//If the name/date/description was updated by the user it won't match the values that were passed
		//from itemActivity when the user clicked a item to be updated. In this case, updated
		//that item respectively. 
		ContentValues values = new ContentValues();
		if(!name.equals(hwName)){
			values.put( ItemTable.COLUMN_NAME, name );
			String[] selection = {hwName, location, description};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "name=? AND date=? AND desc=?", selection );
		}
		if(!loc.equals(location)){
			values.put( ItemTable.COLUMN_LOCATION, loc );
			String[] selection = {location, name, description};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "date=? AND name=? AND desc=?", selection );
		}
		if(!desc.equals(description)){
			values.put( ItemTable.COLUMN_DESCRIPTION, desc );
			String[] selection = {description, name, loc};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "desc=? AND name=? AND date=?", selection );
		}

		if(rowsUpdated == 0){
			Log.d("ADDitem", "No rows were updated");
		}
	}

	/**
	 * The insertNewHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it.   
	 * 
	 * @param name - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 * @param hunt - name of the hunt
	 */
	public void insertNewHW(String name, String loc, String desc, String hunt){
		ContentValues values = new ContentValues();
		values.put( ItemTable.COLUMN_NAME, name );
		values.put( ItemTable.COLUMN_LOCATION, loc );
		values.put( ItemTable.COLUMN_DESCRIPTION, desc);
		values.put( ItemTable.COLUMN_HUNT_NAME, hunt);
		values.put(ItemTable.COLUMN_DISPLAY, answerDisp);
		
		//Insert values into the item Table
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI_H, values );

		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME};
		String[] selection = {name};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_H, projection, "name=?", selection, ItemTable.COLUMN_ID + " DESC" );
		
		//If there were multiple entries remove the last insert then notify the user. 
		if(cursor.getCount() > 1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " + name +"!" , Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		cursor.close();
	}
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radBWord:
	            if (checked)
	                answerDisp = "word";
	            break;
	        case R.id.radBPic:
	            if (checked)
	                answerDisp = "picture";
	            break;
	    }
	}
}
