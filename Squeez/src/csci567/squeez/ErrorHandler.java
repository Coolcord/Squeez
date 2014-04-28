package csci567.squeez;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorHandler {
	
	public static void ShowError(Status status, String fileName, Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Error");
		
		DialogInterface.OnClickListener errorDiag = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		};
		alertDialogBuilder.setPositiveButton("Ok", errorDiag);
		
		switch (status)
		{
			case OK:
				//Nothing to see here
				return;
			case DOES_NOT_EXIST:
				alertDialogBuilder.setMessage(fileName + " does not exist!");
				break;
			case NAME_TAKEN:
				alertDialogBuilder.setMessage("A file by the name of " + fileName + " already exists!");
				break;
			case NOT_DIRECTORY:
			case DESTINATION_NOT_DIRECTORY: //same message
				alertDialogBuilder.setMessage(fileName + " is not a directory!");
				break;
			case NOT_FILE:
				alertDialogBuilder.setMessage(fileName + " is not a file!");
				break;
			case COULD_NOT_DELETE:
				alertDialogBuilder.setMessage("Could not delete " + fileName);
				break;
			case COULD_NOT_RENAME:
				alertDialogBuilder.setMessage("Could not rename " + fileName);
				break;
			case COULD_NOT_COPY:
				alertDialogBuilder.setMessage("Could not copy " + fileName);
				break;
			case COULD_NOT_UNZIP:
				alertDialogBuilder.setMessage("Could not unzip " + fileName);
				break;
			case COULD_NOT_ZIP:
				alertDialogBuilder.setMessage("Could not zip " + fileName);
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
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}
