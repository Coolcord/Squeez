package csci567.squeez;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<String> {

	Context context;
	int layoutId;
	String [] files;
	int fileCounter = 0;
	public GridViewAdapter(Context context, int layoutId, String [] files) {
		super(context, layoutId, files);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.layoutId = layoutId;
		this.files = files;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View grid = convertView;
		RecordHolder holder = null;
		
		if (grid == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			grid = inflater.inflate(layoutId, parent, false);
			
			holder = new RecordHolder();
			holder.txt = (TextView) grid.findViewById(R.id.Grid_txt);
			holder.img = (ImageView) grid.findViewById(R.id.Grid_Image);
			holder.check = (CheckBox) grid.findViewById(R.id.Grid_Check);
			grid.setTag(holder);
		}
		else {
			holder = (RecordHolder) grid.getTag();
		}
		if(fileCounter < files.length) {
			String file = files[fileCounter];
			holder.txt.setText(file);
			if( file.charAt(file.length() -1) == '/') {
				holder.img.setImageResource(R.drawable.folder);
			}
			else{
				holder.img.setImageResource(R.drawable.file);
			}
			fileCounter++;
		}
		return grid;		
	}

	static class RecordHolder {
		TextView txt;
		ImageView img;
		CheckBox check;
	}
}
