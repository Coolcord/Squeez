package csci567.squeez;

import android.app.AlertDialog;
import android.content.Context;

public class ErrorHandler {
	
	public static void ShowError(Status status, String fileName, Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Error");
		
		switch (status)
		{
			case OK:
				//Nothing to see here
				return;
			case DOES_NOT_EXIST:
				alertDialog.setMessage(fileName + " does not exist!");
				break;
			case NAME_TAKEN:
				alertDialog.setMessage("A file by the name of " + fileName + " already exists!");
				break;
			case NOT_DIRECTORY:
			case DESTINATION_NOT_DIRECTORY: //same message
				alertDialog.setMessage(fileName + " is not a directory!");
				break;
			case NOT_FILE:
				alertDialog.setMessage(fileName + " is not a file!");
				break;
			case COULD_NOT_DELETE:
				alertDialog.setMessage("Could not delete " + fileName);
				break;
			case COULD_NOT_RENAME:
				alertDialog.setMessage("Could not rename " + fileName);
				break;
			case COULD_NOT_COPY:
				alertDialog.setMessage("Could not copy " + fileName);
				break;
			case COULD_NOT_UNZIP:
				alertDialog.setMessage("Could not unzip " + fileName);
				break;
			case COULD_NOT_ZIP:
				alertDialog.setMessage("Could not zip " + fileName);
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
