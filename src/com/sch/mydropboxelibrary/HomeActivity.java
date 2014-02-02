/*
Copyright (C) 2014  Sergio Carrasco Herranz

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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	
	/**
	 * Show the cover of the specified ebook
	 * @param pos
	 */
	private void showEBookCover(EBook ebook){
		Context context = this;
		// Get screen size
		/*Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int screenWidth = size.x;
		int screenHeight = size.y;

		// Get target image size
		Bitmap bitmap = BitmapFactory.decodeFile(TARGET);
		int bitmapHeight = bitmap.getHeight();
		int bitmapWidth = bitmap.getWidth();

		// Scale the image down to fit perfectly into the screen
		// The value (250 in this case) must be adjusted for phone/tables displays
		while(bitmapHeight > (screenHeight - 250) || bitmapWidth > (screenWidth - 250)) {
		    bitmapHeight = bitmapHeight / 2;
		    bitmapWidth = bitmapWidth / 2;
		}

		// Create resized bitmap image
		BitmapDrawable resizedBitmap = new BitmapDrawable(context.getResources(), 
				Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false));*/

		// Create dialog
		Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.xml.ebook_cover);

		ImageView image = (ImageView) dialog.findViewById(R.id.imageview);
		image.setBackground();

		// Without this line there is a very small border around the image (1px)
		dialog.getWindow().setBackgroundDrawable(null);

		dialog.show();
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

				/*
				 * Open file if it's an epub file, get the title and author and
				 * create a new object according to the model
				 */
				for (DbxFileInfo dbxfileInfo : folderItems) {
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
						ebooksL.add(new EBook(metabook.getFirstTitle(), author,
								dbxfileInfo.modifiedTime, dbxfileInfo.path
										.getName(), EBook.EBookType.EPUB));
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
			adapter = new EbookArrayAdapter(HomeActivity.this, ebooks);
			listview.setAdapter(adapter);
			//FIXME 0. Mover al adaptador (ejecutar cuando se pulsa la iamgen)
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				private int clicks = 0;
				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					// displayToast("Clicked: " + ((EBook)
					// parent.getAdapter().getItem(position)).getTitle());
					
	                Handler handler = new Handler();
	                Runnable r = new Runnable() {                   
	                    @Override
	                    public void run() {
	                        clicks = 0;
	                    }
	                };
	                if (clicks == 0) {
	                	//displayToast("single click!");
	                    handler.postDelayed(r, 1000);
	                } else if (clicks == 1) {
	                	clicks = 0;
	                	showEBookCover((EBook) parent.getAdapter().getItem(position));
	                }
	                clicks++;
				}
			});
			
			// remove progress dialog
			if (HomeActivity.this.progdialog != null) {
				HomeActivity.this.progdialog.dismiss();
			}
		}
	}
}
