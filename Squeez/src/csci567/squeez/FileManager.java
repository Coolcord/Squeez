package csci567.squeez;

import java.util.ArrayList;

import android.R.string;

public class FileManager {

	public static Status List(string directory, ArrayList<string> files) {
		return Status.OK;
	}
	
	public static Status Move(string source, string destination) {
		assert(source != destination);
		return Status.OK;
	}
	
	public static Status Rename(string source, string destination) {
		assert(source != destination);
		return Status.OK;
	}
	
	public static Status Copy(string source, string destination) {
		assert(source != destination);
		return Status.OK;
	}
	
	public static Status Delete(string source, string destination) {
		assert(source != destination);
		return Status.OK;
	}
	
}
