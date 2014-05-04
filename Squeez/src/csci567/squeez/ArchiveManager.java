package csci567.squeez;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ArchiveManager {

	public static Status Zip(LinkedList<String> files, String archive) {
		assert(files != null);
		assert(files.size() > 0);
		
//		BufferedInputStream bufferIn = null;
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
//		BufferedOutputStream bufferOut = new BufferedOutputStream(outputArchive);
		ZipOutputStream zip = new ZipOutputStream(outputArchive);
		ZipEntry zipEntry = null;
		byte bytes[] = new byte[1024];
		
		//Perform the zip
		try {
			for (String fileName : files) {
				inputFile = new FileInputStream(fileName);
	//			bufferIn = new BufferedInputStream(inputFile, 1024);
				zipEntry = new ZipEntry(fileName.substring(fileName.lastIndexOf("/") + 1));
				zip.putNextEntry(zipEntry);
				int length = 0;
				while ((length = inputFile.read(bytes)) > 0) {
					zip.write(bytes, 0, length);
				}
				//int count;
		        //while ((count = bufferIn.read(bytes, 0, 1024)) != -1) {
		        	//outputArchive.write(bytes, 0, count);
		        //}
				//for (int i = bufferIn.read(bytes); i != -1; i = bufferIn.read(bytes)) {
					//outputArchive.write(bytes, 0, i);
				//}
				//bufferIn.close();
				zip.closeEntry();
				inputFile.close();
			}
			zip.close();
			outputArchive.close();
			//bufferOut.close();
		} catch (IOException e) { //something went wrong
			e.printStackTrace();
			try {
				inputFile.close();
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
		
		return Status.OK;
	}
	
	public static Status Unzip(String archive) {
		
		//Build the destination path
		String[] name = archive.split("/");
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
		
		return Unzip(archive, destination); //unzip to current directory
	}
	
	public static Status Unzip(String archive, String destination) {
		assert(archive != destination);
		
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
		
		return Status.OK;
	}
}
