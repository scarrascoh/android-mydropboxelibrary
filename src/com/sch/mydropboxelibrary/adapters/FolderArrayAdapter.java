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
package com.sch.mydropboxelibrary.adapters;

import java.util.List;

import com.dropbox.sync.android.DbxPath;
import com.sch.mydropboxelibrary.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapter for list Dropbox files (used for folders, but valid for files too)
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.0
 */
public class FolderArrayAdapter extends ArrayAdapter<DbxPath> {
	// HashMap<DbxFileInfo, Integer> mIdMap = new HashMap<DbxFileInfo, Integer>();
	private List<DbxPath> foldersList;
	private Context context;

	/**
	 * 
	 * @param context the context (e.g: this)
	 * @param itemLayout the layout used to visualize the items
	 * @param ebookslist a list with the items to show
	 */
	public FolderArrayAdapter(Context context, List<DbxPath> foldersList) {
		super(context, R.layout.folders_listview, foldersList);
		this.foldersList = foldersList;
		this.context = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		FolderViewHolder holder = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.folder_item_list,
					null, false);
			holder = new FolderViewHolder();
			holder.folderLabel = (TextView) view.findViewById(R.id.folder_item);
			view.setTag(holder);
		} else {
			holder = (FolderViewHolder) view.getTag();
		}
		// Set item content
		DbxPath folder = foldersList.get(position);
		holder.folderLabel.setText(folder.getName());
		
		return view;
	}
	
	/* ************************ SUBCLASSES ******************* */

	/**
	 * View holder that represent a item in this list. Required to optimize the
	 * view
	 */
	static class FolderViewHolder {
		TextView folderLabel;
	}
}
