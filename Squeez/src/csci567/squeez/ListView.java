package csci567.squeez;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ListView extends Activity implements OnClickListener {

	String directory;
	ArrayList<String> files;
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_sub);
		//String[] values = new String[] { "use", "get", "file", "function" };
		
		files = new ArrayList<String>();
		directory = "/";
		layout = (LinearLayout)findViewById(R.id.ListViewLayout);
		
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
		getMenuInflater().inflate(R.menu.list_view, menu);
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
	
	public void onClick(View v) {
		Button btn = (Button)v;
	    String folder = btn.getText().toString();
	    directory += "/" + folder;
	    Refresh();
	}
	
	public void Refresh() {
		FileManager.List(files, directory);
		layout.removeAllViews();
		
		//This for loop can be used to make clickable objects for each file given
		for (String fileName : files) {
			Button btnFile = new Button(getApplicationContext());
			btnFile.setText(fileName);
			btnFile.setOnClickListener(this);
			layout.addView(btnFile);
		}
	}

}
