package csci567.squeez;

import java.util.Hashtable;
import java.util.LinkedList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FileViewer extends Activity implements OnClickListener, OnLongClickListener {

	public static final int BUTTON_ID_OFFSET = 8192000;
	public static final int CHECKBOX_ID_OFFSET = BUTTON_ID_OFFSET / 2;
	public static final int HOLDER_ID_OFFSET = BUTTON_ID_OFFSET * 2;
	public static final int IMAGE_ID_OFFSET = BUTTON_ID_OFFSET * 4;
	
	Button btnRename, btnMove, btnCopy, btnDelete, btnZip, btnUnzip,
			btnManage, btnArchive, btnSelect;
	Context context;
	TextView dir_text;
	Boolean selectMode = false;
	Boolean getFolderMode = false;
	Boolean firstBoot = true;
	
	String directory = "/";
	String previousDirectory = "/"; //used for storedManage
	LinkedList<String> files = new LinkedList<String>();
	LinkedList<String> toManage = new LinkedList<String>();
	LinkedList<String> storedManage = new LinkedList<String>();
	LinkedList<String> checkedFiles = new LinkedList<String>();
	Hashtable<String, Integer> checkBoxes = new Hashtable<String, Integer>();
	SharedPreferences prefs;
	
	LinearLayout manageLayout, archiveLayout, optionButtonSpacer;
	ScrollView layout;
	String [] list_items;
	ViewType viewType = ViewType.LIST;
	
	/*
	final ProgressDialog mDialog = new ProgressDialog(context);
	final Runnable showBusyDialog = new Runnable() {
		public void run() {
	        mDialog.show();
		}
	};
	final Runnable dismissBusyDialog = new Runnable() {
		public void run() {
	        mDialog.dismiss();
		}
	};
	*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		firstBoot = prefs.getBoolean("FirstBoot", true);
		int intViewType = prefs.getInt("ViewType", 0);
		
		//mDialog.setCancelable(false);
		
		switch (intViewType) {
		case 0:
			firstBoot = true;
			break;
		case 1:
			viewType = ViewType.LIST;
			break;
		case 2:
			viewType = ViewType.GRID;
			break;
		default:
			assert(false);
			break;
		}
		
		if (firstBoot) {
			showFirstBootScreen();
		} else {
			setContentView(R.layout.activity_view);
		}
		context = this;
	}
	
	private void showFirstBootScreen() {
		setContentView(R.layout.activity_main);
		Button btnList = (Button) findViewById(R.id.list_but);
		Button btnGrid = (Button) findViewById(R.id.grid_but);
		btnList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewType = ViewType.LIST;
				firstBoot = false;
				final SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("FirstBoot", firstBoot);
				editor.putInt("ViewType", 1);
				editor.commit();
				showView();
			}
		});
		btnGrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewType = ViewType.GRID;
				firstBoot = false;
				final SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("FirstBoot", firstBoot);
				editor.putInt("ViewType", 2);
				editor.commit();
				showView();
			}
		});
	}
	
	private void showView() {
		setContentView(R.layout.activity_view);
		context = this;
		onResume();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      Refresh(); //call refresh on screen rotation
    }
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (firstBoot) {
			return;
		}
		
		layout = (ScrollView) findViewById(R.id.ViewScrollLayout);
		manageLayout = (LinearLayout)findViewById(R.id.ViewManageButtonsLayout);
		archiveLayout = (LinearLayout)findViewById(R.id.ViewArchiveButtonsLayout);
		optionButtonSpacer = (LinearLayout)findViewById(R.id.ViewOptionButtonsLayoutSpacer);
		
		btnManage = (Button) findViewById(R.id.btnManage);
		btnArchive = (Button) findViewById(R.id.btnArchive);
		btnRename = (Button) findViewById(R.id.btnRename);
		btnMove = (Button) findViewById(R.id.btnMove);
		btnCopy = (Button) findViewById(R.id.btnCopy);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnZip = (Button) findViewById(R.id.btnZip);
		btnUnzip = (Button) findViewById(R.id.btnUnzip);
		btnSelect = (Button) findViewById(R.id.btnSelect);
		
		btnManage.setOnClickListener(this);
		btnArchive.setOnClickListener(this);
		btnRename.setOnClickListener(this);
		btnMove.setOnClickListener(this);
		btnCopy.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnZip.setOnClickListener(this);
		btnUnzip.setOnClickListener(this);
		btnSelect.setOnClickListener(this);
		
		//Only show the select button in grid view mode
		if (viewType != ViewType.GRID) {
			btnSelect.setVisibility(View.GONE);
			Button btnSelectSpacer = (Button) findViewById(R.id.btnSelectSpacer);
			btnSelectSpacer.setVisibility(View.GONE);
		} else {
			if (getFolderMode) {
				btnSelect.setVisibility(View.GONE);
				Button btnSelectSpacer = (Button) findViewById(R.id.btnSelectSpacer);
				btnSelectSpacer.setVisibility(View.GONE);
			} else {
				btnSelect.setVisibility(View.VISIBLE);
				Button btnSelectSpacer = (Button) findViewById(R.id.btnSelectSpacer);
				btnSelectSpacer.setVisibility(View.INVISIBLE);
			}
		}
		
		Refresh();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!getFolderMode) {
			manageLayout.setVisibility(View.GONE);
			archiveLayout.setVisibility(View.GONE);
			optionButtonSpacer.setVisibility(View.GONE);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.clear_selected:
			checkMarkedFiles(false);
			Toast.makeText(context, "All Files Unselected", Toast.LENGTH_SHORT).show();
			break;
		case R.id.change_view_mode:
			final SharedPreferences.Editor editor = prefs.edit();
			if (viewType == ViewType.LIST) {
				viewType = ViewType.GRID;
				editor.putInt("ViewType", 2);
				Toast.makeText(context, "Switched to Grid View", Toast.LENGTH_SHORT).show();
			} else {
				viewType = ViewType.LIST;
				editor.putInt("ViewType", 1);
				Toast.makeText(context, "Switched to List View", Toast.LENGTH_SHORT).show();
			}
			editor.commit();
			onResume();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void Refresh() {
		LinearLayout rootLayout = new LinearLayout(this);
		LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		int i = 0;
		int c = 0;
		LinearLayout directoryLayout;
		checkBoxes.clear();
		switch(viewType)
		{
		case LIST:
			FileManager.List(files, directory);
			layout.removeAllViews();
			directoryLayout = (LinearLayout) findViewById(R.id.LayoutDirectoryLocation);
			directoryLayout.setBackgroundColor(Color.DKGRAY);
			dir_text = (TextView) findViewById(R.id.dir_text);
			dir_text.setText(directory);
			
			rootLayout.setLayoutParams(rootLayoutParams);
			rootLayout.setOrientation(LinearLayout.VERTICAL);
			
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
				cbFile.setOnClickListener(this);
				checkBoxes.put(directory + fileName, CHECKBOX_ID_OFFSET + i);
				btnFile.setLayoutParams(btnFileLayoutParams);
				fileHolder.setPadding(0, 5, 0, 5);
				fileHolder.addView(cbFile);
				fileHolder.addView(imFile);
				fileHolder.addView(btnFile);
				rootLayout.addView(fileHolder);
				i++;
			}
			layout.addView(rootLayout);
			break;
		case GRID:
			selectMode = false;
			FileManager.List(files, directory);
			layout.removeAllViews();
			directoryLayout = (LinearLayout) findViewById(R.id.LayoutDirectoryLocation);
			directoryLayout.setBackgroundColor(Color.DKGRAY);
			dir_text = (TextView) findViewById(R.id.dir_text);
			dir_text.setText(directory);
			
			//Determine how many columns to use based upon screen width
			int maxCols = 0;
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			for (int width = dm.widthPixels; width > 0; width -= 240)
			{
				maxCols++;
			}
			if (maxCols > 5) {
				maxCols = 5;
			}
			
			rootLayout.setLayoutParams(rootLayoutParams);
			rootLayout.setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams vLayoutParams;
			switch (maxCols)
			{
			default:
			case 1:
				vLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
				break;
			case 2:
				vLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f);
				break;
			case 3:
				vLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.33f);
				break;
			case 4:
				vLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.25f);
				break;
			case 5:
				vLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.2f);
				break;
			}
			
			LinearLayout vLayout1 = new LinearLayout(this);
			LinearLayout vLayout2 = new LinearLayout(this);
			LinearLayout vLayout3 = new LinearLayout(this);
			LinearLayout vLayout4 = new LinearLayout(this);
			LinearLayout vLayout5 = new LinearLayout(this);
			vLayout1.setLayoutParams(vLayoutParams);
			vLayout2.setLayoutParams(vLayoutParams);
			vLayout3.setLayoutParams(vLayoutParams);
			vLayout4.setLayoutParams(vLayoutParams);
			vLayout5.setLayoutParams(vLayoutParams);
			vLayout1.setOrientation(LinearLayout.VERTICAL);
			vLayout2.setOrientation(LinearLayout.VERTICAL);
			vLayout3.setOrientation(LinearLayout.VERTICAL);
			vLayout4.setOrientation(LinearLayout.VERTICAL);
			vLayout5.setOrientation(LinearLayout.VERTICAL);
			//This for loop can be used to make clickable objects for each file given
			for (String fileName : files) {
				
				LinearLayout fileHolder = new LinearLayout(this);
				LayoutParams fileHolderParams;
				
				switch (maxCols)
				{
				default:
				case 1:
					fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
					break;
				case 2:
					fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f);
					break;
				case 3:
					fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.33f);
					break;
				case 4:
					fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.25f);
					break;
				case 5:
					fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.2f);
					break;
				}
				fileHolderParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f);
				fileHolder.setLayoutParams(fileHolderParams);
				fileHolder.setOrientation(LinearLayout.VERTICAL);
				fileHolder.setPadding(0, 5, 0, 5);
				fileHolder.setId(CHECKBOX_ID_OFFSET + c);
				checkBoxes.put(directory + fileName, CHECKBOX_ID_OFFSET + c);
				
				ImageButton btnFile = new ImageButton(this);
				btnFile.setOnClickListener(this);
				btnFile.setOnLongClickListener(this);
				btnFile.setId(IMAGE_ID_OFFSET + c);
				btnFile.setBackgroundColor(Color.BLACK);
				if (fileName.charAt(fileName.length() - 1) == '/') {
					btnFile.setImageResource(R.drawable.folder);
				} else {
					btnFile.setImageResource(R.drawable.file);
				}
				TextView tvFile = new TextView(this);
				tvFile.setText(fileName);
				tvFile.setId(BUTTON_ID_OFFSET + c);
				tvFile.setGravity(Gravity.CENTER);
				tvFile.setEllipsize(TextUtils.TruncateAt.END);
				tvFile.setSingleLine(true);
				tvFile.setOnClickListener(this);
				tvFile.setOnLongClickListener(this);
				
				fileHolder.addView(btnFile);
				fileHolder.addView(tvFile);
				if (i == 0) {
					vLayout1.addView(fileHolder);
				} else if (i == 1) {
					vLayout2.addView(fileHolder);
				} else if (i == 2) {
					vLayout3.addView(fileHolder);
				} else if (i == 3) {
					vLayout4.addView(fileHolder);
				} else if (i == 4) {
					vLayout5.addView(fileHolder);
				}
				c++;
				i++;
				if (i >= maxCols) {
					i = 0;
				}
			}
			if (maxCols >= 1) {
				rootLayout.addView(vLayout1);
			}
			if (maxCols >= 2) {
				rootLayout.addView(vLayout2);
			}
			if (maxCols >= 3) {
				rootLayout.addView(vLayout3);
			}
			if (maxCols >= 4) {
				rootLayout.addView(vLayout4);
			}
			if (maxCols >= 5) {
				rootLayout.addView(vLayout5);
			}
			layout.addView(rootLayout);
			break;
		default:
			assert(false);
		}
		checkMarkedFiles(true);
	}		
		
	@Override
	public void onClick(View v) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDialog;
		switch (v.getId())
		{
			case R.id.btnManage:
				if (getFolderMode) { //Cancel get folder mode
					getFolderMode = false;
					archiveLayout.setVisibility(View.GONE);
					manageLayout.setVisibility(View.GONE);
					btnArchive.setVisibility(View.VISIBLE);
					if (viewType == ViewType.GRID) {
						btnSelect.setVisibility(View.VISIBLE);
					}
					btnMove.setVisibility(View.VISIBLE);
					btnCopy.setVisibility(View.VISIBLE);
					btnRename.setVisibility(View.VISIBLE);
					btnDelete.setVisibility(View.VISIBLE);
					btnZip.setVisibility(View.VISIBLE);
					btnUnzip.setVisibility(View.VISIBLE);
					btnMove.setText(R.string.move);
					btnCopy.setText(R.string.copy);
					btnRename.setText(R.string.rename);
					btnDelete.setText(R.string.delete);
					btnZip.setText(R.string.zip);
					btnUnzip.setText(R.string.unzip);
					btnManage.setText(R.string.manage);
					checkMarkedFiles(false);
				} else {
					archiveLayout.setVisibility(View.GONE);
					if (manageLayout.getVisibility() == View.GONE) {
						manageLayout.setVisibility(View.VISIBLE);
						optionButtonSpacer.setVisibility(View.INVISIBLE);
					} else {
						manageLayout.setVisibility(View.GONE);
						optionButtonSpacer.setVisibility(View.GONE);
					}
				}
				break;
			case R.id.btnArchive:
				manageLayout.setVisibility(View.GONE);
				if (archiveLayout.getVisibility() == View.GONE) {
					archiveLayout.setVisibility(View.VISIBLE);
					optionButtonSpacer.setVisibility(View.INVISIBLE);
				} else {
					archiveLayout.setVisibility(View.GONE);
					optionButtonSpacer.setVisibility(View.GONE);
				}
				break;
			case R.id.btnSelect:
				if (!selectMode) {
					selectMode = true;
					Toast.makeText(context, "Select Mode Enabled", Toast.LENGTH_SHORT).show();
				} else {
					selectMode = false;
					Toast.makeText(context, "Select Mode Disabled", Toast.LENGTH_SHORT).show();
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
				String[] names = toManage.getFirst().split("/");
				renameInput.setText(names[names.length-1]);
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
						} else if (which == DialogInterface.BUTTON_NEGATIVE) {
							Refresh();
						}
						manageLayout.setVisibility(View.GONE);
						archiveLayout.setVisibility(View.GONE);
						optionButtonSpacer.setVisibility(View.GONE);
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
					selectMode = false;
					checkMarkedFiles(true);
					btnMove.setText("Move Files Here");
					btnRename.setVisibility(View.GONE);
					btnCopy.setVisibility(View.GONE);
					btnDelete.setVisibility(View.GONE);
					btnArchive.setVisibility(View.GONE);
					btnSelect.setVisibility(View.GONE);
					btnManage.setText("Cancel");
					previousDirectory = directory;
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
					btnArchive.setVisibility(View.VISIBLE);
					if (viewType == ViewType.GRID) {
						btnSelect.setVisibility(View.VISIBLE);
					}
					btnManage.setText(R.string.manage);
					if (storedManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						checkMarkedFiles(false);
						break;
					} else if (previousDirectory == directory) {
						ErrorHandler.ShowError(Status.SAME_DIRECTORY, "", context);
						checkMarkedFiles(false);
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
								checkMarkedFiles(false);
								Refresh();
							} else if (which == DialogInterface.BUTTON_NEGATIVE) {
								checkMarkedFiles(false);
								Refresh();
							}
							manageLayout.setVisibility(View.GONE);
							archiveLayout.setVisibility(View.GONE);
							optionButtonSpacer.setVisibility(View.GONE);
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
					selectMode = false;
					checkMarkedFiles(true);
					btnCopy.setText("Copy Files Here");
					btnRename.setVisibility(View.GONE);
					btnMove.setVisibility(View.GONE);
					btnDelete.setVisibility(View.GONE);
					btnArchive.setVisibility(View.GONE);
					btnSelect.setVisibility(View.GONE);
					btnManage.setText("Cancel");
					previousDirectory = directory;
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
					btnArchive.setVisibility(View.VISIBLE);
					if (viewType == ViewType.GRID) {
						btnSelect.setVisibility(View.VISIBLE);
					}
					btnManage.setText(R.string.manage);
					if (storedManage.size() <= 0) {
						ErrorHandler.ShowError(Status.NO_FILES_SPECIFIED, "", context);
						checkMarkedFiles(false);
						break;
					}
					alertDialogBuilder = new AlertDialog.Builder(this);                 
					alertDialogBuilder.setTitle("Copy");  
					alertDialogBuilder.setMessage("Are you sure you want to copy the files to this directory?");                
				 	DialogInterface.OnClickListener copyDiag = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								new Thread(new Runnable() {
							        public void run() {
							        	//runOnUiThread(showBusyDialog);
										Status s = Status.OK;
										for (String file : storedManage) {
											String[] names = file.split("/");
											String location = directory + names[names.length-1];
											s = FileManager.Copy(file, location);
											/*
											if (s != Status.OK) {
												ErrorHandler.ShowError(s, file, context);
												break;
											}
											*/
										}
										/*
										if (s == Status.OK) {
											Toast.makeText(context, "Files Copied", Toast.LENGTH_LONG).show();
										}
										runOnUiThread(finishJob);
										*/
										//runOnUiThread(dismissBusyDialog);
							        }
							    }).start();
							} else if (which == DialogInterface.BUTTON_NEGATIVE) {
								checkMarkedFiles(false);
								Refresh();
								manageLayout.setVisibility(View.GONE);
								archiveLayout.setVisibility(View.GONE);
								optionButtonSpacer.setVisibility(View.GONE);
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
							checkMarkedFiles(false);
							Refresh();
						} else if (which == DialogInterface.BUTTON_NEGATIVE) {
							checkMarkedFiles(false);
							Refresh();
						}
						manageLayout.setVisibility(View.GONE);
						archiveLayout.setVisibility(View.GONE);
						optionButtonSpacer.setVisibility(View.GONE);
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
							checkMarkedFiles(false);
							Refresh();
						} else if (which == DialogInterface.BUTTON_NEGATIVE) {
							checkMarkedFiles(false);
							Refresh();
						}
						manageLayout.setVisibility(View.GONE);
						archiveLayout.setVisibility(View.GONE);
						optionButtonSpacer.setVisibility(View.GONE);
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
					selectMode = false;
					checkMarkedFiles(true);
					btnUnzip.setText("Unzip Files Here");
					btnZip.setVisibility(View.GONE);
					btnArchive.setVisibility(View.GONE);
					btnSelect.setVisibility(View.GONE);
					btnManage.setText("Cancel");
					previousDirectory = directory;
					//push these on stored to be moved later
					for (String file : toManage) {
						storedManage.add(file);
					}
				} else {
					getFolderMode = false;
					btnUnzip.setText(R.string.unzip);
					btnZip.setVisibility(View.VISIBLE);
					btnArchive.setVisibility(View.VISIBLE);
					if (viewType == ViewType.GRID) {
						btnSelect.setVisibility(View.VISIBLE);
					}
					btnManage.setText(R.string.manage);
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
								checkMarkedFiles(false);
								Refresh();
							} else if (which == DialogInterface.BUTTON_NEGATIVE) {
								checkMarkedFiles(false);
								Refresh();
							}
							manageLayout.setVisibility(View.GONE);
							archiveLayout.setVisibility(View.GONE);
							optionButtonSpacer.setVisibility(View.GONE);
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
				switch (viewType)
				{
				case LIST:
					if (v.getId() >= IMAGE_ID_OFFSET) {
						ImageView image = (ImageView)v;
						int id = image.getId();
						id -= IMAGE_ID_OFFSET;
						id += BUTTON_ID_OFFSET;
						Button btn = (Button) findViewById(id);
						selection = btn.getText().toString();
					} else if (v.getId() >= BUTTON_ID_OFFSET) { //assume button
						Button btn = (Button)v;
						selection = btn.getText().toString();
					} else { //assume checkbox
						CheckBox cb = (CheckBox)v;
						int btnId = v.getId() - CHECKBOX_ID_OFFSET + BUTTON_ID_OFFSET;
						Button btn = (Button) findViewById(btnId);
						checkFile(v.getId(), cb.isChecked());
						if (cb.isChecked()) {
							toManage.add(directory + btn.getText());
							checkedFiles.add(directory + btn.getText());
						} else {
							toManage.remove(directory + btn.getText());
							checkedFiles.remove(directory + btn.getText());
						}
						break;
					}
				    //Load the new Directory
				    if (selection.charAt(selection.length() - 1) == '/') {
					    directory += selection;
					    Refresh();
					} else { //treat as a file
						Status s = FileManager.Open(directory + selection, context);
						if (s != Status.OK) {
							ErrorHandler.ShowError(s, selection, context);
						}
					}
				    break;
				case GRID:
					if (selectMode) {
						int id = 0;
						TextView tvFile;
						ImageButton image;
						if (v.getId() >= IMAGE_ID_OFFSET) {
							image = (ImageButton)v;
							id = image.getId();
							id -= IMAGE_ID_OFFSET;
							int textId = id + BUTTON_ID_OFFSET;
							tvFile = (TextView) findViewById(textId);						
						} else { //assume textview
							tvFile = (TextView)v;
							id = tvFile.getId();
							id -= BUTTON_ID_OFFSET;
							int imageId = id + IMAGE_ID_OFFSET;
							image = (ImageButton) findViewById(imageId);
						}
						
						selection = directory + tvFile.getText().toString();
						
						int holderId = id + CHECKBOX_ID_OFFSET;
						LinearLayout fileHolder = (LinearLayout) findViewById(holderId);
						
						if (toManage.contains(selection)) {
							toManage.remove(selection);
							checkedFiles.remove(selection);
							fileHolder.setBackgroundColor(Color.BLACK);
							image.setBackgroundColor(Color.BLACK);
						} else {
							toManage.add(selection);
							checkedFiles.add(selection);
							fileHolder.setBackgroundColor(Color.BLUE);
							image.setBackgroundColor(Color.BLUE);
						}
						break;
					} else {
						if (v.getId() >= IMAGE_ID_OFFSET) {
							ImageButton image = (ImageButton)v;
							int id = image.getId();
							id -= IMAGE_ID_OFFSET;
							id += BUTTON_ID_OFFSET;
							TextView tvFile = (TextView) findViewById(id);
							selection = tvFile.getText().toString();
						} else { //assume button
							TextView tvFile = (TextView)v;
							selection = tvFile.getText().toString();
						}
					    //Load the new Directory
					    if (selection.charAt(selection.length() - 1) == '/') {
						    directory += selection;
						    Refresh();
						} else { //treat as a file
							Status s = FileManager.Open(directory + selection, context);
							if (s != Status.OK) {
								ErrorHandler.ShowError(s, selection, context);
							}
						}
					    break;
					}
				default:
					assert(false);
				}
				break;
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		switch (viewType)
		{
		case LIST: //doesn't make use of Long Click
			break;
		case GRID:
			if (!getFolderMode) {
				if (!selectMode) {
					selectMode = true;
					Toast.makeText(context, "Select Mode Enabled", Toast.LENGTH_SHORT).show();
					onClick(v);
				} else {
					selectMode = false;
					Toast.makeText(context, "Select Mode Disabled", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		default:
			assert(false);
		}
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
	
	private void checkFile(int id, Boolean check) {
		
		Button btn = null;
		ImageButton img = null;
		int imageId = id - CHECKBOX_ID_OFFSET + IMAGE_ID_OFFSET;
		int btnId = id;
		btnId -= CHECKBOX_ID_OFFSET;
		btnId += BUTTON_ID_OFFSET;
		int holderId = id;
		switch (viewType) {
		case LIST:
			holderId -= CHECKBOX_ID_OFFSET;
			holderId += HOLDER_ID_OFFSET;
			CheckBox cb = (CheckBox) findViewById(id);
			if (cb != null) {
				cb.setChecked(check);
			}
			btn = (Button) findViewById(btnId);
			break;
		case GRID:
			img = (ImageButton) findViewById(imageId);
			break;
		default:
			assert(false);
			break;
		}
		
		LinearLayout fileHolder = (LinearLayout) findViewById(holderId);
		if (check) {
			if (getFolderMode) {
				if (btn != null) {
					btn.setBackgroundColor(Color.parseColor("#0B610B"));
				}
				if (img != null) {
					img.setBackgroundColor(Color.parseColor("#0B610B"));
				}
				fileHolder.setBackgroundColor(Color.parseColor("#0B610B"));
			} else {
				if (btn != null) {
					btn.setBackgroundColor(Color.BLUE);
				}
				if (img != null) {
					img.setBackgroundColor(Color.BLUE);
				}
				fileHolder.setBackgroundColor(Color.BLUE);
			}
		} else {
			if (btn != null) {
				btn.setBackgroundColor(Color.BLACK);
			}
			if (img != null) {
				img.setBackgroundColor(Color.BLACK);
			}
			fileHolder.setBackgroundColor(Color.BLACK);
		}
	}
	
	private void checkMarkedFiles(Boolean check) {
		
		for (String fileName : checkedFiles) {
			int id = 0;
			int btnId = 0;
			String currentFile = "";
			switch (viewType) {
			case LIST:
				if (checkBoxes.containsKey(fileName)) {
					id = checkBoxes.get(fileName);
				} else {
					continue;
				}
				btnId = id - CHECKBOX_ID_OFFSET + BUTTON_ID_OFFSET;
				Button btn = (Button) findViewById(btnId);
				currentFile = directory + btn.getText().toString();
				if (currentFile.contentEquals(fileName)) {
					CheckBox cb = (CheckBox) findViewById(id);
					if (cb != null) {
						checkFile(id, check);
					}
				}
				break;
			case GRID:
				if (checkBoxes.containsKey(fileName)) {
					id = checkBoxes.get(fileName);
				} else {
					continue;
				}
				btnId = id - CHECKBOX_ID_OFFSET + BUTTON_ID_OFFSET;
				TextView tv = (TextView) findViewById(btnId);
				currentFile = directory + tv.getText().toString();
				if (currentFile.contentEquals(fileName)) {
					LinearLayout linearLayout = (LinearLayout) findViewById(id);
					if (linearLayout != null) {
						checkFile(id, check);
					}
				}
				break;
			default:
				assert(false);
				break;
			}
		}
		//Enable or disable checkboxes depending upon if getFolderMode is enabled
		if (viewType != viewType.GRID) {
			for (int id : checkBoxes.values()) {
				CheckBox cb = (CheckBox) findViewById(id);
				if (cb != null) {
					if (getFolderMode) {
						cb.setEnabled(false);
					} else {
						cb.setEnabled(true);
					}
				}
			}
		}
		if (!check) {
			storedManage.clear();
			toManage.clear();
			checkedFiles.clear();
		}
	}
}
