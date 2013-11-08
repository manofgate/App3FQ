/**
 * Description: This class takes in user input for the homework's name, due date, and description. 
 * 	Once the user submits this data it validates and normalizes the input and adds it to the database. 
 * 	The class handles adding new homework as well as updating homework.  
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class AddItemActivity extends Activity {

	private final static int DESC_MAX = 140; //used to limit the users description to 140 characters
	private boolean update = false; //used to help determine if homework is being updated or not
	private String hwName = ""; //used for checking if name needs to be updated
	private String date = ""; //used for checking if date needs to be updated
	private String description = ""; //used for checking if description needs to be updated

	/**
	 * The onCreate method retrieves any saved instances and sets the content view layout. It
	 * retrieves and sets the hunt name, homwork name, due date, and description (if present
	 * from the HomeworkActivity, fills the respective TextViews.    
	 * 
	 * @param savedInstanceState - a bundle of any saved instance values 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);

		//Retrieve strings passed in from the HomeworkActivity
		String message = getIntent().getStringExtra( ManagerMain.HUNT_NAME);
		/*hwName = getIntent().getStringExtra( MainActivity.HW_NAME_TEXT);
		date = getIntent().getStringExtra( MainActivity.DATE_TEXT);
		description = getIntent().getStringExtra( MainActivity.DESC_TEXT);*/

		//Get the TextView item to be updated
		//TextView mhuntText = (TextView) findViewById(R.id.huntNameEnd);
		TextView hwNameText = (TextView) findViewById(R.id.nameInput);
		TextView dateText = (TextView) findViewById(R.id.dateInput);
		TextView descText = (TextView) findViewById(R.id.descriptionInput);

		//Set the TextView item to the new text form the HomeworkActivity
		//mhuntText.setText(message);

		//If HomeworkActivity will be updating info, the hwName won't be empty nor will description
		//or date guaranteed (we normalize the info put into the db so we will always have this). We
		//also set the update value to true to follow update logic. 
		if(!hwName.equals("")){
			((TextView) hwNameText).setText(hwName);
			((TextView) descText).setText(description);
			((TextView) dateText).setText(date);
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
		EditText nameInput = (EditText) findViewById(R.id.nameInput);
		EditText descriptionInput = (EditText) findViewById(R.id.descriptionInput);
		EditText dateInput = (EditText) findViewById(R.id.dateInput);

		String hwName = nameInput.getText().toString();
		String desc = descriptionInput.getText().toString();
		String dueDate = dateInput.getText().toString();
		String hunt = getIntent().getStringExtra( ManagerMain.HUNT_NAME);

		//Make sure the name and desc have content, if not give it generic information. 
		hwName = hwName.length() > 0 ? hwName : "Untitled";
		desc = desc.length() > 0 ? desc : "None";

		//Trim the desc to 140 characters
		if(desc.length() > DESC_MAX) {
			desc = desc.substring(0, DESC_MAX);
		}

		//Validate that the date is in the correct format and in the future. 
		if(checkDate(dueDate)){
			//Normalize dueDate
			if(!dueDate.contains("-")){
				dueDate = dueDate.substring(0, 2) + "-" + dueDate.substring(2, 4) + "-" + dueDate.substring(4, 8);
			}
		} else {
			//Notify the user that the date was incorrect. 
			Toast toast = Toast.makeText(getApplicationContext(), "Incorrect date format. Set to today's date." , Toast.LENGTH_LONG);
			toast.show();

			//Set dueDate to today's date. 
			Calendar currentDate = Calendar.getInstance(); 
			SimpleDateFormat today = new SimpleDateFormat("MM-dd-yyyy", java.util.Locale.getDefault()); 
			dueDate = today.format(currentDate.getTime());
		}

		//Call the respective method based on what the user is doing
		if(update){
			updateHW(hwName, dueDate, desc);
		} else {
			insertNewHW(hwName, dueDate, desc, hunt);
		}

		finish();
	}

	/**
	 * The checkDate method checks that the input is a date, the date is in mmddyyyy format, and that
	 * it is in the future. If it doesn't meet this criteria, it will return false.  
	 * 
	 * @param dateInput - the user input date
	 * 
	 * @return correctInput - return if input is valid
	 */
	private boolean checkDate(String dateInput) {
		boolean correctInput = false;

		//Add hyphens to the dateInput for verifying
		if(dateInput.length() == 8){
			dateInput = dateInput.substring(0, 2) + "-" + dateInput.substring(2, 4) + "-" + dateInput.substring(4, 8);
		}

		if(dateInput.length() == 10){
			//Format that will be usedfor the date
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy", java.util.Locale.getDefault());

			//Get current date and convert it to a string
			Calendar currentDate = Calendar.getInstance();
			String dateNow = sdf.format(currentDate.getTime());

			//Parse user input date and today's date to Date objects then make sure user date not
			//before (can be today's date or future date) today's date. If any input is incorrect, 
			//catch the exception and don't set correctInput to true;
			try {
				Date dueDate = sdf.parse(dateInput);
				Date today = sdf.parse(dateNow);
				correctInput = !dueDate.before(today);
			} catch (ParseException e) {
				correctInput = false;
				Log.d("ADDHOMEWORKACTIVITY", "Error parsing dates." + e);
			}
		}

		return correctInput;
	}
	
	/**
	 * The updateHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it. 
	 * 
	 * @param name - name retrieved from Activity
	 * @param dueDate - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 */
	private void updateHW(String name, String dueDate, String desc) {
		int rowsUpdated = 0;
		
		//If the name/date/description was updated by the user it won't match the values that were passed
		//from HomeworkActivity when the user clicked a homework to be updated. In this case, updated
		//that item respectively. 
		ContentValues values = new ContentValues();
		if(!name.equals(hwName)){
			values.put( ItemTable.COLUMN_NAME, name );
			String[] selection = {hwName, date, description};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "name=? AND date=? AND desc=?", selection );
		}
		if(!dueDate.equals(date)){
			values.put( ItemTable.COLUMN_DATE, dueDate );
			String[] selection = {date, name, description};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "date=? AND name=? AND desc=?", selection );
		}
		if(!desc.equals(description)){
			values.put( ItemTable.COLUMN_DESCRIPTION, desc );
			String[] selection = {description, name, dueDate};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "desc=? AND name=? AND date=?", selection );
		}

		if(rowsUpdated == 0){
			Log.d("ADDHOMEWORK", "Now rows were updated");
		}
	}

	/**
	 * The insertNewHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it.   
	 * 
	 * @param view - this is necessary for the button to interact with the activity
	 */
	public void insertNewHW(String name, String date, String desc, String hunt){
		ContentValues values = new ContentValues();
		values.put( ItemTable.COLUMN_NAME, name );
		values.put( ItemTable.COLUMN_DATE, date );
		values.put( ItemTable.COLUMN_DESCRIPTION, desc);
		values.put( ItemTable.COLUMN_HUNT_NAME, hunt);
		
		//Insert values into the Homework Table
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI_H, values );

		//Verify if identical entries were inserted into the Homework Table 
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
}
