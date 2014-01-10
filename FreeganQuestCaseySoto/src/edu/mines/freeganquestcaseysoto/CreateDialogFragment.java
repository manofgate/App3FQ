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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
	    
	    ((Button) dialog.findViewById(R.id.create)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
            	EditText mUserName =  (EditText) dialog.findViewById(R.id.userName);
            	EditText mPassword =  (EditText) dialog.findViewById(R.id.password);
            	EditText mCPass =  (EditText) dialog.findViewById(R.id.confirmPassword);
            	EditText mName =  (EditText) dialog.findViewById(R.id.name);
            	
            	String pass = "";
        		String cPass = "";
        		
		        String key = "Spe12c34Sp51e23c45Co98nt765C9o87"; // 256 bit key
            		 
            		         // Create key and cipher
		        Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
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
        		String name = mName.getText().toString();

        		//Make sure the name and desc have content, if not give it generic information. 
        		if(username.length()>0 && pass.length()>5 && cPass.equals(pass) && name.length()>0){
        			insertNewUser(name, username, pass, dialog);
        		}
        		else{
        			Log.d("FREEGAN::CDF", "It is in the else statment");
        		}
            	
            }
        });
	  return dialog;
	  }
	  
	  public void insertNewUser(String name, String uName, String pass, Dialog d){
			ContentValues values = new ContentValues();
			values.put( UserTable.COLUMN_NAME, name );
			values.put( UserTable.COLUMN_USER_NAME, uName );
			values.put( UserTable.COLUMN_PASSWORD, pass);
			Log.d("FREEGAN::CDF", "This is the user in adding" + uName);
			//Insert values into the item Table
			getActivity().getContentResolver().insert( FreeganContentProvider.CONTENT_URI_U, values );

			//Verify if identical entries were inserted into the item Table 
			String[] projection = { UserTable.COLUMN_ID, UserTable.COLUMN_USER_NAME};
			String[] selection = {uName};
			Cursor cursor = getActivity().getContentResolver().query( FreeganContentProvider.CONTENT_URI_U, projection, "name=?", selection, UserTable.COLUMN_ID + " DESC" );
			
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
