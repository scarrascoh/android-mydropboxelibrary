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
import com.sch.mydropboxelibrary.model.EBook;
import com.sch.mydropboxelibrary.model.comparators.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Home activity. Shows a button for login with Dropbox and the list of ebooks 
 * existing in Dropbox when the user is logged.
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.1
 */
public class HomeActivity extends Activity {
	private ProgressDialog progdialog = null;
	private List<EBook> ebooks;
	private static Toast toast;
	private DbxAccountManager mDbxAcctMgr;
	final static int REQUEST_LINK_TO_DBX = 0; // This value is up to you

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				"iserf0jchr78w3e", "qv6mo1h43oqgdlj");
		
		if (mDbxAcctMgr.hasLinkedAccount()) {
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
		((TextView) findViewById(R.id.home_infotext)).setText("");
		/* Check if the user is linked with Dropbox */
		if (!mDbxAcctMgr.hasLinkedAccount()) {
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
		/* Load list of ebooks from Dropbox an show them*/ 
		new LoadDropboxEbooks().execute();
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
		displayToast("Implement: Show fragment with the folders of the current directory");
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
				DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
						.getLinkedAccount());
				folderItems = dbxFs.listFolder(DbxPath.ROOT);
				//
				// TODO 0. Seguir aqui. Listar carpetas y ebooks
				// Crear otro list view para cambiar de dirctorio? creo q no, mejor
				// uno
				// con carpetas o un fragment en el que elegir la ruta que aparezca
				// sobre el layout y luego debajo los libros??
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
			setContentView(R.layout.ebooks_listview);
			
			EbookArrayAdapter adapter;
			ListView listview = (ListView) findViewById(R.id.ebooks_list);

			// Pass the result data back to the main activity
			HomeActivity.this.ebooks = result;
			
			// Pass list to the adapter to create the list of ebooks
			// sort by title (by default)
			adapter = new EbookArrayAdapter(HomeActivity.this, ebooks);
			adapter.sort(new EBookTitleComparator());
			listview.setAdapter(adapter);
			
			// remove progress dialog
			if (HomeActivity.this.progdialog != null) {
				HomeActivity.this.progdialog.dismiss();
			}
		}
	}
}
