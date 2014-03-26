/**
 * This class is for the manager main fragment. It will hide the correct buttons, addItem or AddHunt depending on which fragment you are in.
 * It is the main interaction for the buttons:  addHunt, addItem. Depending on which pane it is in, it will decide how it should be displayed.
 *	@author Ben
 *@author Craig
 */
package edu.mines.freeganquestcaseysoto;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class ManagerFragment extends FragmentActivity 
implements CopyOfManagerMain.OnHeadlineSelectedListener, InputDialogFragment.Listener, ConfirmDialogFragment.Listener {

	public static String huntName;

	 NfcAdapter mNfcAdapter;
	    // Flag to indicate that Android Beam is available
	 boolean mAndroidBeamAvailable  = false;
	 
	 // Instance that returns available files from this app
	 

		private NdefMessage mNdefMessage;
	    
	    /**
	    
	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hunts_list_frag);
		findViewById(R.id.addItemButton).setVisibility(View.GONE);
		// Check whether the activity is using the layout version with
		// the fragment_container FrameLayout. If so, we must add the first fragment
		if (findViewById(R.id.fragment_titles) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}
			Log.d("MANAGER_FRAGEMENT", "we are above the new");
			// Create an instance of ExampleFragment
			CopyOfManagerMain firstFragment = new CopyOfManagerMain();
			Log.d("MANAGER_FRAGEMENT", "we are below this spot");
			// In case this activity was started with special instructions from an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_titles, firstFragment).commit();
		}
		
		
		// NFC isn't available on the device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
        	
            /*
             * Disable NFC features here.
             * For example, disable menu items or buttons that activate
             * NFC-related features
             */
            
        // Android Beam file transfer isn't supported
        } else if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // If Android Beam isn't available, don't continue.
            mAndroidBeamAvailable = false;
            /*
             * Disable Android Beam file transfer features here.
             */
            
        // Android Beam file transfer is available, continue
        } else {
        	ArrayList<String> hunts = new ArrayList<String>();
        	mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        	String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME, ManagerHuntTable.COLUMN_ORIGIN_USER};
        	String[] selection = {MainActivity.USERN};
        	Log.d("FREEGAN::MF", "After proj and select");
        	//checks to see if that hunt name is already in database and adds if not. 
        	//Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
        	Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection,null, null, null);
        	Log.d("FREEGANQUEST::MF", "The curesor has " + cursor.getCount());
        	String data = "";
        	String dataItems = "";
        	if(cursor.getCount() > 0){
        		cursor.moveToFirst();

        		for ( int i=0; i < cursor.getCount(); i++){
        			//data +=cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME )) + " : "+ cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ORIGIN_USER )) + " } ";
        			data = data.concat(cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME )) + " : "+ cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ORIGIN_USER )) + "\n "); 
        			//Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ID )) );
        			hunts.add(cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME)));
        			cursor.moveToNext();
        		}
        		cursor.close();
        		Log.d("FREEGANQUEST::", "the hunts size is " + hunts);
        		Log.d("FREEGANQUEST::MF", "data: " + data);

        		
        		for (int i =0; i < hunts.size();  i++){
        			String[] projectionI = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_ANSWER, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME, ItemTable.COLUMN_DISPLAY, ItemTable.COLUMN_ANSWER_PIC };
        			String[] selectionI = {hunts.get(i)};
        			Log.d("FREEGAN::MF", "After proj and select");
        			//checks to see if that hunt name is already in database and adds if not. 
        			//Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder
        			Cursor cursor2 = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projectionI,"hunt=?", selectionI, null);
        			Log.d("FREEGANQUEST::MF", "The curesor has " + cursor2.getCount());
        			if(cursor2.getCount() > 0) {
        				cursor2.moveToFirst();
        				for ( int j=0; j< cursor.getCount(); j++){
        					//data +=cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME )) + " : "+ cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ORIGIN_USER )) + " } ";
        					dataItems = dataItems.concat(cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_NAME )) + " : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_LOCATION )) +" : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_DESCRIPTION )) + 
        							" : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER ))+ " : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_HUNT_NAME)) +" : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_DISPLAY )) + " : "+ cursor2.getString(cursor2.getColumnIndexOrThrow( ItemTable.COLUMN_ANSWER_PIC )) +"\n "); 
        					//Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ID )) );
        					//hunts.add(cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_NAME)));
        					cursor2.moveToNext();
        				}

        			}
        			cursor2.close();

        			Log.d("FREGANQUEST::MF", "The dataItems: " + dataItems);
        		}
        	}
        	
        	
        	// Set the dynamic callback for URI requests.
        	// mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback,this);
        	Log.d("FREEQUE::ManFrag", "After sending hunts and pushing (on)");


        	
        	if(!data.equals("") || !dataItems.equals("")){
        		mNdefMessage = new NdefMessage(
        				new NdefRecord[] {
        						createNewTextRecord(data, Locale.ENGLISH, true),
        						createNewTextRecord(dataItems, Locale.ENGLISH, true) });
        	}
        	else{
        		mNdefMessage = new NdefMessage(
        				new NdefRecord[] {
        						createNewTextRecord("data is null", Locale.ENGLISH, true),
        						createNewTextRecord("dataItems is null", Locale.ENGLISH, true) });
        	}
        }
	}

public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
 byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

 Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
 byte[] textBytes = text.getBytes(utfEncoding);

 int utfBit = encodeInUtf8 ? 0 : (1 << 7);
 char status = (char)(utfBit + langBytes.length);

 byte[] data = new byte[1 + langBytes.length + textBytes.length];
 data[0] = (byte)status;
 System.arraycopy(langBytes, 0, data, 1, langBytes.length);
 System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

 return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
	}

	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		Button addItem = (Button) findViewById(R.id.addItemButton);
		if(addItem.getVisibility() ==View.GONE){
			savedInstanceState.putBoolean("Display", true);
		}
		else{
			savedInstanceState.putBoolean("Display", false);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
    public void onResume() {
        super.onResume();
 
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundNdefPush(this, mNdefMessage);
    }
 
    @SuppressWarnings("deprecation")
	@Override
    public void onPause() {
        super.onPause();
 
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundNdefPush(this);
    }
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		//Sets the respective values for game information
		Boolean addGone = savedInstanceState.getBoolean("Display");
		if(addGone){
			findViewById(R.id.addItemButton).setVisibility(View.GONE);
			
		}
		else if(findViewById(R.id.fragment_titles) != null){
			findViewById(R.id.addItemButton).setVisibility(View.VISIBLE);
			findViewById(R.id.addHunt).setVisibility(View.GONE);
		}
		else {
			findViewById(R.id.addItemButton).setVisibility(View.VISIBLE);
		}
	}
	
	
	public OnMenuItemClickListener listener2 = new OnMenuItemClickListener() {
	    @Override
	    public boolean onMenuItemClick(MenuItem item) {

	        switch (item.getItemId()) {
	        case R.id.loginB:  {
	        	//TODO: make it so it resets the fragment.
	        	Bundle args = new Bundle();
				args.putInt( "dialogID", 6 );
				args.putString( "prompt", getString( R.string.LoginTitle ) );

				LoginDialogFragment dialog = new LoginDialogFragment();
				dialog.setArguments( args );
				dialog.show( getFragmentManager(), "Dialog" );
				return true;
	        }
	        case R.id.logoutB:  {
	        	Bundle args = new Bundle();
	            args.putString( "message", getString( R.string.confirm_message ) );

	            ConfirmDialogFragment dialog = new ConfirmDialogFragment();
	            dialog.setArguments( args );
	            dialog.show( getFragmentManager(), "Dialog" );
				return true;
	        }
	        default:
				return true;   
	        }
	    }};
	    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.menu.umenu, menu);
				
				getMenuInflater().inflate(R.menu.mm, menu);
				getMenuInflater().inflate(R.menu.main_m, menu);
		return true;
	}
	/**
	 * When the menu item is clicked it will decide on Help or About fragment or setting to pop up.
	 */
	@SuppressLint("NewApi")
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
			builder.setMessage(MainActivity.MANAGER_HELP_INFO);
			builder.setPositiveButton("OK", null);
			AlertDialog dialog = builder.show();
			TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
			messageText.setGravity(Gravity.CENTER);
			return true;
		}
		case R.id.action_user: {
			View menuItemView = findViewById(R.id.action_user); // SAME ID AS MENU ID
		    PopupMenu popupMenu = new PopupMenu(this, menuItemView); 
		    popupMenu.inflate(R.menu.usermain);
		    popupMenu.setOnMenuItemClickListener(listener2);
		    
		    MenuItem UserMenuItem = popupMenu.getMenu().findItem(R.id.userName);
	           UserMenuItem.setTitle(MainActivity.USERN);
		    popupMenu.show();
			return true;
		}
		
		case R.id.send_hunts: {
			/*
             * Instantiate a new FileUriCallback to handle requests for
             * URIs
             */
            //mFileUriCallback = new FileUriCallback();
            // Set the dynamic callback for URI requests.
            //mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback,this);
            Log.d("FreeQue::ManFrag", "After sending hunts and pushing");
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * When to go between the two fragments, need to hide and show correct buttons
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		findViewById(R.id.addItemButton).setVisibility(View.GONE);
		findViewById(R.id.addHunt).setVisibility(View.VISIBLE);
		return;
	}

	/**
	 * This is the method when the user clicks on a hunt in the list, it will take the huntName and start the other fragment
	 * which will have it's correct list
	 * @param position - is the hunt Name
	 */
	public void onArticleSelected(String position) {
		// The user selected the headline of an article from the HeadlinesFragment
		huntName = position;
		// Capture the article fragment from the activity layout
		CopyOfItemActivity articleFrag = (CopyOfItemActivity)
				getSupportFragmentManager().findFragmentById(R.id.items_fragment);
		findViewById(R.id.addItemButton).setVisibility(View.VISIBLE);

		if (articleFrag != null) {
			// If article frag is available, we're in two-pane layout...
			Log.d("FQ: MF", "here in the articleUpdate");
			// Call a method in the ArticleFragment to update its content
			articleFrag.updateArticleView(position);

		} else {
			// If the frag is not available, we're in the one-pane layout and must swap frags...
			findViewById(R.id.addHunt).setVisibility(View.GONE);
			// Create fragment and give it an argument for the selected article
			CopyOfItemActivity newFragment = new  CopyOfItemActivity();
			Bundle args = new Bundle();

			args.putString( CopyOfManagerMain.HUNT_NAME, position);
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack so the user can navigate back
			transaction.replace(R.id.fragment_titles, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	/**
	 * The additemToList method starts the AdditemActivity. It also sets the needed elements
	 * used in that activity. 
	 * 
	 * @param view - this is necessary for the button to interact with the activity
	 */
	public void addItemToList(View view) {
		Intent intent = new Intent(this, AddItemActivity.class);
		intent.putExtra(CopyOfManagerMain.HUNT_NAME, huntName);
		//Set these to empty strings to prevent null point exception and prevent filling changeable
		//elements in the next activity. 
		intent.putExtra(CopyOfManagerMain.ITEM_NAME_TEXT, "");
		intent.putExtra(CopyOfManagerMain.LOC_TEXT, "");
		intent.putExtra(CopyOfManagerMain.DESC_TEXT, "");
		startActivity(intent);
	}
	
	/**
	 * Method to add a new Hunt to the database
	 */
	public void insertNewHunt(){
		ContentValues values = new ContentValues();

		values.put( ManagerHuntTable.COLUMN_NAME, huntName );
		values.put( ManagerHuntTable.COLUMN_ORIGIN_USER, MainActivity.USER );
		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		String[] selection = {huntName};
		getContentResolver().insert( FreeganContentProvider.CONTENT_URI, values );

		//checks to see if that hunt name has already been added
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, "name=?", selection, ManagerHuntTable.COLUMN_ID + " DESC" );
		if(cursor.getCount() >1){
			cursor.moveToFirst();
			Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ID )) );
			getContentResolver().delete(huntUri, null, null);
			Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +huntName+" hunt!" , Toast.LENGTH_LONG);
			toast.show();
			//fillData();

		}
		cursor.close();

	}

/**
 * method to update the hunt and it's corresponding items
 * @param newHuntName
 */
	public void updateNewHunt(String newHuntName){
		ContentValues values = new ContentValues();
		values.put( ManagerHuntTable.COLUMN_NAME, newHuntName );
		String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
		String[] selection = {huntName};
		String[] querySelection = {newHuntName};

		//checks to see if that hunt name is already in database and adds if not. 
		Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, "name=?", querySelection, ManagerHuntTable.COLUMN_ID + " DESC" );

		if(cursor.getCount() <1){
			int rowsUpdated = getContentResolver().update( FreeganContentProvider.CONTENT_URI, values, "name=?", selection );
			//fillData();
			Log.d("FREEGANQUEST::EDIT", "updated rows: " + rowsUpdated);
			String[] selectionC = {huntName};
			String[] projection2 = {ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_DESCRIPTION, ItemTable.COLUMN_HUNT_NAME};

			Cursor cursorC = getContentResolver().query(FreeganContentProvider.CONTENT_URI_I, projection2, "hunt=?", selectionC, null);
			ContentValues valuesC = new ContentValues();
			valuesC.put( ItemTable.COLUMN_HUNT_NAME, newHuntName );
			for(int i=0; i < cursorC.getCount(); ++i){
				rowsUpdated = getContentResolver().update( FreeganContentProvider.CONTENT_URI_I, valuesC, "hunt=?", selectionC );

			}
		}
		cursor.close();


	}
	public void onDialog(View view){
		Bundle args = new Bundle();
		args.putInt( "dialogID", 1 );
		args.putString( "prompt", getString( R.string.statement ) );

		InputDialogFragment dialog = new InputDialogFragment();
		dialog.setArguments( args );
		dialog.show( getFragmentManager(), "Dialog" );

	}
	/**
	 * @param input : the returned string.
	 */
	@Override
	public void onInputDone( int dialogID, String input )
	{

		if(dialogID == 1){
			this.huntName = input;
			insertNewHunt();
		}
		else if(dialogID == 2){
			Log.d("FREEGAN_QUEST MF: ", "man frag: " + huntName);
			updateNewHunt(input);
		}

	}

	/**
	 * Callback method from InputDialogFragment when the user clicks Cancel.
	 * 
	 * @param dialogID The dialog producing the callback.
	 */
	@Override
	public void onInputCancel( int dialogID )
	{
	}
	
	
	 /**
	   * Callback method from ConfirmDialogFragment when the user clicks Yes.
	   * 
	   * @param dialogID The dialog producing the callback.
	   */
	  @Override
	  public void onConfirmPositive( int dialogID )
	  {
	    MainActivity.USER = "";
	    MainActivity.USERN = "";
	    Toast toast = Toast.makeText(getApplicationContext(),"Have Logged Out ", Toast.LENGTH_LONG);
		toast.show();
		
		finish();
	  }

	  /**
	   * Callback method from ConfirmDialogFragment when the user clicks No.
	   * 
	   * @param dialogID The dialog producing the callback.
	   */
	  @Override
	  public void onConfirmNegative( int dialogID )
	  {
	    Log.d( "DIALOG_DEMO", "Negative reply from confirm dialog with id =" + dialogID );
	  }

}