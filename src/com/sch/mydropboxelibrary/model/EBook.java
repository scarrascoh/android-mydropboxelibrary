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
package com.sch.mydropboxelibrary.model;

import java.util.Date;

import android.graphics.drawable.Drawable;

/**
 * Represent an e-book file in the model of this App with attributes like 
 * title, creation date and can be extended to contain author, book icon...
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.3.1
 */
public class EBook {
	private String title;
	private String author;
	private Date creationDate;
	private String filename;
	private EBookType type;
	private Drawable coverImage;
	
	/**
	 * Create a new e-book
	 * 
	 * @param title the title of the e-book
	 * @param creationDate the date when was created the e-book
	 * @param filename the name of the file associated with it
	 * @param type the type of ebook
	 */
	public EBook(String title, String author, Date creationDate, String filename, 
			EBookType type, Drawable coverImage) {
		this.title = title;
		this.author = author;
		this.creationDate = creationDate;
		this.filename = filename;
		this.setType(type);
		this.coverImage = coverImage;
	}
	
	/* *********************** GETTERS/SETTERS ********************* */

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}


	/**
	 * @return the type
	 */
	public EBookType getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(EBookType type) {
		this.type = type;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the coverImage
	 */
	public Drawable getCoverImage() {
		return coverImage;
	}

	/**
	 * @param coverImage the coverImage to set
	 */
	public void setCoverImage(Drawable coverImage) {
		this.coverImage = coverImage;
	}

	@Override
	public String toString() {
		return "EBook [title=" + title + ", author=" + author
				+ ", creationDate=" + creationDate + ", filename=" + filename
				+ ", type=" + type + "]";
	}
	
	
	/* ********************** SUBCLASSES ********************** */
	/**
	 * Types of e-books interpreted by this App
	 *
	 */
	public enum EBookType{
		EPUB;
	}	
}
