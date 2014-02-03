/*
Copyright (C) 2014 Sergio Carrasco Herranz

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.sch.mydropboxelibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.sch.mydropboxelibrary.adapters.EbookArrayAdapter;
import com.sch.mydropboxelibrary.adapters.FolderArrayAdapter;
import com.sch.mydropboxelibrary.model.EBook;
import com.sch.mydropboxelibrary.model.comparators.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Home activity. Shows a button for login with Dropbox and the list of ebooks 
 * existing in Dropbox when the user is logged.
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.4
 */
public class HomeActivity extends Activity {
	private ProgressDialog progdialog = null;
	private List<EBook> ebooks;
	private static DbxPath currentDir, newDir;
	private static Toast toast;
	private DbxAccountManager mDbxAcctMgr;
	private DbxFileSystem dbxFs;
	final static int REQUEST_LINK_TO_DBX = 0; // This value is up to you

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				"iserf0jchr78w3e", "qv6mo1h43oqgdlj");
		currentDir = DbxPath.ROOT;
		
		if (mDbxAcctMgr.hasLinkedAccount()) {
			setContentView(R.layout.ebooks_listview);
			listEBooks();
		}else{
			setContentView(R.layout.activity_home);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	/**
	 * Event triggered when the login button is clicked
	 * 
	 * @param view
	 */
	public void onClickLoginBtn(View view) {
		/* Check if the user is linked with Dropbox */
		if (!mDbxAcctMgr.hasLinkedAccount()) {
			((TextView) findViewById(R.id.home_infotext)).setText("");
			mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
		} else {// Already authenticated
			listEBooks();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {// Start using Dropbox files.
				listEBooks();
			} else {// Link failed or was cancelled by the user.
				showErrorLoginMessage(getString(R.string.invalid_login));
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Change the layout and lList all e-books with extension .epub in the
	 * Dropbox current folder
	 */
	private void listEBooks() {
		try{
			if(dbxFs == null){
				dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			}
			setContentView(R.layout.ebooks_listview);
			/* Load list of ebooks from Dropbox an show them*/
			new LoadDropboxEbooks().execute();
		} catch (Unauthorized e) {
			Log.e("exception", "Unathourized to access to files");
			showErrorLoginMessage(getString(R.string.no_access));
		}
	}

	/**
	 * Show an error message in the login layout
	 * 
	 * @param msg the error message to show
	 */
	private void showErrorLoginMessage(String msg) {
		setContentView(R.layout.activity_home);
		final TextView view = (TextView) findViewById(R.id.home_infotext);
		view.setText(msg);
		view.setTextColor(getResources().getColor(R.color.error_msg));
	}

	/**
	 * Cancel the current toast, set new text and show it
	 * 
	 * @param message
	 */
	private final void displayToast(final String message) {
		toast.cancel();
		toast.setText(message);
		toast.show();
	}

	/**
	 * Action that will be run when the change folder button is clicked
	 */
	public void onClickChangeDirBtn(View view) {
		//displayToast("Clicked btn");
		newDir = currentDir;
		new LoadDropboxFolders().execute();
	}
	
	/**
	 * Change the current folder to the specified and load the ebooks
	 */
	public void changeFolder(DbxPath folder){
		// If go to previous folder and NO ROOT, load previous folder
		if(folder.getName().equals(getString(R.string.up_folder))){
			newDir = folder.getParent().getParent();
		}else{
			newDir = folder;
		}
		new LoadDropboxFolders().execute();
	}

	/* ************************* SUBCLASSES ***************************** */
	
	/**
	 * Load data from Dropbox in background (asynctask)
	 */
	class LoadDropboxEbooks extends AsyncTask<Void, Void, List<EBook>> {
		@Override
		protected void onPreExecute() {
			//showDialog(AUTHORIZING_DIALOG);
			HomeActivity.this.progdialog = ProgressDialog.show(
					HomeActivity.this, 
					getString(R.string.loading_title), 
					getString(R.string.loading_ebooks));
		}

		@Override
		protected List<EBook> doInBackground(Void... v) {
			List<DbxFileInfo> folderItems;
			List<EBook> ebooksL = new ArrayList<EBook>();

			try {
				folderItems = dbxFs.listFolder(currentDir);

				DbxFile dbxfile = null;
				EpubReader epubreader = new EpubReader();
				Book book = null;
				ZipInputStream zipinstream = null;
				Drawable bookCover = null;

				/*
				 * Open file if it's an epub file, get the title and author and
				 * create a new object according to the model
				 */
				for (int i=0; i < folderItems.size(); i++) {
					DbxFileInfo dbxfileInfo = folderItems.get(i);
					if (dbxfileInfo.isFolder
							|| !dbxfileInfo.path.getName().endsWith(".epub"))
						continue;// skip folders
					try {
						dbxfile = dbxFs.open(dbxfileInfo.path);
						zipinstream = new ZipInputStream(dbxfile.getReadStream());
						book = epubreader.readEpub(zipinstream);
						String author = "";
						Metadata metabook = book.getMetadata();
						// Get all authors of the book
						for (Author authorB : metabook.getAuthors()) {
							author += ", " + authorB.getFirstname()
									+ authorB.getLastname();
						}
						try {
							bookCover = Drawable.createFromStream(
								book.getCoverImage().getInputStream(), "ic_ebook_"+i);
						} catch (IOException e) {
							bookCover = getResources().getDrawable(R.drawable.ic_book);
							Log.e("homeebook", "Image couldn't be loaded"+e.getMessage());
						};
						ebooksL.add(new EBook(metabook.getFirstTitle(), author,
								dbxfileInfo.modifiedTime, 
								dbxfileInfo.path.getName(), 
								EBook.EBookType.EPUB, 
								bookCover));
						zipinstream.close();
					} catch (Exception e) {
						// If a file is not valid or can't be opened, it
						// will be ignored
						continue;
					} finally {
						dbxfile.close();
					}
				}// for-end
			} catch (Unauthorized e) {
				Log.e("exception", "Unathourized to access to files");
				showErrorLoginMessage(getString(R.string.no_access));
			} catch (DbxException e) {
				Log.e("exception", e.getMessage());
				displayToast(getString(R.string.error_loading_books));
			}

			return ebooksL;
		}
		
		@Override
		protected void onPostExecute(List<EBook> result) {
			//setContentView(R.layout.ebooks_listview);
			final ListView listview = (ListView) findViewById(R.id.ebooks_list);
			final EbookArrayAdapter adapter;
			
			// Pass the result data back to the main activity
			HomeActivity.this.ebooks = result;
			
			// Pass list to the adapter to create the list of ebooks
			// sort by title (by default)
			adapter = new EbookArrayAdapter(HomeActivity.this, ebooks);
			listview.setAdapter(adapter);
			TextView tv = (TextView) findViewById(R.id.ebl_msg);
			// If list empty, show message
			if(adapter.getCount() == 0){
				tv.setText(R.string.no_entries);
			}else{
				tv.setText("");
			}
			
			//Set on change event for spinner
			Spinner spinner = (Spinner) findViewById(R.id.sort_type);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					sortEBooksList(adapter, (String) parent.getItemAtPosition(position));
					//listview.setAdapter(adapter);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// Do nothing
				}
			});
			// Sort ebooks list
			sortEBooksList(adapter, (String) spinner.getSelectedItem());
			
			// remove progress dialog
			if (HomeActivity.this.progdialog != null) {
				HomeActivity.this.progdialog.dismiss();
			}
		}
	}
	
	/**
	 * Sort the list of the specified adapter in the specified sort
	 * @param adapter
	 * @param sort
	 */
	void sortEBooksList(EbookArrayAdapter adapter, String sort) {
		if(sort.equals(getString(R.string.sort_by_date))){
			adapter.sort(new EBookDateComparator());
		}else{//Default option
			adapter.sort(new EBookTitleComparator());
		}
	}
	
	/**
	 * Load data from Dropbox in background (asynctask)
	 */
	class LoadDropboxFolders extends AsyncTask<Void, Void, List<DbxPath>> {
		@Override
		protected void onPreExecute() {
			//showDialog(AUTHORIZING_DIALOG);
			HomeActivity.this.progdialog = ProgressDialog.show(
					HomeActivity.this, 
					getString(R.string.loading_title), 
					getString(R.string.loading_folders));
		}

		@Override
		protected List<DbxPath> doInBackground(Void... v) {
			List<DbxFileInfo> folderItems;
			List<DbxPath> foldersL = new ArrayList<DbxPath>();

			try {
				folderItems = dbxFs.listFolder(newDir);
				if(!newDir.equals(DbxPath.ROOT)){
					foldersL.add(new DbxPath(newDir, 
							getString(R.string.up_folder)));
				}
				
				/*
				 * Get the list of folders of the current directory
				 */
				for (int i=0; i < folderItems.size(); i++) {
					DbxFileInfo dbxfileInfo = folderItems.get(i);
					if (!dbxfileInfo.isFolder){ continue; } //skip files
					foldersL.add(dbxfileInfo.path);
				}// for-end
			} catch (Unauthorized e) {
				Log.e("exception", "Unathourized to access to files");
				showErrorLoginMessage(getString(R.string.no_access));
			} catch (DbxException e) {
				Log.e("exception", e.getMessage());
				displayToast(getString(R.string.error_loading_books));
			}

			return foldersL;
		}
		
		@Override
		protected void onPostExecute(List<DbxPath> foldersL) {
			// Open dialog
			final Dialog dlg = new Dialog(HomeActivity.this);
			LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = li.inflate(R.layout.folders_listview, null, false);
			dlg.setTitle(R.string.change_folder_title);
			dlg.setContentView(v);
			ListView listview = (ListView) dlg.findViewById(R.id.folders_list);
			final FolderArrayAdapter adapter;
			
			// Pass list to the adapter to create the list of folders
			adapter = new FolderArrayAdapter(dlg.getContext(), foldersL);
			listview.setAdapter(adapter);
			// Load folders when selected one
			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					dlg.dismiss();
					HomeActivity.this.changeFolder((DbxPath) parent.getItemAtPosition(position));
				}
			});
			// Load ebooks when confirm button clicked
			Button confirmBtn = (Button) dlg.findViewById(R.id.fl_confirm_btn);
			confirmBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dlg.dismiss();
					TextView tv = (TextView) findViewById(R.id.ebl_curdir);
					if(newDir.equals(DbxPath.ROOT)){
						tv.setText("/");
					}else{
						tv.setText(newDir.toString());
					}
					if(!currentDir.equals(newDir)){
						currentDir = newDir;
						listEBooks();
					}
				}
			});
			
			// remove progress dialog
			if (HomeActivity.this.progdialog != null) {
				HomeActivity.this.progdialog.dismiss();
			}
			
			// Show list of folders
			dlg.show();
		}
	}
}
