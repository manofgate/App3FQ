/**
 * Description: This class fragment takes in the name of the hunt. then determines if it should be in 1 pane or two pane.
 * Then it calls the hunt activity which displays the list of items for that hunt. It also does all the button interaction,
 * such as finishing the hunt, submitting an answer, and doing the timer.
 *
 * @author Craig J. Soto II
 * @author Ben Casey
 */
package edu.mines.freeganquestcaseysoto;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HuntPlayFragment extends FragmentActivity implements CopyOfHuntActivity.OnHeadlineSelectedListener, CopyOfAddAnswerActivity.OnFinishListener {

	public static String huntName;
	public static String desc;
	private long startTime = 0L;
	private long timeInMilliseconds = 0L;
	private long timeSwapBuff = 0L;
	private long updatedTime = 0L;
	private static boolean onAnswer = false;
	private TextView timerValue;
	public static final String DESCRIP = "description";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private Handler customHandler = new Handler();
	private File storageDir;
	private String name;
	private String desciption;
	private String huntName_q;
	private String loc;
	

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			onAnswer = false;
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			timerValue.setText(getTime(timeInMilliseconds, timeSwapBuff, updatedTime));
			customHandler.postDelayed(this, 0);
		}
	};
	private String mCurrentPhotoPath;
	private int CAMERA_REQUEST = 10;
	

	private boolean IsThereAnAppToTakePictures()
	{
	    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    IList<ResolveInfo> availableActivities = PackageManager.QueryIntentActivities(intent, PackageInfoFlags.MatchDefaultOnly);
	    return availableActivities != null && availableActivities.Count > 0;
		*/
		return false;
	}

	private void CreateDirectoryForPictures()
	{
	    /*_dir = new File(Environment.GetExternalStoragePublicDirectory(Environment.DirectoryPictures), "CameraAppDemo");
	    if (!_dir.Exists())
	    {
	        _dir.Mkdirs();
	    }*/
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("FREEGAN::HUNT", "this is the onCreate()" );
		super.onCreate(savedInstanceState);
		huntName = getIntent().getStringExtra( MainActivity.HUNT_NAME);
		setContentView(R.layout.hunts_list_player);
		//Get/set the huntName in the activity

		TextView mhuntText = (TextView)findViewById(R.id.huntName);
		mhuntText.setText(huntName);

		timerValue = (TextView) findViewById(R.id.timeView);
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);

		// Check whether the activity is using the layout version with
		// the fragment_hunts FrameLayout. If so, we must add the first fragment
		if (findViewById(R.id.fragment_hunts) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			// Create an instance of firstFragment
			CopyOfHuntActivity firstFragment = new CopyOfHuntActivity();

			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_hunts' FrameLayout
			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_hunts, firstFragment).commit();
		}
		else{
			//allows the answer fragment to be invisble for two pane at the begining
			findViewById(R.id.answers_fragment).setVisibility(View.GONE);
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mm, menu);
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * for the menu. depending on which is chosen, the help, about, or settings, it will display appropriate fragment.
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
		case R.id.action_manage:
		{
			Intent i = new Intent(this, ManagerFragment.class);
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
			return true;
		}
		case R.id.help_settings: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Help");
			builder.setMessage(MainActivity.HELP_INFO);
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

	/**
	 * To stop the user from backing up out of the hunt unless they hit the finish button.
	 */
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
	


	/** method when clicked on in the list of items for the hunt with display the other fragment to add your answer
	 * @Param position - position is the description of the item to match it up
	 */
	public void onArticleSelected(String position, String itemName, String locName) {
		// The user selected the headline of an article from the HeadlinesFragment
		desc = position;
		name = itemName;
		loc = locName;
		// Capture the article fragment from the activity layout
		CopyOfAddAnswerActivity articleFrag = (CopyOfAddAnswerActivity)
				getSupportFragmentManager().findFragmentById(R.id.answers_fragment);

		/*//Query the database for the necessary information
		Uri itemUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/" + position );
		String[] projection = { ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_DESCRIPTION };
		Cursor cursor = getContentResolver().query( itemUri, projection, null, null, null );

		//Retrieve the information from the database. 
		cursor.moveToFirst();	    
		name = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME ) );
		desciption = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION ) );
		huntName_q = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_HUNT_NAME ) );
		loc = cursor.getString( cursor.getColumnIndexOrThrow( ItemTable.COLUMN_LOCATION ) );
		cursor.close();*/

		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...

			// Call a method in the ArticleFragment to update its content
			findViewById(R.id.answers_fragment).setVisibility(View.VISIBLE);
			articleFrag.updateArticleView(position);
		} else {
			// If the frag is not available, we're in the one-pane layout and must swap frags...
			onAnswer = true;
			// Create fragment and give it an argument for the selected article
			CopyOfAddAnswerActivity newFragment = new CopyOfAddAnswerActivity();
			Bundle args = new Bundle();

			args.putString( HuntPlayFragment.DESCRIP, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_hunts view with this fragment,
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
		onAnswer = savedInstanceState.getBoolean("ONANSWER");
		//Log.d("FREEGAN::FRAGEMNET", "We are on restored state");
	}
	

	public void onDialog(View view){
		String finalTime = getTime(SystemClock.uptimeMillis() - startTime, 0L, 0L);

		insertTimer(finalTime);

		Intent i = new Intent(this, MainActivity.class);
		i.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);
		startActivity(i);	

		finish();
	}
	/**
	 * inserts the timer into it's timer table for completion of the hunt. Used to check the time for results
	 * @Param finalTime - final time to be inserted into the table
	 */
	public void insertTimer(String finalTime){
		Log.d("TIME", finalTime);
		onAnswer = true;
		ContentValues values = new ContentValues();
		values.put( TimerTable.COLUMN_HUNT_NAME, huntName );
		values.put( TimerTable.COLUMN_TIME, finalTime);

		//Insert values into the item Table
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI_T, values );

		//Verify if identical entries were inserted into the item Table 
		String[] projection = { TimerTable.COLUMN_ID, TimerTable.COLUMN_HUNT_NAME};
		String[] selection = {huntName};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_T, projection, "hunt=?", selection, TimerTable.COLUMN_ID + " DESC" );

		//If there were multiple entries remove the last insert then notify the user. 
		if(cursor.getCount() >1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_T + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( TimerTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +huntName+" hunt!" , Toast.LENGTH_LONG);
			toast.show();
		}
		cursor.close();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		//Save the game scores, round scores, and tosses amounts for the respective teams
		savedInstanceState.putLong("START_TIME", startTime);
		savedInstanceState.putBoolean("ONANSWER", onAnswer);
	}

	/**
	 * gets the current time and formats it for display
	 * @param milliTime - current time in milli seconds
	 * @param locTimeSwapBuff
	 * @param locUpdatedTime
	 * @return
	 */
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

	/**
	 * When the user clicks submit this will fix the answer fragment to be right text and or not visible
	 * @Param position - 
	 */
	public void onFinishSelected(String position) {

		//CopyOfHuntActivity articleFrag = (CopyOfHuntActivity)
		CopyOfAddAnswerActivity articleFrag = (CopyOfAddAnswerActivity)
				getSupportFragmentManager().findFragmentById(R.id.answers_fragment);


		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...
			EditText mText = (EditText) findViewById(R.id.answerWord);
			mText.setText("");
			// Call a method in the ArticleFragment to update its content
			findViewById(R.id.answers_fragment).setVisibility(View.GONE);
			articleFrag.updateArticleView(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and must swap frags...

			//Create fragment and give it an argument for the selected article
			getSupportFragmentManager().popBackStack();
			Log.d("FQ::HP " , "In the under onFinishLisetner");

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
		Log.d("FQ::AAA", "in the submit");
		EditText answerEditView = (EditText) findViewById(R.id.answerWord);

		String answer = answerEditView.getText().toString();
		String hunt = HuntPlayFragment.huntName;

		//Make sure the name and desc have content, if not give it generic information. 

		//Call the respective method based on what the user is doing
		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_HUNT_NAME};
		String[] selection = {answer, hunt};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, "ans=? AND hunt=?", selection, ItemTable.COLUMN_ID + " DESC" );

		//If there were multiple entries remove the last insert then notify the user. 
		if(cursor.getCount() > 1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
		} else {
			insertNewAnswer(answer, hunt);
		}
		cursor.close();
		Log.d("Freegan::Hunt", "The stack count: " + getSupportFragmentManager().getBackStackEntryCount());
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
	 * The insertNewAnswer method checks to see if the name, due date, or description needs to be updated. 
	 * If any of them need to be updated then update it.   
	 * 
	 * @param answer - name retrieved from Activity
	 * @param loc - date retrieved from Activity
	 * @param desc - description retrieved from Activity
	 * @param hunt - name of the hunt
	 */
	public void insertNewAnswer(String answer, String hunt){
		ContentValues values = new ContentValues();
		values.put( ItemTable.COLUMN_ANSWER, answer );
		//values.put( ItemTable.COLUMN_HUNT_NAME, huntName_q);
		//values.put( ItemTable.COLUMN_DESCRIPTION, desciption);
		values.put( ItemTable.COLUMN_LOCATION, loc);
		values.put( ItemTable.COLUMN_NAME, name);
		
		int rowsUpdated =0;
		//ContentValues values = new ContentValues();
		
			//values.put( ItemTable.COLUMN_NAME, name );
			String[] selection = {name, loc, hunt};
			rowsUpdated = rowsUpdated + getContentResolver().update( FreeganContentProvider.CONTENT_URI_I, values, "name=? AND loc=? AND hunt=?", selection );
		


		if(rowsUpdated == 0){
			Log.d("ADDitem", "No rows were updated");
		}
		//Insert values into the item Table
		/*getContentResolver().insert( FreeganContentProvider.CONTENT_URI_I, values );
		Log.d("FQ::HPF", "this is in InsertNewAnsswer");
		//Verify if identical entries were inserted into the item Table 
		String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_NAME};
		String[] selection = {answer, desciption, huntName_q, loc, name};
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, "ans=? AND desc=? AND hunt=? AND loc=? AND name=?", selection, ItemTable.COLUMN_ID + " DESC" );

		//If there were multiple entries remove the last insert then notify the user. 
		if(cursor.getCount() > 1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " + answer +"!" , Toast.LENGTH_LONG);
			toast.show();
			//finish();
		}
		cursor.close();
		*/
	}
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = 
	        new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0));
	    String imageFileName = "FQ" + timeStamp + "_";
	    File image = File.createTempFile(
	        imageFileName, 
	        JPEG_FILE_SUFFIX, 
	        getObbDir()
	    );
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	
	public void dispatchCamera(View view) {
		/*storageDir = createImageFile();
		  Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(storageDir));
		    startActivityForResult(takePictureIntent, 0);*/
		
		Intent cameraIntent= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
		 startActivityForResult(cameraIntent, CAMERA_REQUEST );  
		
	  
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("FREEGAN::QHPF", "In the activityResult");
	    if (requestCode == CAMERA_REQUEST) {   
	    	Log.d("FREEGAN::QHPF", "In the camera request");
	    	Bitmap photo = (Bitmap) data.getExtras().get("data"); 
	    	 ImageView mImageView = (ImageView) findViewById(R.id.captureView);
	    	int targetW = mImageView.getWidth();
	 	    int targetH = mImageView.getHeight();

	 	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	 	    bmOptions.inJustDecodeBounds = true;
	 	    //BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	 	    /*int photoW = bmOptions.outWidth;
	 	    int photoH = bmOptions.outHeight;
	 	  
	 	    // Determine how much to scale down the image
	 	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	 	  
	 	    // Decode the image file into a Bitmap sized to fill the View
	 	    bmOptions.inJustDecodeBounds = false;
	 	    bmOptions.inSampleSize = scaleFactor;
	 	    bmOptions.inPurgeable = true;
	 	  */
	 	   // Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	 	    mImageView.setImageBitmap(photo);
	    	}
	    
	    // make it available in the gallery
	   /* Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri contentUri = Uri.fromFile(storageDir);
	    mediaScanIntent.setData(contentUri);
	    sendBroadcast(mediaScanIntent);
	   
	    // display in ImageView. We will resize the bitmap to fit the display
	    // Loading the full sized image will consume to much memory 
	    // and cause the application to crash.
	    // Get the dimensions of the bitmap
	    */
	   

	}

}