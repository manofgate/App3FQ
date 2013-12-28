package edu.mines.freeganquestcaseysoto;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
         
    private Activity activity;
    private ArrayList<HashMap<String, Object>> data;
    private static LayoutInflater inflater=null;
    private int rPlace;
   // public ImageLoader imageLoader;
 
    public LazyAdapter(Activity a, ArrayList<HashMap<String, Object>> d, int Rid) {
        activity = a;
        data=d;
        rPlace = Rid;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // imageLoader=new ImageLoader(activity.getApplicationContext());
    }
 
    public int getCount() {
        return data.size();
    }
 
    public Object getItem(int position) {
        return data.get(position);
    }
 
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        
        if(rPlace == R.layout.item_list_row_player){
        if(convertView==null)
            vi = inflater.inflate(R.layout.item_list_row_player, null);
        TextView answer = (TextView)vi.findViewById(R.id.answer_player); // title
        TextView description = (TextView)vi.findViewById(R.id.description_player); // description
        ImageView answerPic = (ImageView)vi.findViewById(R.id.answer_player_pic); // thumb image
        
        if (position %2 ==1) {
			vi.setBackgroundColor(Color.argb(120, 100, 100, 100));
		} else {
			vi.setBackgroundColor(Color.argb(120, 170, 170, 170)); //or whatever was original
		}

        HashMap<String, Object> article = new HashMap<String, Object>();
        article = data.get(position);
 
        // Setting all values in listview
        answer.setText((String)article.get(ItemTable.COLUMN_ANSWER));
        description.setText((String)article.get(ItemTable.COLUMN_DESCRIPTION));
        byte[] bb = (byte[])article.get(ItemTable.COLUMN_ANSWER_PIC);
        if(bb !=null){
        	Log.d("FREEGAN::HLA", "This is retrieving data"+ bb.length);
        	Bitmap b = BitmapFactory.decodeByteArray(bb, 0, bb.length);
        	Log.d("FREEGAN::HLA", "THis is what is getting back: " + b.getByteCount());
        	//answerPic.clearColorFilter();
        	Drawable drawable = new BitmapDrawable(parent.getResources(),b);
        	//Log.i("","pre setimage"); 
        	//answerPic.setImageDrawable(drawable);
        	answerPic.setImageBitmap(b);
        	//answerPic.invalidateDrawable(drawable);
        	answerPic.invalidate();
        }
        return vi;
        
        }
        else if(rPlace == R.layout.item_list_row_results){
        	  if(convertView==null)
                  vi = inflater.inflate(R.layout.item_list_row_results, null);
              TextView answer = (TextView)vi.findViewById(R.id.answer); // title
              TextView name = (TextView)vi.findViewById(R.id.name);
              TextView loc = (TextView)vi.findViewById(R.id.loc);
              TextView description = (TextView)vi.findViewById(R.id.descrption); // description
              ImageView answerPic = (ImageView)vi.findViewById(R.id.answer_player_pic); // thumb image
              
              if (position %2 ==1) {
      			vi.setBackgroundColor(Color.argb(120, 100, 100, 100));
      		} else {
      			vi.setBackgroundColor(Color.argb(120, 170, 170, 170)); //or whatever was original
      		}

              HashMap<String, Object> article = new HashMap<String, Object>();
              article = data.get(position);
       
              // Setting all values in listview
              answer.setText((String)article.get(ItemTable.COLUMN_ANSWER));
              name.setText((String)article.get(ItemTable.COLUMN_NAME));
              loc.setText((String)article.get(ItemTable.COLUMN_LOCATION));
              description.setText((String)article.get(ItemTable.COLUMN_DESCRIPTION));
              byte[] bb = (byte[])article.get(ItemTable.COLUMN_ANSWER_PIC);
              if(bb !=null){
              	Log.d("FREEGAN::HLA", "This is retrieving data"+ bb.length);
              	Bitmap b = BitmapFactory.decodeByteArray(bb, 0, bb.length);
              	Log.d("FREEGAN::HLA", "THis is what is getting back: " + b.getByteCount());
              	//answerPic.clearColorFilter();
              	Drawable drawable = new BitmapDrawable(parent.getResources(),b);
              	//Log.i("","pre setimage"); 
              	//answerPic.setImageDrawable(drawable);
              	answerPic.setImageBitmap(b);
              	//answerPic.invalidateDrawable(drawable);
              	answerPic.invalidate();
              }
              return vi;
        	
        	
        }
        return vi;
    }
}
