package csci567.squeez;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class GridViewer extends Activity {
	
	String directory;
	ArrayList<String> files;
	LinearLayout layout;
	private GridView Gview;
	String [] grid_items;
	GridViewAdapter myAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        
        files = new ArrayList<String>();
        directory = "/";
        Gview = (GridView) findViewById(R.id.grid);
 
       Refresh();
    }
    
    /**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_view, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		if(item.getItemId()==R.id.zip) {
			//use zip function
			Toast.makeText(getBaseContext(), "zip selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.open) {
			//use open function
			Toast.makeText(getBaseContext(), "open selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.move) {
			//use move function
			Toast.makeText(getBaseContext(), "move selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.copy) {
			//use copy function
			Toast.makeText(getBaseContext(), "copy selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.delete) {
			//use delete function
			Toast.makeText(getBaseContext(), "delete selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onContextItemSelected(null);
	}
	
	public void onClick(View v) {
		Button btn = (Button)v;
	    String folder = btn.getText().toString();
	    
	    //Load the new Directory
	    if (folder.charAt(folder.length() - 1) == '/') {
		    directory += folder;
		    Refresh();
		}
	}
    
    public void Refresh() {
		FileManager.List(files, directory);
		//Lview.removeAllViews();
		
		grid_items = new String[files.size()];
		grid_items = files.toArray(grid_items);
		myAdapter = new GridViewAdapter(this, R.layout.grid_items, grid_items);
		
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grid_items);
		Gview.setAdapter(myAdapter);
		registerForContextMenu(Gview);
		//This for loop can be used to make clickable objects for each file given
		/*for (String fileName : files) {
			Button btnFile = new Button(getApplicationContext());	
			btnFile.setText(fileName);
			btnFile.setOnClickListener(this);
			layout.addView(btnFile);
		}*/
	}
    
    @Override
	public void onBackPressed() {
		if (directory == "/") {
			super.onBackPressed();
		} else {
			//Build the destination path
			String[] name = directory.split("/");
			if (name.length > 2) {
				directory = "";
				for (int i = 0; i < name.length-1; i++) {
					directory += "/" + name[i];
				}
			} else {
				directory = "/";
			}
			Refresh();
		}
	}
}