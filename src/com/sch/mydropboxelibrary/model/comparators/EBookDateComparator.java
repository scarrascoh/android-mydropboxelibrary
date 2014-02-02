package com.sch.mydropboxelibrary.model.comparators;

import java.util.Comparator;

import com.sch.mydropboxelibrary.model.EBook;


/**
 * Comparator that sort by the creation date of the ebook
 */
public class EBookDateComparator implements Comparator<EBook>{
	@Override
	public int compare(EBook ebook1, EBook ebook2) {
		return ebook1.getCreationDate().compareTo(ebook2.getCreationDate());
	}
}

