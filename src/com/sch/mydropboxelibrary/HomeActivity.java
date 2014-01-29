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

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxList;
import com.dropbox.sync.android.DbxPath;
import com.sch.mydropboxelibrary.adapters.EbookArrayAdapter;
import com.sch.mydropboxelibrary.model.EBook;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private Toast toast;
	private DbxAccountManager mDbxAcctMgr;
	final static int REQUEST_LINK_TO_DBX = 0;  // This value is up to you
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), 
				"iserf0jchr78w3e", "qv6mo1h43oqgdlj");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	/**
	 * Event triggered when the login button is clicked
	 * @param view
	 */
	public void onClickLoginBtn(View view){
		((TextView) findViewById(R.id.home_infotext)).setText("");
		/* Check if the user is linked with Dropbox */
		if(!mDbxAcctMgr.hasLinkedAccount()){
			mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
		}else{//Already authenticated
			listEBooks();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_LINK_TO_DBX) {
	        if (resultCode == Activity.RESULT_OK) {// Start using Dropbox files.
	        	listEBooks();
	        } else {//Link failed or was cancelled by the user.
	        	showErrorLoginMessage(getString(R.string.invalid_login));
	        }
	    } else {
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	}
	
	
	/**
	 * Change the layout and lList all e-books with extension .epub 
	 * in the Dropbox current folder
	 */
	private void listEBooks(){
		setContentView(R.layout.ebooks_listview);
		
		List<EBook> ebooks = new ArrayList<EBook>();
		EbookArrayAdapter adapter;
		ListView listview = (ListView) findViewById(R.id.ebooks_list);
		List<DbxFileInfo> folderItems;
		
		try {
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			folderItems = dbxFs.listFolder(DbxPath.ROOT);
			//
			// TODO 0. Seguir aqui. Listar carpetas y ebooks
			// Crear otro list view para cambiar de dirctorio? creo q no, mejor uno 
			// con carpetas o un fragment en el que elegir la ruta que aparezca
			// sobre el layout y luego debajo los libros??
			
			// Pass list to the adapter to create the list of ebooks
			adapter = new EbookArrayAdapter(this, ebooks);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick (AdapterView<?> parent, final View view,
		          int position, long id) {
		        displayToast("Clicked: "+view.getId());
		        //FIXME 0. Cambiar, mostrar la imagen on double click
		        //usar variable con temporizador? de 500ms
		      }
		    });
		} catch (Unauthorized e) {
			Log.e("exception", "Unathourized to access to files");
			showErrorLoginMessage(getString(R.string.no_access));
		} catch (DbxException e) {
			Log.e("exception", e.getMessage());
			displayToast(getString(R.string.error_loading_books));
		}
	}
	
	/**
	 * Show an error message in the login layout
	 * @param msg the error message to show
	 */
	private void showErrorLoginMessage(String msg){
		setContentView(R.layout.activity_home);
		final TextView view = (TextView) findViewById(R.id.home_infotext);
		view.setText(msg);
		view.setTextColor(getResources().getColor(R.color.error_msg));
	}
	
	
	/**
	 * Cancel the current toast, set new text and show it
	 * @param message
	 */
	private final void displayToast(final String message) {
		toast.cancel();
		toast.setText(message); 
		toast.show();
	}
	
}
