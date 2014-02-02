package com.sch.mydropboxelibrary.model.comparators;

import java.util.Comparator;

import com.sch.mydropboxelibrary.model.EBook;

/**
 * Comparator that sort by the title of the ebook
 */
public class EBookTitleComparator implements Comparator<EBook>{
	@Override
	public int compare(EBook ebook1, EBook ebook2) {
		return ebook1.getTitle().compareTo(ebook2.getTitle());
	}
}
