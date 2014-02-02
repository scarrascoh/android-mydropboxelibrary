package com.sch.mydropboxelibrary.model;

import java.util.Date;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Represent an e-book file in the model of this App with attributes like 
 * title, creation date and can be extended to contain author, book icon...
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.1
 */
public class EBook {
	
	/**
	 * Types of e-books interpreted by this App
	 *
	 */
	public enum EBookType{
		EPUB;
	}
	
	private String coverfile = "ic_book.jpg";
	private String title;
	private String author;
	private Date creationDate;
	private String filename;
	private EBookType type;
	
	/**
	 * Create a new e-book
	 * 
	 * @param title the title of the e-book
	 * @param creationDate the date when was created the e-book
	 * @param filename the name of the file associated with it
	 * @param type the type of ebook
	 */
	public EBook(String title, String author, Date creationDate, String filename, 
			EBookType type) {
		this.title = title;
		this.author = author;
		this.creationDate = creationDate;
		this.filename = filename;
		this.setType(type);
		Log.d("Ebook", "Created ebook: "+this.toString());
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
	 * @return the coverfile
	 */
	public String getCoverfile() {
		return coverfile;
	}

	/**
	 * @param coverfile the coverfile to set
	 */
	public void setCoverfile(String coverfile) {
		this.coverfile = coverfile;
	}

	@Override
	public String toString() {
		return "EBook [title=" + title + ", author=" + author
				+ ", creationDate=" + creationDate + ", filename=" + filename
				+ ", type=" + type + "]";
	}
}
