package csci567.squeez;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileManager {
	
	public static Status List(LinkedList<String> files, String directory) {
		assert(files != null);
		
		files.clear(); //clear out the LinkedList before using it
		
		File folder = new File(directory);
		String[] fileList = null;
		
		//Make sure the folder exists
		if (!folder.exists()) {
			return Status.DOES_NOT_EXIST;
		} else if (!folder.isDirectory()) {
			return Status.NOT_DIRECTORY;
		}
		fileList = folder.list(); //get the files in the folder
		
		//Add all of the files to the LinkedList
		if (fileList != null) {
			for (int i = 0; i < fileList.length; i++) {
				folder = new File(directory + fileList[i]);
				if (folder.canRead()) { //only show files that can be accessed
					if (folder.isDirectory()) {
						fileList[i] += "/";
					}
					files.add(fileList[i]);
				}
			}
		}
		
		return Status.OK;
	}
	
	public static Status Move(LinkedList<String> files, String source, String destination) {
		assert(files != null);
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Move(source, destination);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		if (name.length > 1) {
			for (int i = 0; i < name.length-1; i++) {
				if (name[i].length() > 0) {
					folder += "/" + name[i];
				}
			}
			folder += "/";
		} else {
			folder = "/";
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
	
	public static Status Rename(LinkedList<String> files, String source, String newName) {
		assert(files != null);
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Rename(source, newName);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		if (name.length > 1) {
			for (int i = 0; i < name.length-1; i++) {
				if (name[i].length() > 0) {
					folder += "/" + name[i];
				}
			}
			folder += "/";
		} else {
			folder = "/";
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
		if (name.length > 1) {
			for (int i = 0; i < name.length-1; i++) {
				if (name[i].length() > 0) {
					destination += "/" + name[i];
				}
			}
			destination += "/";
		} else {
			destination = "/";
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
	
	public static Status Copy(LinkedList<String> files, String source, String destination) {
		assert(files != null);
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Copy(source, destination);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		if (name.length > 1) {
			for (int i = 0; i < name.length-1; i++) {
				if (name[i].length() > 0) {
					folder += "/" + name[i];
				}
			}
			folder += "/";
		} else {
			folder = "/";
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
		
		//Append (Copy) to the filename if it exists
		File newFile = new File(destination);
		if (newFile.exists()) {
			String names[] = destination.split("\\.");
	        String extension = "";
	        String path = "";
	        int appendCount = 0;
	        if (names.length > 1) { //filename has an extension
	        	extension = names[names.length-1];
	        	for (int i = 0; i < names.length-1; i++) {
		        	path += names[i];
		        }
	        	do {
		        	appendCount++;
		        	destination = path;
		        	for (int i = 0; i < appendCount; i++) {
		        		destination += " (Copy)"; //add (Copy) to the filename
		        	}
		        	destination += "." + extension;
		        	newFile = new File(destination);
	        	} while (newFile.exists()); //continue to append (Copy) until a file with the name does not exist
	        } else { //no extension
	        	do {
					destination += " (Copy)"; //add (Copy) to the filename
					newFile = new File(destination);
				} while (newFile.exists()); //continue to append (Copy) until a file with the name does not exist
	        }
		} else {
			//Build the destination path
			String[] name = source.split("/");
			String path = "";
			if (name.length > 1) {
				for (int i = 0; i < name.length-1; i++) {
					if (name[i].length() > 0) {
						path += "/" + name[i];
					}
				}
				path += "/";
			} else {
				path = "/";
			}
			newFile = new File(path);
			newFile.mkdirs();
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
	
	public static Status Delete(LinkedList<String> files, String source) {
		assert(files != null);
		
		Status s = Status.OK;
		Status listStatus = Status.OK;
		s = Delete(source);
		
		//Build the destination path
		String[] name = source.split("/");
		String folder = "";
		if (name.length > 1) {
			for (int i = 0; i < name.length-1; i++) {
				if (name[i].length() > 0) {
					folder += "/" + name[i];
				}
			}
			folder += "/";
		} else {
			folder = "/";
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
			LinkedList<String> files = new LinkedList<String>();
			Status s = List(files, source);
			assert(s == Status.OK);
			for (String fileName : files) {
				s = Delete(source + fileName);
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
	
	public static Status Open(String filePath, Context context) {
		
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(filePath);
		if (!file.exists()) {
			return Status.DOES_NOT_EXIST;
		}
        String name[] = filePath.split("\\.");
        String extension = name[name.length-1];
        
        if (extension.contentEquals("doc") || extension.contentEquals("docx")) {
            // Word document
            intent.setDataAndType(Uri.fromFile(file), "application/msword");
        } else if (extension.contentEquals("pdf")) {
            // PDF file
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        } else if (extension.contentEquals("ppt") || extension.contentEquals("pptx")) {
            // Powerpoint file
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-powerpoint");
        } else if (extension.contentEquals("xls") || extension.contentEquals("xlsx")) {
            // Excel file
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
        } else if (extension.contentEquals("zip") || extension.contentEquals("rar")) {
            // Archive file
            intent.setDataAndType(Uri.fromFile(file), "application/zip");
        } else if (extension.contentEquals("rtf")) {
            // RTF file
            intent.setDataAndType(Uri.fromFile(file), "application/rtf");
        } else if (extension.contentEquals("wav") || extension.contentEquals("mp3")) {
            // WAV audio file
            intent.setDataAndType(Uri.fromFile(file), "audio/x-wav");
        } else if (extension.contentEquals("gif")) {
            // GIF file
            intent.setDataAndType(Uri.fromFile(file), "image/gif");
        } else if (extension.contentEquals("jpg") || extension.contentEquals("jpeg") || extension.contentEquals("png")) {
            // JPG file
            intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
        } else if (extension.contentEquals("txt")) {
            // Text file
            intent.setDataAndType(Uri.fromFile(file), "text/plain");
        } else if (extension.contentEquals("3gp") || extension.contentEquals("mpg") || extension.contentEquals("mpeg") || extension.contentEquals("mpe") || extension.contentEquals("mp4") || extension.contentEquals("avi")) {
            // Video files
            intent.setDataAndType(Uri.fromFile(file), "video/*");
        } else {
            // Unknown file
            intent.setDataAndType(Uri.fromFile(file), "*/*");
        }
        try {
        	context.startActivity(intent);
        } catch (Exception e) {
        	intent.setDataAndType(Uri.fromFile(file), "*/*"); //no application for specified filetype
        	try {
            	context.startActivity(intent);
            } catch (Exception e1) { //something went wrong during launch
            	return Status.COULD_NOT_OPEN;
            }
        }
		return Status.OK;
	}
}
