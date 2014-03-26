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
 * Description of final submission. The player can now play a hunt, submit answers, and view results. The player enter
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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, InputDialogFragment.Listener, ConfirmDialogFragment.Listener{

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
	public static String USER = "";
	public static String USERN = "";
	private static PendingIntent pendingIntent;
	private static Intent intent;
	private static String[][] techList;
	private ArrayList<String> arrayList1 = new ArrayList<String>();
	
	 NfcAdapter mNfcAdapter;
	    // Flag to indicate that Android Beam is available
	 boolean mAndroidBeamAvailable  = false;
	 
	 // A File object containing the path to the transferred files
	    // Incoming Intent
	    private Intent mIntent;

	 @Override
	 protected void onNewIntent(Intent intent) {
		 Log.d("FREEGANQUEST::MA", "new Intent, here");
        handleIntent(intent);
	 }
	 private void handleIntent(Intent intent) {
	        if (intent != null && intent.getAction().contains("android.nfc")) {
	        	Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	            if (data != null) {
	                try {
	                    for (int i = 0; i < data.length; i++) {
	                        NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
	                        for (int j = 0; j < recs.length; j++) {
	                            if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
	                                Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
	                                byte[] payload = recs[j].getPayload();
	                                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
	                                int langCodeLen = payload[0] & 0077;
	                                //String s = ("\n\nNdefMessage[" + i + "], NdefRecord[" + j + "]:\n\"");
	                                //Log.d("FREEGANQUEST::MA", "the s is " + s);
	                                String str = "\n\n" +  new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,textEncoding);
	                                //Log.d("FREEGANQUEST::MA" , "The string says from j of " + j + " : " + str);
	                                if(j ==0){
	                                	str.replaceAll("\r\n", "\n");
	                                	String[] tokens = str.split("\n");
	                                	for(int k=0; k < tokens.length; k++){
	                                		Log.d("FREEGANQUEST::MA", "The str is 1) " + tokens[k] );
	                                		if(!str.equals("")){
	                                		String[] tokensC = tokens[k].split(" : ");

	                                		if(tokensC.length ==2){
	                                			ContentValues values = new ContentValues();

	                                			values.put( ManagerHuntTable.COLUMN_NAME, tokensC[0].trim() );
	                                			values.put( ManagerHuntTable.COLUMN_ORIGIN_USER, tokensC[1].trim() );
	                                			String[] projection = { ManagerHuntTable.COLUMN_ID, ManagerHuntTable.COLUMN_NAME};
	                                			String[] selection = {tokensC[0].trim()};
	                                			getContentResolver().insert( FreeganContentProvider.CONTENT_URI, values );

	                                			//checks to see if that hunt name has already been added
	                                			Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI, projection, "name=?", selection, ManagerHuntTable.COLUMN_ID + " DESC" );
	                                			if(cursor.getCount() >1){
	                                				
	                                				cursor.moveToFirst();
	                                				Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ManagerHuntTable.COLUMN_ID )) );
	                                				getContentResolver().delete(huntUri, null, null);
	                                				Toast toast = Toast.makeText(getApplicationContext(),"Have already added " +tokensC[0]+" hunt!" , Toast.LENGTH_LONG);
	                                				toast.show();
	                                				//fillData();

	                                			}
	                                			cursor.close();
	                                		}
	                                		}
	                                	}
	                                }
	                                if(j == 1){
	                                	str.replaceAll("\r\n", "\n");
	                                	String[] tokens = str.split("\n");
	                                	for(int k=0; k < tokens.length; k++){
	                                		String[] tokensC = tokens[k].split(" : ");
	                                		
	                                		if(tokensC.length ==7){
	                                			//Log.d("FREEGANQUEST::MA", "Inputting : " + tokensC[0]);
	                                			ContentValues values = new ContentValues();
	                                			
	                                			values.put( ItemTable.COLUMN_NAME, tokensC[0].trim() );
	                                			values.put( ItemTable.COLUMN_LOCATION, tokensC[1].trim() );
	                                			values.put( ItemTable.COLUMN_DESCRIPTION, tokensC[2].trim());
	                                			values.put( ItemTable.COLUMN_HUNT_NAME, tokensC[4].trim());
	                                			values.put( ItemTable.COLUMN_DISPLAY, tokensC[5].trim());
	                                			values.put( ItemTable.COLUMN_ANSWER, "ANSWER");
	                                			
	                                			//Insert values into the item Table
	                                			Uri uri = getContentResolver().insert( FreeganContentProvider.CONTENT_URI_I, values );
	                                			//Log.d("FREEGANQUEST::MA", "inserted uri is " + uri);
	                                			//Verify if identical entries were inserted into the item Table 
	                                			String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION};
	                                			String[] selection = {tokensC[0].trim()};
	                                			Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, "name=?", selection, ItemTable.COLUMN_ID + " DESC" );
	                                			
	                                			//If there were multiple entries remove the last insert then notify the user. 
	                                			if(cursor.getCount() > 1){
	                                				cursor.moveToFirst();
	                                				Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) );
	                                				getContentResolver().delete(huntUri, null, null);
	                                				Toast toast = Toast.makeText(getApplicationContext(),"Have already added " + tokensC[0] +"!" , Toast.LENGTH_LONG);
	                                				toast.show();
	                                				finish();
	                                			}
	                                			
	                                			cursor.close();
	                                		}

	                                	}
	                                }
	                               
	                            }
	                        }
	                    }
	                  
	                    String[] projection = { ItemTable.COLUMN_ID, ItemTable.COLUMN_NAME, ItemTable.COLUMN_LOCATION, ItemTable.COLUMN_HUNT_NAME};
	                    //Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_I + "/3" );
        				//Cursor cursor = getContentResolver().query(huntUri, projection, null, null, null);
	                    Cursor cursor = getContentResolver().query( FreeganContentProvider.CONTENT_URI_I, projection, null, null, null);
        				Log.d("FREEGANQUEST::MA", "The offical count aftwerward is " + cursor.getCount());
	                    for(int i =0; i< cursor.getCount(); i++){
	                    	cursor.moveToNext();
	                    	Log.d("FREEGANQUEST::MA", "The item is: " +cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_ID )) + ": " 
	                    	+cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_NAME )) +" : " +cursor.getString(cursor.getColumnIndexOrThrow( ItemTable.COLUMN_HUNT_NAME )));
	                    }
        				cursor.close();
        				
	                } catch (Exception e) {
	                	Log.e("TagDispatch", e.toString());
	                }
	            }
	        }
	    }
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHunts = (Spinner)findViewById( R.id.selectHunts );
		Intent intent = getIntent();
		
		
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		
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
		// create an NDEF message with two records of plain text type
        
	
	}
	
	
	public OnMenuItemClickListener listener2 = new OnMenuItemClickListener() {
	    @Override
	    public boolean onMenuItemClick(MenuItem item) {

	        switch (item.getItemId()) {
	        case R.id.loginB:  {
	        	Bundle args = new Bundle();
				args.putInt( "dialogID", 1 );
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
	            
	

	 
	    
	    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
	        intent = new Intent(activity.getApplicationContext(), activity.getClass());
	        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

	        pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

	        techList = new String[][]{};
	        adapter.enableForegroundDispatch(activity, pendingIntent, null, techList);
	    }
	    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
	        adapter.disableForegroundDispatch(activity);
	    }
	    
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        stopForegroundDispatch(this, mNfcAdapter);
	    }
	@Override
	protected void onResume()
	{
		super.onResume(); // Must do this or app will crash!
		setupForegroundDispatch(this, mNfcAdapter);

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
			if(!USER.equals("")){
				Intent i = new Intent(this, ManagerFragment.class);
				startActivity(i);
			}
			else{
				Toast toast = Toast.makeText(getApplicationContext(),"Must be logged in for Manager Mode ", 4);
				toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 0);
				toast.show();
			}

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
		case R.id.action_user: {
			View menuItemView = findViewById(R.id.action_user); // SAME ID AS MENU ID
		    PopupMenu popupMenu = new PopupMenu(this, menuItemView); 
		    popupMenu.inflate(R.menu.usermain);
		    popupMenu.setOnMenuItemClickListener(listener2);
		    
		    MenuItem UserMenuItem = popupMenu.getMenu().findItem(R.id.userName);
	           UserMenuItem.setTitle(USERN);
		    popupMenu.show();
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
		getMenuInflater().inflate(R.menu.umenu, menu);
		
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
	 public void onCreateDialog(View view){
		  
		  Bundle args = new Bundle();
			args.putInt( "dialogID", 1 );
			args.putString( "prompt", getString( R.string.statement ) );

			CreateDialogFragment dialog = new CreateDialogFragment();
			dialog.setArguments( args );
			dialog.show( getFragmentManager(), "Dialog" );
	  }
	 
	 /**
	   * Callback method from ConfirmDialogFragment when the user clicks Yes.
	   * 
	   * @param dialogID The dialog producing the callback.
	   */
	  @Override
	  public void onConfirmPositive( int dialogID )
	  {
	    USER = "";
	    USERN = "";
	    Toast toast = Toast.makeText(getApplicationContext(),"Have Logged Out ", Toast.LENGTH_LONG);
		toast.show();
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
