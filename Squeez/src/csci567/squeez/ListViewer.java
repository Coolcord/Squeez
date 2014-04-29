package csci567.squeez;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewer extends Activity implements OnClickListener, OnLongClickListener, OnCheckedChangeListener {

	public static final int BUTTON_ID_OFFSET = 8192000;
	public static final int CHECKBOX_ID_OFFSET = BUTTON_ID_OFFSET / 2;
	public static final int HOLDER_ID_OFFSET = BUTTON_ID_OFFSET * 2;
	public static final int IMAGE_ID_OFFSET = BUTTON_ID_OFFSET * 4;
	
	Button btnRename, btnMove, btnCopy, btnDelete, btnZip, btnUnzip,
			btnManage, btnArchive;
	Context context;
	TextView dir_text;
	Boolean getFolderMode;
	
	String directory;
	ArrayList<String> files;
	ArrayList<String> toManage, storedManage;
	LinearLayout layout, manageLayout, archiveLayout;
	private ListView Lview;
	String [] list_items;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		context = this;
		
		getFolderMode = false;
		files = new ArrayList<String>();
		toManage = new ArrayList<String>();
		storedManage = new ArrayList<String>();
		directory = "/";
		layout = (LinearLayout) findViewById(R.id.ListViewVerticalLayout);
		manageLayout = (LinearLayout)findViewById(R.id.ListViewManageButtonsLayout);
		archiveLayout = (LinearLayout)findViewById(R.id.ListViewArchiveButtonsLayout);
		//Lview = (ListView) findViewById(R.id.listView1);
		
		btnManage = (Button) findViewById(R.id.btnManage);
		btnArchive = (Button) findViewById(R.id.btnArchive);
		btnRename = (Button) findViewById(R.id.btnRename);
		btnMove = (Button) findViewById(R.id.btnMove);
		btnCopy = (Button) findViewById(R.id.btnCopy);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnZip = (Button) findViewById(R.id.btnZip);
		btnUnzip = (Button) findViewById(R.id.btnUnzip);
		
		btnManage.setOnClickListener(this);
		btnArchive.setOnClickListener(this);
		btnRename.setOnClickListener(this);
		btnMove.setOnClickListener(this);
		btnCopy.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnZip.setOnClickListener(this);
		btnUnzip.setOnClickListener(this);
		
		Refresh();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Toast.makeText(getBaseContext(), "EEYUP!", Toast.LENGTH_LONG).show();
		
		int position = info.position;
		if(item.getItemId()==R.id.zip) {
			//use zip function
			Toast.makeText(getBaseContext(), "zip selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.open) {
			//use open function
			Toast.makeText(getBaseContext(), "open selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.move) {
			//use move function
			Toast.makeText(getBaseContext(), "move selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.copy) {
			//use copy function
			Toast.makeText(getBaseContext(), "copy selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		if(item.getItemId()==R.id.delete) {
			//use delete function
			Toast.makeText(getBaseContext(), "delete selected: ", Toast.LENGTH_LONG).show();
			return true;
		}
		return true;
	}
	
	public void Refresh() {
		FileManager.List(files, directory);
		toManage.clear();
		layout.removeAllViews();
		dir_text = (TextView) findViewById(R.id.dir_text);
		dir_text.setText("current directory:" + directory);
		int i = 0;
		//This for loop can be used to make clickable objects for each file given
		for (String fileName : files) {
			LinearLayout fileHolder = new LinearLayout(this);
			fileHolder.setOrientation(LinearLayout.HORIZONTAL);
			fileHolder.setBackgroundColor(Color.BLACK);
			fileHolder.setId(HOLDER_ID_OFFSET + i);
			CheckBox cbFile = new CheckBox(this);
			ImageView imFile = new ImageView(this);
			if (fileName.charAt(fileName.length() - 1) == '/') {
				imFile.setBackgroundResource(R.drawable.folder);
			} else {
				imFile.setBackgroundResource(R.drawable.file);
			}
			imFile.setId(IMAGE_ID_OFFSET + i);
			imFile.setOnClickListener(this);
			Button btnFile = new Button(this);
			btnFile.setId(BUTTON_ID_OFFSET + i);
			btnFile.setText(fileName);
			btnFile.setBackgroundColor(Color.BLACK);
			btnFile.setTextColor(Color.WHITE);
			btnFile.setGravity(Gravity.LEFT);
			btnFile.setOnClickListener(this);
			btnFile.setOnLongClickListener(this);
			LayoutParams cbFileLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.01f);
			LayoutParams btnFileLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.99f);
			cbFile.setLayoutParams(cbFileLayoutParams);
			cbFile.setId(CHECKBOX_ID_OFFSET + i);
			cbFile.setOnCheckedChangeListener(this);
			btnFile.setLayoutParams(btnFileLayoutParams);
			fileHolder.setPadding(0, 5, 0, 5);
			fileHolder.addView(cbFile);
			fileHolder.addView(imFile);
			fileHolder.addView(btnFile);
			layout.addView(fileHolder);
			registerForContextMenu(btnFile);
			i++;
		}
		
		
		
		
		/*
		FileManager.List(files, directory);
		//Lview.removeAllViews();
		
		list_items = new String[files.size()];
		list_items = files.toArray(list_items);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_view_items, list_items);
		Lview.setAdapter(adapter);
		registerForContextMenu(Lview);
		Lview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				TextView txt = (TextView)v;
				String folder = txt.getText().toString();
			    
			    //Load the new Directory
			    if (folder.charAt(folder.length() - 1) == '/') {
				    directory += folder;
				    Refresh();
				}
				
			}
			
		});
		*/
/*		Lview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getBaseContext(), "long click: ", Toast.LENGTH_LONG).show();
				registerForContextMenu(v);
				return true;
			}
			
		});*/
	}
	
	@Override
	public void onClick(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDialog;
		switch (v.getId())
		{
			case R.id.btnManage:
				archiveLayout.setVisibility(View.GONE);
				if (manageLayout.getVisibility() == View.GONE) {
					manageLayout.setVisibility(View.VISIBLE);
				} else {
					manageLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.btnArchive:
				manageLayout.setVisibility(View.GONE);
				if (archiveLayout.getVisibility() == View.GONE) {
					archiveLayout.setVisibility(View.VISIBLE);
				} else {
					archiveLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.btnRename:
				//Make sure that the proper number of files have been selected
				if (toManage.size() <= 0) {
					ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
					break;
				} else if (toManage.size() > 1) {
					ErrorHandler.ShowError(Status.CAN_ONLY_RENAME_ONE, "", context);
					break;
				}
				alertDialogBuilder = new AlertDialog.Builder(this);                 
				alertDialogBuilder.setTitle("Rename");  
				alertDialogBuilder.setMessage("Enter a new name: ");                
				final EditText renameInput = new EditText(this); 
			 	DialogInterface.OnClickListener renameDiag = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							String newName = renameInput.getText().toString();
							Status s = Status.OK;
							for (String file : toManage) {
								s = FileManager.Rename(file, newName);
								if (s != Status.OK) {
									ErrorHandler.ShowError(s, file, context);
									break;
								}
							}
							if (s == Status.OK) {
								Toast.makeText(context, "File Renamed", Toast.LENGTH_LONG).show();
							}
							Refresh();
						}
					}
				};
				alertDialogBuilder.setView(renameInput);
				alertDialogBuilder.setPositiveButton("Ok", renameDiag);
				alertDialogBuilder.setNegativeButton("Cancel", renameDiag);
			    alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				break;
			case R.id.btnMove:
				if (!getFolderMode) {
					if (toManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						break;
					}
					getFolderMode = true;
					btnMove.setText("Move Files Here");
					btnRename.setVisibility(View.GONE);
					btnCopy.setVisibility(View.GONE);
					btnDelete.setVisibility(View.GONE);
					//push these on stored to be moved later
					for (String file : toManage) {
						storedManage.add(file);
					}
				} else {
					getFolderMode = false;
					btnMove.setText(R.string.move);
					btnRename.setVisibility(View.VISIBLE);
					btnCopy.setVisibility(View.VISIBLE);
					btnDelete.setVisibility(View.VISIBLE);
					if (storedManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						storedManage.clear();
						break;
					}
					alertDialogBuilder = new AlertDialog.Builder(this);                 
					alertDialogBuilder.setTitle("Move");  
					alertDialogBuilder.setMessage("Are you sure you want to move the files to this directory?");                
					DialogInterface.OnClickListener moveDiag = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Status s = Status.OK;
								for (String file : storedManage) {
									String[] names = file.split("/");
									String location = directory + names[names.length-1];
									s = FileManager.Move(file, location);
									if (s != Status.OK) {
										ErrorHandler.ShowError(s, file, context);
										break;
									}
								}
								if (s == Status.OK) {
									Toast.makeText(context, "Files Moved", Toast.LENGTH_LONG).show();
								}
								storedManage.clear();
								Refresh();
							}
						}
					};
					alertDialogBuilder.setPositiveButton("Yes", moveDiag);
					alertDialogBuilder.setNegativeButton("No", moveDiag);
				    alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
				break;
			case R.id.btnCopy:
				if (!getFolderMode) {
					if (toManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						break;
					}
					getFolderMode = true;
					btnCopy.setText("Copy Files Here");
					btnRename.setVisibility(View.GONE);
					btnMove.setVisibility(View.GONE);
					btnDelete.setVisibility(View.GONE);
					//push these on stored to be moved later
					for (String file : toManage) {
						storedManage.add(file);
					}
				} else {
					getFolderMode = false;
					btnCopy.setText(R.string.copy);
					btnRename.setVisibility(View.VISIBLE);
					btnMove.setVisibility(View.VISIBLE);
					btnDelete.setVisibility(View.VISIBLE);
					if (storedManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						storedManage.clear();
						break;
					}
					alertDialogBuilder = new AlertDialog.Builder(this);                 
					alertDialogBuilder.setTitle("Copy");  
					alertDialogBuilder.setMessage("Are you sure you want to copy the files to this directory?");                
				 	DialogInterface.OnClickListener copyDiag = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Status s = Status.OK;
								for (String file : storedManage) {
									String[] names = file.split("/");
									String location = directory + names[names.length-1];
									s = FileManager.Copy(file, location);
									if (s != Status.OK) {
										ErrorHandler.ShowError(s, file, context);
										break;
									}
								}
								if (s == Status.OK) {
									Toast.makeText(context, "Files Copied", Toast.LENGTH_LONG).show();
								}
								storedManage.clear();
								Refresh();
							}
						}
					};
					alertDialogBuilder.setPositiveButton("Yes", copyDiag);
					alertDialogBuilder.setNegativeButton("No", copyDiag);
				    alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
				break;
			case R.id.btnDelete:
				if (toManage.size() <= 0) {
					ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
					break;
				}
				alertDialogBuilder.setTitle("Delete");
				alertDialogBuilder.setMessage("Are you sure you want to delete the selected files?");
				DialogInterface.OnClickListener deleteDiag = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							Status s = Status.OK;
							for (String file : toManage) {
								s = FileManager.Delete(file);
								if (s != Status.OK) {
									ErrorHandler.ShowError(s, file, context);
									break;
								}
							}
							if (s == Status.OK) {
								Toast.makeText(context, "Files Deleted", Toast.LENGTH_LONG).show();
							}
							Refresh();
						}
					}
				};
				alertDialogBuilder.setPositiveButton("Yes", deleteDiag);
				alertDialogBuilder.setNegativeButton("No", deleteDiag);
				alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				break;
			case R.id.btnZip:
				if (toManage.size() <= 0) {
					ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
					break;
				}
				alertDialogBuilder = new AlertDialog.Builder(this);                 
				alertDialogBuilder.setTitle("Zip");  
				alertDialogBuilder.setMessage("Enter a name: ");                
				final EditText archiveInput = new EditText(this); 
			 	DialogInterface.OnClickListener archiveDiag = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							String archive = archiveInput.getText().toString();
							Status s = Status.OK;
							s = ArchiveManager.Zip(toManage, directory + archive);
							if (s != Status.OK) {
								ErrorHandler.ShowError(s, archive, context);
							} else {
								Toast.makeText(context, "Archive Created", Toast.LENGTH_LONG).show();
							}
							Refresh();
						}
					}
				};
				alertDialogBuilder.setView(archiveInput);
				alertDialogBuilder.setPositiveButton("Ok", archiveDiag);
				alertDialogBuilder.setNegativeButton("Cancel", archiveDiag);
			    alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				break;
			case R.id.btnUnzip:
				//Make sure that the proper number of files have been selected
				if (!getFolderMode) {
					if (toManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						break;
					}
					getFolderMode = true;
					btnUnzip.setText("Unzip Files Here");
					btnZip.setVisibility(View.GONE);
					//push these on stored to be moved later
					for (String file : toManage) {
						storedManage.add(file);
					}
				} else {
					getFolderMode = false;
					btnUnzip.setText(R.string.unzip);
					btnZip.setVisibility(View.VISIBLE);
					if (storedManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						break;
					}
					alertDialogBuilder = new AlertDialog.Builder(this);                 
					alertDialogBuilder.setTitle("Unzip");  
					alertDialogBuilder.setMessage("Would you like to unzip the files to this directory?");                
				 	DialogInterface.OnClickListener unzipDiag = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								Status s = Status.OK;
								for (String file : storedManage) {
									s = ArchiveManager.Unzip(file, directory);
									if (s != Status.OK) {
										ErrorHandler.ShowError(s, file, context);
										break;
									}
								}
								if (s == Status.OK) {
									Toast.makeText(context, "Archive Unzipped", Toast.LENGTH_LONG).show();
								}
								storedManage.clear();
								Refresh();
							}
						}
					};
					alertDialogBuilder.setPositiveButton("Yes", unzipDiag);
					alertDialogBuilder.setNegativeButton("No", unzipDiag);
				    alertDialog = alertDialogBuilder.create();
					alertDialog.show();
				}
				break;
			default:
				String selection;
				if (v.getId() >= IMAGE_ID_OFFSET) {
					ImageView image = (ImageView)v;
					int id = image.getId();
					id -= IMAGE_ID_OFFSET;
					id += BUTTON_ID_OFFSET;
					Button btn = (Button) findViewById(id);
					selection = btn.getText().toString();
				} else { //assume button
					Button btn = (Button)v;
					selection = btn.getText().toString();
				}
				    
				    //Load the new Directory
				    if (selection.charAt(selection.length() - 1) == '/') {
					    directory += selection;
					    Refresh();
					} else { //treat as a file
						FileManager.Open(directory + selection, this);
					}
				    break;
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		Toast.makeText(getBaseContext(), "long click: ", Toast.LENGTH_LONG).show();
		//registerForContextMenu(v);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (directory == "/") {
			super.onBackPressed();
		} else {
			//Build the destination path
			String[] name = directory.split("/");
			directory = "";
			if (name.length > 1) {
				for (int i = 0; i < name.length-1; i++) {
					if (name[i].length() > 0) {
						directory += "/" + name[i];
					}
				}
				directory += "/";
			} else {
				directory = "/";
			}
			Refresh();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		int btnId = buttonView.getId();
		btnId -= CHECKBOX_ID_OFFSET;
		btnId += BUTTON_ID_OFFSET;
		int holderId = buttonView.getId();
		holderId -= CHECKBOX_ID_OFFSET;
		holderId += HOLDER_ID_OFFSET;
		Button btn = (Button) findViewById(btnId);
		LinearLayout fileHolder = (LinearLayout) findViewById(holderId);
		if (isChecked) {
			toManage.add(directory + btn.getText());
			btn.setBackgroundColor(Color.BLUE);
			fileHolder.setBackgroundColor(Color.BLUE);
		} else {
			toManage.remove(directory + btn.getText());
			btn.setBackgroundColor(Color.BLACK);
			fileHolder.setBackgroundColor(Color.BLACK);
		}	
	}
}
