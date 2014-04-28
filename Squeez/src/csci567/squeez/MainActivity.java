package csci567.squeez;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button list = (Button) findViewById(R.id.list_but);
		Button grid = (Button) findViewById(R.id.grid_but);
		list.setOnClickListener(this);
		grid.setOnClickListener(this);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public void onClick(View v)
	{
		Intent list_intent = new Intent(this, ListViewer.class);
		//Intent grid_intent = new Intent(this, GridViewss);
		switch(v.getId()) {
		case R.id.list_but:
			this.startActivity(list_intent);
			break;
		case R.id.grid_but:
			//this.startActivity(grid_intent);
			break;

		}
	}

}
