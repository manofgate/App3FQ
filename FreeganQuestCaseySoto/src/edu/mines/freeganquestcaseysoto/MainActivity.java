package edu.mines.freeganquestcaseysoto;

/**
 * Description of intermediate submission. There is a manager mode button in the action bar. Once clicked, it will bring up managerMain
 * which is a list of hunts. Can click on add button and it does bring up an inputDialog. This adds hunts. Can click on each hunt and add
 * items to it. The addItemActivity allows to add name, location, description, and wehather the answer should be word or picture.
 * Also Can long tap on each hunt and can delete it, edit it, or show the locations of it. 
 * @author Ben Casey
 * @author Craig Soto
 */
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	private Spinner mHunts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHunts = (Spinner)findViewById( R.id.selectHunts );
		
		ArrayList<String> arrayList1 = new ArrayList<String>();

        arrayList1.add("Select a Hunt...");
        arrayList1.add("Delhi");
        ArrayAdapter<String> adp = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,arrayList1);
        mHunts.setAdapter(adp);

        mHunts.setVisibility(View.VISIBLE);
       //Set listener Called when the item is selected in spinner
       mHunts.setOnItemSelectedListener(new OnItemSelectedListener() 
       {
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long arg3) 
            {
                //String huntN = "The quest is " + parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), city, Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> arg0) 
            {
                // TODO Auto-generated method stub
            }
        });
		
	}

	@Override
	  public boolean onOptionsItemSelected( MenuItem item )
	  {
	    switch( item.getItemId() )
	    {
	      case R.id.action_manage:
	      {
	        Intent i = new Intent(this, ManagerMain.class);
	        startActivity(i);

	        return true;
	      }
	      default:
	          return super.onOptionsItemSelected(item);
	    }
	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mm, menu);
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

}
