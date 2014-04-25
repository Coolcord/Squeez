package csci567.squeez;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchiveManager {

	public static Status Zip(ArrayList<String> files, String archive) {
		assert(files != null);
		assert(files.size() > 0);
		
		BufferedInputStream bufferIn = null;
		FileInputStream inputFile = null;
		FileOutputStream outputArchive = null;
		Status s = Status.OK;
		
		//Check to see if the archive exists
		File archiveFile = new File(archive);
		if (archiveFile.exists()) {
			s = FileManager.Delete(archive); //delete it if it does
			if (s != Status.OK) {
				return s;
			}
		}
		
		//Prepare the streams and buffers
		try {
			outputArchive = new FileOutputStream(archive);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Status.COULD_NOT_ZIP;
		}
		BufferedOutputStream bufferOut = new BufferedOutputStream(outputArchive);
		ZipOutputStream zip = new ZipOutputStream(bufferOut);
		ZipEntry zipEntry = null;
		byte bytes[] = new byte[1024];
		
		//Perform the zip
		try {
			for (String fileName : files) {
				inputFile = new FileInputStream(fileName);
				bufferIn = new BufferedInputStream(inputFile, 1024);
				zipEntry = new ZipEntry(fileName.substring(fileName.lastIndexOf("/") + 1));
				zip.putNextEntry(zipEntry);
				for (int i = bufferIn.read(); i != -1; i = bufferIn.read()) {
					outputArchive.write(bytes, 0, i);
				}
				bufferIn.close();
				inputFile.close();
			}
			bufferOut.close();
		} catch (IOException e) { //something went wrong
			e.printStackTrace();
			try {
				bufferIn.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				inputFile.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				zip.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			s = FileManager.Delete(archive);
			if (s != Status.OK) {
				return s;
			}
			return Status.COULD_NOT_ZIP;
		}
		
		//Close the streams
		try {
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				outputArchive.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			s = FileManager.Delete(archive);
			if (s != Status.OK) {
				return s;
			}
			return Status.COULD_NOT_ZIP;
		}
		try {
			outputArchive.close();
		} catch (IOException e) {
			e.printStackTrace();
			s = FileManager.Delete(archive);
			if (s != Status.OK) {
				return s;
			}
			return Status.COULD_NOT_ZIP;
		}
		
		//Build the destination path
		String[] name = archive.split("/");
		String destination = "";
		for (int i = 0; i < name.length-1; i++) {
			destination += name[i] + "/";
		}
		
		s = FileManager.List(files, destination); //refresh the contents of the current directory
		if (s != Status.OK) {
			return s;
		}
		return Status.OK;
	}
	
	public static Status Unzip(ArrayList<String> files, String archive) {
		assert(files != null);
		
		//Build the destination path
		String[] name = archive.split("/");
		String destination = "";
		for (int i = 0; i < name.length-1; i++) {
			destination += name[i] + "/";
		}
		
		return Unzip(files, archive, destination); //unzip to current directory
	}
	
	public static Status Unzip(ArrayList<String> files, String archive, String destination) {
		assert(archive != destination);
		assert(files != null);
		
		//Make sure the archive exists
		File archiveFile = new File(archive);
		if (!archiveFile.isFile()) {
			return Status.NOT_FILE;
		}
		
		//Make sure the destination path is fine
		File destinationFile = new File(destination);
		if (!destinationFile.exists()) {
			if (!destinationFile.mkdirs()) { //create the missing folders
				return Status.COULD_NOT_UNZIP;
			}
		} else if (!destinationFile.isDirectory()) {
			return Status.DESTINATION_NOT_DIRECTORY;
		}
		
		//Open the archive
		FileInputStream archiveFileStream = null;
		FileOutputStream fileStream = null;
		ZipInputStream zip = null;
		ZipEntry zipEntry = null;
		try {
			archiveFileStream = new FileInputStream(archive);
			zip = new ZipInputStream(archiveFileStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return Status.DOES_NOT_EXIST;
		}
		
		//Perform unzip
		try {
			while ((zipEntry = zip.getNextEntry()) != null) {
				if (zipEntry.isDirectory()) {
					File folder = new File(destination + zipEntry.getName());
					if (!folder.exists()) {
						folder.mkdirs();
					} else { //it already exists, so delete it first
						Status s = FileManager.Delete(destination + zipEntry.getName());
						if (s != Status.OK) {
							zip.close();
							return s; //something went wrong with delete
						}
						folder.mkdirs(); //recreate the folder
					}
				} else {
					fileStream = new FileOutputStream(destination + zipEntry.getName());
					for (int i = zip.read(); i != -1; i = zip.read()) {
						fileStream.write(i); //write the bytes
					}
					fileStream.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				archiveFileStream.close();
				zip.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return Status.COULD_NOT_UNZIP;
		}
		
		//Close the streams
		try {
			archiveFileStream.close();
			zip.close();
		} catch (IOException e) {
			e.printStackTrace();
			return Status.COULD_NOT_UNZIP;
		}
		
		Status s = FileManager.List(files, destination); //refresh the contents of the current directory
		if (s != Status.OK) {
			return s;
		}
		return Status.OK;
	}
}
