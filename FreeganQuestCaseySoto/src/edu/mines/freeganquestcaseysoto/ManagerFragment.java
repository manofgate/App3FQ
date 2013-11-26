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

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ManagerFragment extends FragmentActivity 
        implements CopyOfManagerMain.OnHeadlineSelectedListener, InputDialogFragment.Listener {
	
	public static String huntName;

    /** Called when the activity is first created. */
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
			Intent i = new Intent(this, ManagerFragment.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		findViewById(R.id.addItemButton).setVisibility(View.GONE);
		findViewById(R.id.addHunt).setVisibility(View.VISIBLE);
	return;
	}
	
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
    public void insertNewHunt(){
		ContentValues values = new ContentValues();

		values.put( ManagerHuntTable.COLUMN_NAME, huntName );
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

			Cursor cursorC = getContentResolver().query(FreeganContentProvider.CONTENT_URI_H, projection2, "hunt=?", selectionC, null);
			ContentValues valuesC = new ContentValues();
			valuesC.put( ItemTable.COLUMN_HUNT_NAME, newHuntName );
			for(int i=0; i < cursorC.getCount(); ++i){
				rowsUpdated = getContentResolver().update( FreeganContentProvider.CONTENT_URI_H, valuesC, "hunt=?", selectionC );

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

}