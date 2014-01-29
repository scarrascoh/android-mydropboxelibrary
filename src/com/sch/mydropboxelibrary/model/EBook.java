package com.sch.mydropboxelibrary.model;

import java.util.Date;

/**
 * Represent an e-book file in the model of this App with attributes like 
 * title, creation date and can be extended to contain author, book icon...
 * 
 * @author Sergio Carrasco Herranz (scarrascoh at gmail dot com)
 * @version 1.0
 */
public class EBook {
	
	/**
	 * Types of e-books interpreted by this App
	 *
	 */
	enum EBookType{
		EPUB;
	}
	
	private String title;
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
	public EBook(String title, Date creationDate, String filename, EBookType type) {
		this.title = title;
		this.creationDate = creationDate;
		this.filename = filename;
		this.setType(type);
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
}
