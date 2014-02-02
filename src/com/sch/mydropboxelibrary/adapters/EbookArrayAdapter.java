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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.sch.mydropboxelibrary.R;
import com.sch.mydropboxelibrary.model.EBook;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter for list Dropbox files with extesion epub (ebooks)
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.1
 */
public class EbookArrayAdapter extends ArrayAdapter<EBook> {
	// HashMap<DbxFileInfo, Integer> mIdMap = new HashMap<DbxFileInfo, Integer>();
	private List<EBook> ebooksList;
	private Context context;

	/**
	 * 
	 * @param context the context (e.g: this)
	 * @param itemLayout the layout used to visualize the items
	 * @param ebookslist a list with the items to show
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
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		EbookViewHolder holder = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item_list,
					null, false);
			holder = new EbookViewHolder();
			holder.bookIcon = (ImageView) view.findViewById(R.id.itl_book_icon);
			holder.bookIcon.setOnClickListener(new View.OnClickListener() {
				private int clicks = 0;
				@Override
				public void onClick(View v) {
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
	                	showEBookCover((EBook) EbookArrayAdapter.this.getItem(position));
	                }
	                clicks++;
				}
			});
			
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
		try {
			holder.bookIcon.setImageDrawable(Drawable.createFromStream(
					ebook.getCoverImage().getInputStream(), "ic_ebook_"+position));
		} catch (IOException e) {
			holder.bookIcon.setImageResource(R.drawable.ic_book);
			Log.e("ebookarray", "Image couldn't be loaded"+e.getMessage());
		};
		holder.titleLabel.setText(ebook.getTitle());
		holder.creationDateLabel.setText(SimpleDateFormat.getDateInstance()
				.format(ebook.getCreationDate()));
		holder.filenameLabel.setText(ebook.getFilename());
		
		return view;
	}

	/*
	 * @Override 
	 * public long getItemId(int position) { 
	 * 	DbxFileInfo item =getItem(position); 
	 * 	return mIdMap.get(item); 
	 * }
	 * 
	 * @Override 
	 * public boolean hasStableIds() { 
	 * 	return true; 
	 * }
	 */
	
	
	
	/**
	 * Show the cover of the specified ebook
	 * @param pos
	 */
	private void showEBookCover(EBook ebook){
		Context context = this.getContext();
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
		image.setBackgroundResource(R.drawable.ic_book);

		// Without this line there is a very small border around the image (1px)
		dialog.getWindow().setBackgroundDrawable(null);

		dialog.show();
	}
	
	/* ************************ SUBCLASSES ******************* */

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
