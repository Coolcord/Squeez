package csci567.squeez;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileManager {
	
	public static Status List(ArrayList<String> files, String directory) {
		assert(files != null);
		
		files.clear(); //clear out the ArrayList before using it
		
		File folder = new File(directory);
		String[] fileList = null;
		
		//Make sure the folder exists
		if (!folder.exists()) {
			return Status.DOES_NOT_EXIST;
		} else if (!folder.isDirectory()) {
			return Status.NOT_DIRECTORY;
		}
		fileList = folder.list(); //get the files in the folder
		
		//Add all of the files to the ArrayList
		if (fileList != null) {
			for (int i = 0; i < fileList.length; i++) {
				files.add(fileList[i]);
			}
		}
		
		return Status.OK;
	}
	
	public static Status Move(ArrayList<String> files, String source, String destination) {
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Move(source, destination);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		for (int i = 0; i < name.length-1; i++) {
			folder += name[i] + "/";
		}
		
		listStatus = List(files, folder);
		if (s == Status.OK && listStatus != Status.OK) {
			return listStatus;
		}
		return s;
	}
	
	public static Status Move(String source, String destination) {
		assert(source != destination);
		Status s = Status.OK;
		
		//Perform the copy
		s = Copy(source, destination);
		if (s != Status.OK) { //make sure the copy was successful
			Delete(destination); //try to undo what's been done
			return s;
		}
		
		//Perform the delete
		s = Delete(source);
		if (s != Status.OK) { //make sure the delete was successful
			return s;
		}
		
		return Status.OK;
	}
	
	public static Status Rename(ArrayList<String> files, String source, String newName) {
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Rename(source, newName);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		for (int i = 0; i < name.length-1; i++) {
			folder += name[i] + "/";
		}
		
		listStatus = List(files, folder);
		if (s == Status.OK && listStatus != Status.OK) {
			return listStatus;
		}
		return s;
	}
	
	public static Status Rename(String source, String newName) {
		assert(source != newName);
		
		//Build the destination path
		String[] name = source.split("/");
		String destination = "";
		for (int i = 0; i < name.length-1; i++) {
			destination += name[i] + "/";
		}
		destination += newName;
		
		//Check to see if the names are valid
		File oldFile = new File(source);
		File newFile = new File(destination);
		if (!oldFile.exists()) {
			return Status.DOES_NOT_EXIST;
		} else if (newFile.exists()) {
			return Status.NAME_TAKEN;
		}
		
		//Rename the file
		if (oldFile.renameTo(newFile)) {
			return Status.OK;
		} else {
			return Status.COULD_NOT_RENAME;
		}
	}
	
	public static Status Copy(ArrayList<String> files, String source, String destination) {
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Copy(source, destination);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		for (int i = 0; i < name.length-1; i++) {
			folder += name[i] + "/";
		}
		
		listStatus = List(files, folder);
		if (s == Status.OK && listStatus != Status.OK) {
			return listStatus;
		}
		return s;
	}
	
	public static Status Copy(String source, String destination) {
		assert(source != destination);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		
		//Delete the destination file if it exists
		File newFile = new File(destination);
		if (newFile.exists()) {
			Status s = Delete(destination);
			if (s != Status.OK){
				return s;
			}
		} else {
			//Build the destination path
			String[] name = source.split("/");
			String path = "";
			for (int i = 0; i < name.length-1; i++) {
				path += name[i] + "/";
			}
			newFile = new File(path);
			if (!newFile.mkdirs()) { //create the missing folders
				return Status.COULD_NOT_COPY;
			}
		}
		
		//Prepare copy streams
		try {
			inStream = new FileInputStream(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Status.DOES_NOT_EXIST;
		}
	    try {
			outStream = new FileOutputStream(destination);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			try {
				inStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return Status.COULD_NOT_COPY;
		}
	    
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    
	    //Perform copy
	    try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
		    outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return Status.COULD_NOT_COPY;
		}
	    
	    if (!newFile.exists()) { //the new file should exist
	    	return Status.COULD_NOT_COPY; //if not, something went wrong
	    }
	    
		return Status.OK;
	}
	
	public static Status Delete(ArrayList<String> files, String source) {
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Delete(source);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		for (int i = 0; i < name.length-1; i++) {
			folder += name[i] + "/";
		}
		
		listStatus = List(files, folder);
		if (s == Status.OK && listStatus != Status.OK) {
			return listStatus;
		}
		return s;
	}
	
	public static Status Delete(String source) {
		File file = new File(source);
		
		//Make sure the file exists first
		if (!file.exists()) {
			return Status.DOES_NOT_EXIST;
		}
		
		//Call delete on all files in the folder
		if (file.isDirectory()) {
			ArrayList<String> files = new ArrayList<String>();
			Status s = List(files, source);
			assert(s == Status.OK);
			for (String fileName : files) {
				s = Delete(fileName);
				if (s != Status.OK) {
					return s; //something went wrong... abort deletion
				}
			}
		}
		
		//Call delete on the file
		if (file.delete()) {
			return Status.OK;
		} else { //delete failed
			return Status.COULD_NOT_DELETE;
		}
	}
	
	public static Status Open(String filePath) {
		
		//Use intents to open a file with a default application
		/*
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File("/sdcard/test.mp4");
		intent.setDataAndType(Uri.fromFile(file), "video/*");
		startActivity(intent);
		*/
		//"video/*"
		//"audio/*"
		//"text/*"
		assert(false);
		return Status.OK;
	}
	
}
