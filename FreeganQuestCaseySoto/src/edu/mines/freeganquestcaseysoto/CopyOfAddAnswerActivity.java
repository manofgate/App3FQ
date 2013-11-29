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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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
	
	OnFinishListener mCallback;
    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnFinishListener {
        /** Called by HeadlinesFragment when a list item is selected */
        public void onFinishSelected(String position);
    }
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
        
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
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
	

	
	 @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);

	        // This makes sure that the container activity has implemented
	        // the callback interface. If not, it throws an exception.
	        try {
	            mCallback = (OnFinishListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnFinishListener");
	        }
	    }
}
