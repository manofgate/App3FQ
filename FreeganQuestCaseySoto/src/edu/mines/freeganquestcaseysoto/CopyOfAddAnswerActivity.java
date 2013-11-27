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

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CopyOfAddAnswerActivity extends Fragment {

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
	/*@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_answer);

		//Retrieve strings passed in from the itemActivity
		//String message = getIntent().getStringExtra( ManagerMain.HUNT_NAME);
		

		//Get the TextView item to be updated
		//TextView mhuntText = (TextView) findViewById(R.id.huntName);
		

		//Set the TextView item to the new text form the itemActivity
		//mhuntText.setText(message);
	}
	*/
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            description = savedInstanceState.getString(HuntPlayFragment.DESCRIP);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_answer, container, false);
    }
	 @Override
	    public void onStart() {
	        super.onStart();
	        //Log.d("FQ::COIA", "hunt name is" + huntName);
	        // During startup, check if there are arguments passed to the fragment.
	        // onStart is a good place to do this because the layout has already been
	        // applied to the fragment at this point so we can safely call the method
	        // below that sets the article text.
	        updateArticleView(description);
	        Bundle args = getArguments();
	        if (args != null) {
	            // Set article based on argument passed in
	            updateArticleView(args.getString(HuntPlayFragment.DESCRIP));
	        } else if (description != "") {
	        	
	            // Set article based on saved instance state defined during onCreateView
	            updateArticleView(description);
	        }
	    }
	 
	 
	    public void updateArticleView(String position) {
	    	description = position;
	    	
	        //fillData();
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
		EditText nameInput = (EditText) getActivity().findViewById(R.id.answerWord);

		String hwName = nameInput.getText().toString();
		String hunt = HuntPlayFragment.huntName;

		//Make sure the name and desc have content, if not give it generic information. 
		
		//Call the respective method based on what the user is doing
		if(update){
			updateHW(hwName);
		} else {
			insertNewHW(hwName, hunt);
		}

		//finish();
	}
		
	/**
	 * The updateHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it. 
	 * 
	 * @param name - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 */
	private void updateHW(String name) {
		int rowsUpdated = 0;
		
		//If the name/date/description was updated by the user it won't match the values that were passed
		//from itemActivity when the user clicked a item to be updated. In this case, updated
		//that item respectively. 
		ContentValues values = new ContentValues();
		if(!name.equals(hwName)){
			values.put( ItemTable.COLUMN_NAME, name );
			String[] selection = {hwName, location, description};
			rowsUpdated = rowsUpdated + getActivity().getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, values, "name=? AND date=? AND desc=?", selection );
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
	public void insertNewHW(String name, String hunt){
		ContentValues values = new ContentValues();
		values.put( ItemTable.COLUMN_NAME, name );
		values.put( ItemTable.COLUMN_HUNT_NAME, hunt);
		values.put(ItemTable.COLUMN_DISPLAY, answerDisp);
		
		//Insert values into the item Table
		getActivity().getContentResolver().insert( FreeganContentProvider.CONTENT_URI_H, values );

		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME};
		String[] selection = {name};
		Cursor cursor = getActivity().getContentResolver().query( FreeganContentProvider.CONTENT_URI_H, projection, "name=?", selection, ItemTable.COLUMN_ID + " DESC" );
		
		//If there were multiple entries remove the last insert then notify the user. 
		if(cursor.getCount() > 1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_H + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) );
			getActivity().getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Have already added " + name +"!" , Toast.LENGTH_LONG);
			toast.show();
			//finish();
		}
		cursor.close();
	}
	
}
