package csci567.squeez;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileManager {

	//Some of these asserts will be handled by status codes later
	
	public static Status List(String directory, ArrayList<String> files) {
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
	
	public static Status Move(String source, String destination) {
		assert(source != destination);
		
		Copy(source, destination);
		Delete(source);
		
		//Handle exceptions!
		
		assert(false);
		return Status.OK;
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
		
		File oldFile = new File(source);
		File newFile = new File(destination);
		oldFile.renameTo(newFile);
		
		//Handle exceptions!
		
		assert(false);
		return Status.OK;
	}
	
	public static Status Copy(String source, String destination) {
		assert(source != destination);
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		
		try {
			inStream = new FileInputStream(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    try {
			outStream = new FileOutputStream(destination);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    
	    try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
			inStream.close();
		    outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    //Handle Exceptions!
	    
		assert(false);
		return Status.OK;
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
			Status s = List(source, files);
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
