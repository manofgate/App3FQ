/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mines.freeganquestcaseysoto;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HuntPlayFragment extends FragmentActivity 
implements CopyOfHuntActivity.OnHeadlineSelectedListener, CopyOfAddAnswerActivity.OnFinishListener {

	public static String huntName;
	public static String desc;
	private long startTime = 0L;
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;
	private boolean onAnswer = false;
	private TextView timerValue;
	public static final String DESCRIP = "description";
	private Handler customHandler = new Handler();

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			timerValue.setText(getTime(timeInMilliseconds, timeSwapBuff, updatedTime));
			customHandler.postDelayed(this, 0);
		}
	};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        huntName = getIntent().getStringExtra( MainActivity.HUNT_NAME);
        setContentView(R.layout.hunts_list_player);
      //Get/set the huntName in the activity
      		
      		Log.d("FQ::HP ", "huntName here is " +huntName);
      		//TextView mhuntText = (TextView)findViewById(R.id.huntName);
      		//mhuntText.setText(huntName);
      		 
      		timerValue = (TextView) findViewById(R.id.timeView);
      		startTime = SystemClock.uptimeMillis();
      		customHandler.postDelayed(updateTimerThread, 0);
      		Log.d("HUNT_PLAY", "we are the timer");
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_hunts) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            Log.d("HUNT_PLAY", "we are above the new");
            // Create an instance of ExampleFragment
            CopyOfHuntActivity firstFragment = new CopyOfHuntActivity();
            Log.d("NUNT_PLAY", "we are below this spot");
            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_hunts, firstFragment).commit();
        }
        else{
        	findViewById(R.id.answers_fragment).setVisibility(View.GONE);
        	
        }
       
    }

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
			Intent i = new Intent(this, HuntPlayFragment.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			return true;
		}
		case R.id.about_settings:
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About");
			builder.setMessage(MainActivity.ABOUT_INFO);
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		case R.id.help_settings: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Help");
			builder.setMessage(MainActivity.MANAGER_HELP_INFO);
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if(!onAnswer){
			Toast.makeText(getApplicationContext(), "Must finish Quest before going back", Toast.LENGTH_LONG).show();
		}
		else {
			super.onBackPressed();
			onAnswer = false;
		}
	}

	public void onArticleSelected(String position) {
		// The user selected the headline of an article from the HeadlinesFragment
		desc = position;
		onAnswer = true;
		Log.d("FQ:HUNT_PLAY " , "in the on ariticleSelected " + desc);
		// Capture the article fragment from the activity layout
		CopyOfAddAnswerActivity articleFrag = (CopyOfAddAnswerActivity)
				getSupportFragmentManager().findFragmentById(R.id.answers_fragment);


		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...
			Log.d("FQ: MF", "here in the articleUpdate");
			// Call a method in the ArticleFragment to update its content
			findViewById(R.id.answers_fragment).setVisibility(View.VISIBLE);
			articleFrag.updateArticleView(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and must swap frags...

			// Create fragment and give it an argument for the selected article
			CopyOfAddAnswerActivity newFragment = new  CopyOfAddAnswerActivity();
			Bundle args = new Bundle();

			args.putString( HuntPlayFragment.DESCRIP, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_hunts, newFragment);
			transaction.addToBackStack("top");

			// Commit the transaction
			transaction.commit();
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		//Sets the respective values for game information
		startTime = savedInstanceState.getLong("START_TIME");
	}

	public void onDialog(View view){
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);
		startActivity(i);		

		String finalTime = getTime(SystemClock.uptimeMillis() - startTime, 0L, 0L);

		insertTimer(finalTime);

		finish();
	}
	/**
	 * Inserts the hunt into the hunt Table.
	 * checks to see if there are now 2 hunt of the same name and deletes the last inserted hunt
	 */
	public void insertTimer(String finalTime){
		ContentValues values = new ContentValues();

		values.put( TimerTable.COLUMN_HUNT_NAME, huntName );
		values.put(TimerTable.COLUMN_TIME, finalTime);

		String[] projection = { TimerTable.COLUMN_ID, TimerTable.COLUMN_HUNT_NAME, TimerTable.COLUMN_TIME};
		String[] selection = {huntName, finalTime};
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI_T, values );

		//checks to see if that hunt name has already been added
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_T, projection, "name=?", selection, TimerTable.COLUMN_ID + " DESC" );
		if(cursor.getCount() >1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_T + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( TimerTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +huntName+" hunt!" , Toast.LENGTH_LONG);
			toast.show();
			//fillData();
		}
		cursor.close();

	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		//Save the game scores, round scores, and tosses amounts for the respective teams
		savedInstanceState.putLong("START_TIME", startTime);
	}
	private String getTime(long milliTime, long locTimeSwapBuff, long locUpdatedTime ){
		String time = "";

		locUpdatedTime = locTimeSwapBuff + milliTime;
		int secs = (int) (locUpdatedTime / 1000);
		int mins = secs / 60;
		secs = secs % 60;
		int milliseconds = (int) (locUpdatedTime % 1000);

		time = "" + mins + ":"
				+ String.format("%02d", secs) + ":"
				+ String.format("%03d", milliseconds);

		return time;
	}

	
	public void onFinishSelected(String position) {
		
		Log.d("FQ::HP " , "In the onFinishLisetner");
		//CopyOfHuntActivity articleFrag = (CopyOfHuntActivity)
				//getSupportFragmentManager().findFragmentById(R.id.fragment_hunts);


		/*if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...
			Log.d("FQ: MF", "here in the articleUpdate");
			// Call a method in the ArticleFragment to update its content
			articleFrag.updateArticleView(position);

		} else {*/
			// If the frag is not available, we're in the one-pane layout and must swap frags...

			//Create fragment and give it an argument for the selected article
		Log.d("FQ::HP " , "In the above onFinishLisetner");
		 getSupportFragmentManager().popBackStack();
			Log.d("FQ::HP " , "In the under onFinishLisetner");
			
		//}
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
		Log.d("FQ::AAA", "in the submit");
		EditText nameInput = (EditText) findViewById(R.id.answerWord);

		String hwName = nameInput.getText().toString();
		String hunt = HuntPlayFragment.huntName;

		//Make sure the name and desc have content, if not give it generic information. 
		
		//Call the respective method based on what the user is doing
		//if(update){
			//updateAnswer(hwName);
		//} else {
			//insertNewAnswer(hwName, hunt);
		//}
		onFinishSelected(hunt);
		
	}
		
	/**
	 * The updateHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it. 
	 * 
	 * @param name - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 */
	/*
	private void updateAnswer(String name) {
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
	*/

	/**
	 * The insertNewHW method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it.   
	 * 
	 * @param name - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 * @param hunt - name of the hunt
	 */
	public void insertNewAnswer(String name, String hunt){
		ContentValues values = new ContentValues();
		values.put( ItemTable.COLUMN_NAME, name );
		values.put( ItemTable.COLUMN_HUNT_NAME, hunt);
		//values.put(ItemTable.COLUMN_DISPLAY, answerDisp);
		
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
			//finish();
		}
		cursor.close();
	}
	
}