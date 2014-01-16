package edu.mines.freeganquestcaseysoto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateDialogFragment extends DialogFragment {

	private EditText input;
	  private int dialogID;
	  private String prompt;

	  protected interface Listener
	  {
	    void onLoginDone( int dialogID, String input );
	    void onInputCancel( int dialogID );
	    
	  }
	  
	  /**
	   * given the dialogID which is either edit or Insert.
	   * given the prompt which is hunt
	   * On Create Dialog creates the dialog and brings it up to be able to input 1 string which will be the hunt name.
	   */
	  @Override
	  public Dialog onCreateDialog( Bundle savedInstanceState )
	  {
	    // Set default values.
	    this.dialogID = -1;
	    this.prompt = getString( R.string.default_message );


	    Bundle args = getArguments();
	    if( args != null )
	    {
	      this.dialogID = args.getInt( "dialogID", this.dialogID );
	      this.prompt = args.getString( "prompt", this.prompt );
	    }

	    // Create an input field.
	    input = new EditText( getActivity() );
	    input.setInputType( InputType.TYPE_CLASS_TEXT );

	    // Create the dialog.
	    final Dialog dialog = new Dialog(getActivity());
	    
	 // Set the title
	    dialog.setTitle(prompt);
	  
	 // inflate the layout
	    dialog.setContentView(R.layout.dialog_create_account);
	    
	   ((EditText) dialog.findViewById(R.id.password)).setOnFocusChangeListener( new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	           if(!hasFocus) {
	        	   EditText mPassword =  (EditText) dialog.findViewById(R.id.password);
	        	   String pass = mPassword.getText().toString(); 
	        	    if(TextUtils.isEmpty(pass) || pass.length() < 5) 
	        	    { 
	        	       mPassword.setError("Must have 5 or more characters"); 
	        	        return; 
	        	    }
	           }
	   }});
	   
	   ((EditText) dialog.findViewById(R.id.confirmPassword)).setOnFocusChangeListener( new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	           if(!hasFocus) {
	        	   EditText mCPass =  (EditText) dialog.findViewById(R.id.confirmPassword);
	        	   String pass = mCPass.getText().toString(); 
	        	    if(TextUtils.isEmpty(pass) || pass.length() < 5) 
	        	    { 
	        	       mCPass.setError("Must have 5 or more characters"); 
	        	        return; 
	        	    }
	           }
	   }});
	    
	   ((EditText) dialog.findViewById(R.id.userName)).setOnFocusChangeListener( new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	           if(!hasFocus) {
	        	   EditText mUserName =  (EditText) dialog.findViewById(R.id.userName);
	        	   String user = mUserName.getText().toString(); 
	        	   
	        	   
	        	 //Verify if identical entries were inserted into the item Table 
	   			String[] projection = { UserTable.COLUMN_ID, UserTable.COLUMN_USER_NAME};
	   			String[] selection = {user};
	   			Cursor cursor = getActivity().getContentResolver().query( FreeganContentProvider.CONTENT_URI_U, projection, "username=?", selection, UserTable.COLUMN_ID + " DESC" );
	   				Log.d("FREEGAN::CDF", "The cursor for same user: " +user+ " is :" + cursor.getCount());
	        	    if(TextUtils.isEmpty(user) || user.length() < 1) 
	        	    { 
	        	       mUserName.setError("You must have  characters"); 
	        	        return; 
	        	    }
	        	    
	        	    else if(cursor.getCount() >= 1){
	        	    	mUserName.setError("User name already taken");
	        	    }
	        	    cursor.close();
	           }
	   }});
	    
	   ((EditText) dialog.findViewById(R.id.email)).setOnFocusChangeListener( new OnFocusChangeListener() {

	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	           if(!hasFocus) {
	        	   EditText mEmail =  (EditText) dialog.findViewById(R.id.email);
	        	   String pass = mEmail.getText().toString(); 
	        	    if(TextUtils.isEmpty(pass) || pass.length() < 1) 
	        	    { 
	        	       mEmail.setError("Must have 1 or more characters"); 
	        	        return; 
	        	    }
	           }
	   }});
	   
	    ((Button) dialog.findViewById(R.id.create)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
            	EditText mUserName =  (EditText) dialog.findViewById(R.id.userName);
            	EditText mPassword =  (EditText) dialog.findViewById(R.id.password);
            	EditText mCPass =  (EditText) dialog.findViewById(R.id.confirmPassword);
            	EditText mEmail =  (EditText) dialog.findViewById(R.id.email);
            	TextView mInvalid =(TextView) dialog.findViewById(R.id.invalidText);
            
            	
            	String pass = "";
        		String cPass = "";
        		
		        GeneratorC c = new GeneratorC();
            		 
            		         // Create key and cipher
		        Key aesKey = new SecretKeySpec(c.getThatString().getBytes(), "AES");
		        c = null;
		        Cipher cipher;
				try {
					cipher = Cipher.getInstance("AES");
					cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			        byte[] encrypted = cipher.doFinal(mPassword.getText().toString().getBytes());
			        byte[] encryptedC = cipher.doFinal( mCPass.getText().toString().getBytes());
			        pass = new String(encrypted);
			        cPass = new String(encryptedC);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            		 
            		         // encrypt the text
		        
		        
		        
            	String username = mUserName.getText().toString();
        		String email = mEmail.getText().toString();

        		//Make sure the name and desc have content, if not give it generic information. 
        		if(username.length()>0 && pass.length()>5 && cPass.equals(pass) && email.length()>0){
        			insertNewUser(email, username, pass, dialog);
        			mInvalid.setText("");
        		}
        		else{
        			if(!cPass.equals(pass)){
        				mInvalid.setText("Confirm/password not the same.");
        			}
        		}
            	
            }
        });
	  return dialog;
	  }
	  
	  public void insertNewUser(String email, String uName, String pass, Dialog d){
			ContentValues values = new ContentValues();
			values.put( UserTable.COLUMN_EMAIL, email );
			values.put( UserTable.COLUMN_USER_NAME, uName );
			values.put( UserTable.COLUMN_PASSWORD, pass);
			Log.d("FREEGAN::CDF", "This is the user in adding" + uName);
			//Insert values into the item Table
			getActivity().getContentResolver().insert( FreeganContentProvider.CONTENT_URI_U, values );

			//Verify if identical entries were inserted into the item Table 
			String[] projection = { UserTable.COLUMN_ID, UserTable.COLUMN_USER_NAME};
			String[] selection = {uName};
			Cursor cursor = getActivity().getContentResolver().query( FreeganContentProvider.CONTENT_URI_U, projection, "username=?", selection, UserTable.COLUMN_ID + " DESC" );
			
			//If there were multiple entries remove the last insert then notify the user. 
			if(cursor.getCount() > 1){
				cursor.moveToFirst();
				Uri huntUri = Uri.parse( FreeganContentProvider.CONTENT_URI_U + "/" +  cursor.getString(cursor.getColumnIndexOrThrow( UserTable.COLUMN_ID )) );
				getActivity().getContentResolver().delete(huntUri, null, null);
				Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Have already added " + uName +"!" , Toast.LENGTH_LONG);
				toast.show();
				
			}
			cursor.close();
			d.dismiss();
		}
}
