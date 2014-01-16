package edu.mines.freeganquestcaseysoto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint( "NewApi" )
public class ConfirmDialogFragment extends DialogFragment
{
  private int dialogID;

  protected interface Listener
  {
    void onConfirmPositive( int dialogID );
    void onConfirmNegative( int dialogID );
  }

  @Override
  public Dialog onCreateDialog( Bundle savedInstanceState )
  {
    // Set default values.
    this.dialogID = -1;
    String message = getString( R.string.loginText );

    // Change values if arguments were provided.
    Bundle args = getArguments();
    if( args != null )
    {
      this.dialogID = args.getInt( "dialogID", this.dialogID );
      message = args.getString( "message", message );
    }

    // Create the dialog.
    return new AlertDialog.Builder( getActivity() )
      .setTitle( R.string.app_name )
      .setMessage( message )
      .setPositiveButton( R.string.dialog_yes,
          new DialogInterface.OnClickListener()
          {
            public void onClick( DialogInterface dialog, int whichButton )
            {
              ((ConfirmDialogFragment.Listener)getActivity()).onConfirmPositive( dialogID );
            }
          }
      )
      .setNegativeButton( R.string.dialog_no,
          new DialogInterface.OnClickListener()
          {
            public void onClick( DialogInterface dialog, int whichButton )
            {
              ((ConfirmDialogFragment.Listener)getActivity()).onConfirmNegative( dialogID );
            }
          }
      )
      .create();
  }
}
