package com.sch.mydropboxelibrary.adapters;

import java.text.SimpleDateFormat;
import java.util.List;

import com.sch.mydropboxelibrary.R;
import com.sch.mydropboxelibrary.model.EBook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for list Dropbox files with extesion epub (ebooks)
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.0
 */
public class EbookArrayAdapter extends ArrayAdapter<EBook> {
	// HashMap<DbxFileInfo, Integer> mIdMap = new HashMap<DbxFileInfo,
	// Integer>();
	private List<EBook> ebooksList;
	private Context context;

	// FIXME 0. Crear un custom adapter??
	/**
	 * 
	 * @param context
	 *            the context (e.g: this)
	 * @param itemLayout
	 *            the layout used to visualize the items
	 * @param ebookslist
	 *            a list with the items to show
	 */
	public EbookArrayAdapter(Context context, List<EBook> ebookslist) {
		super(context, R.layout.item_list, ebookslist);
		this.ebooksList = ebookslist;
		this.context = context;
		// Create HashMap to refer items
		/*
		 * for (int i = 0; i < ebookslist.size(); ++i) {
		 * this.mIdMap.put(ebookslist.get(i), i); }
		 */
		// TODO 1. ordenar lista. orden por defecto
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		EbookViewHolder holder = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_list,
					null, false);
			holder = new EbookViewHolder();
			holder.bookIcon = (ImageView) view.findViewById(R.id.itl_book_icon);
			holder.titleLabel = (TextView) view
					.findViewById(R.id.itl_title_label);
			holder.creationDateLabel = (TextView) view
					.findViewById(R.id.itl_creation_date_label);
			holder.filenameLabel = (TextView) view
					.findViewById(R.id.itl_filename_label);
			view.setTag(holder);
		} else {
			holder = (EbookViewHolder) view.getTag();
		}
		// Set item content
		EBook ebook = ebooksList.get(position);
		holder.bookIcon.setImageDrawable(context.getResources().
				getDrawable(R.drawable.ic_book));
		holder.titleLabel.setText(ebook.getTitle());
		holder.creationDateLabel.setText(SimpleDateFormat.getDateInstance().
				format(ebook.getCreationDate()));
		holder.filenameLabel.setText(ebook.getFilename());

		return view;
	}

	/*
	 * @Override public long getItemId(int position) { DbxFileInfo item =
	 * getItem(position); return mIdMap.get(item); }
	 * 
	 * @Override public boolean hasStableIds() { return true; }
	 */

	/**
	 * View holder that represent a item in this list. Required to optimize the
	 * view
	 */
	static class EbookViewHolder {
		ImageView bookIcon;
		TextView titleLabel;
		TextView creationDateLabel;
		TextView filenameLabel;
	}
}
