package com.sch.mydropboxelibrary.adapters;

import java.util.HashMap;
import java.util.List;

import com.dropbox.sync.android.DbxFileInfo;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Adapter for jobs object of the model
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.0
 */
public class EBookArrayAdapter extends ArrayAdapter<DbxFileInfo> {
	HashMap<DbxFileInfo, Integer> mIdMap = new HashMap<DbxFileInfo, Integer>();
	//FIXEM 0. Crear un custom adapter??
	/**
	 * 
	 * @param context the context (e.g: this)
	 * @param itemLayout the layout used to visualize the items
	 * @param ebookslist a list with the items to show
	 */
	public EBookArrayAdapter(Context context, int textViewResourceId, 
			List<DbxFileInfo> ebookslist) {
		super(context, textViewResourceId, ebookslist);
		//Create HashMap to refer items
		for (int i=0; i < ebookslist.size(); ++i) {
	       this.mIdMap.put(ebookslist.get(i), i);
	    }
	}
	
	@Override
    public long getItemId(int position) {
	  DbxFileInfo item = getItem(position);
      return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

}
