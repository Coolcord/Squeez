package csci567.squeez;

import android.app.AlertDialog;
import android.content.Context;

public class ErrorHandler {
	
	public static void ShowError(Status status, Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("It's all over!!!");
		
		switch (status)
		{
			case OK:
				//Nothing to see here
				return;
			case DOES_NOT_EXIST:
				alertDialog.setMessage("DOES_NOT_EXIST");
				break;
			case NAME_TAKEN:
				alertDialog.setMessage("NAME_TAKEN");
				break;
			case NOT_DIRECTORY:
				alertDialog.setMessage("NOT_DIRECTORY");
				break;
			case NOT_FILE:
				alertDialog.setMessage("NOT_FILE");
				break;
			case COULD_NOT_DELETE:
				alertDialog.setMessage("COULD_NOT_DELETE");
				break;
			case COULD_NOT_RENAME:
				alertDialog.setMessage("COULD_NOT_RENAME");
				break;
			case COULD_NOT_COPY:
				alertDialog.setMessage("COULD_NOT_COPY");
				break;
			case COULD_NOT_UNZIP:
				alertDialog.setMessage("COULD_NOT_UNZIP");
				break;
			case COULD_NOT_ZIP:
				alertDialog.setMessage("COULD_NOT_ZIP");
				break;
			case DESTINATION_NOT_DIRECTORY:
				alertDialog.setMessage("DESTINATION_NOT_DIRECTORY");
				break;
			default:
				assert(false);
		}
		
		/*
		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.tick);
		
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		        // Write your code here to execute after dialog closed
		        Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
		        }
		});
		*/
		
		alertDialog.show();
	}
}
