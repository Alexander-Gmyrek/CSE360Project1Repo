package ProjectUI;

import java.sql.*;
import java.util.*;
import java.util.Base64;
import java.io.Serializable;

/*******
* <p> Article Class. </p>
*
* <p> Description:  The article class that holds the article information that can be stored in the database
 * for users to view. It implements serializable as well so that the data can be backed up to a file
 * for storage
*
* @author <Zach>
* @version 1.00 10/16/2024
*/

//UML DESCRIPTION:
// This is the article management section of the architecture. Here, we envisioned having a set of articles where
// they had a set of values like their id, sequenceNum for searching, title, short description, key words, body, references,
// header, group, sensitive title and author. This design flowed to the code as this class creates unique articles and 
// with the help of getters and setters, allows the system to set and get the article values accordingly.
public class Article implements Serializable {
	//private instance variables for article information
	private int id;
	private int sequenceNum;
	private char[] title; //title of article  
    private char[] shortDescription; //abstract for article
    private char[] keywords; //keywords of the article
    private char[] body; //body text of the article
    private char[] references; //references for article
    private char[] header;
    private char[] group;
    private char[] sensitiveTitle;
    private char[] author;
    /**
     * Constructor for the Article class that sets the title, authors, abstract, keywords, body text, and references
     * so that the article has information that be read by the user.
     * 
     * @param title
     * @param abstractText
     * @param keywords
     * @param body
     * @param references
     */
    public Article(int id, char[] header, char[] group, char[] title, char[] shortDescription,
    		char[] keywords, char[] body, char[] references, char[] sensitiveTitle, char[] author, int sequenceNum) {
    	// Generate a unique long integer identifier for the article ID
	    this.id = id;
        this.header = header;
        this.group = group;
    	this.title = title;
        this.sensitiveTitle = sensitiveTitle;
        this.shortDescription = shortDescription;
        this.keywords = keywords;
        this.body = body;
        this.references = references;
        this.author = author;
        this.sequenceNum = sequenceNum;
    }
   
    // Getter methods
    /**
     * Plain Constructor
     */
    public Article() {
		//Default, no values entered
	}
    
    /**
     * @return sequenceNum that is generated from user/instructor searches
     */
    public int getSNum() {
    	return sequenceNum;
    }

    /**
     * @return the ID
     */
	public int getID() {
    	return id;
    }
    
    /**
     * @return the header
     */
    public char[] getHeader() {
        return header;
    }

    /**
     * @return the group
     */
    public char[] getGroup() {
        return group;
    }

    /**
     * @return the title
     */
    public char[] getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public char[] getShortDescription() {
        return shortDescription;
    }

    /**
     * @return the keywords
     */
    public char[] getKeywords() {
        return keywords;
    }

    /**
     * @return the body
     */
    public char[] getBody() {
        return body;
    }

    /**
     * @return the references
     */
    public char[] getReferences() {
        return references;
    }

    /**
     * @return the sensitive title
     */
    public char[] getSensitiveTitle() {
        return sensitiveTitle;
    }

    /**
     * @return the sensitive description
     */
    public char[] getAuthor() {
        return author;
    }
    
    // Setter methods
    
    
    /**
     * Set the sequence number for student and instructor searching
     * @param sequenceNum
     */
    public void setSNum(int sequenceNum) {
    	this.sequenceNum = sequenceNum;
    }
    
    /**
     * Sets the ID value
     * @param id
     */
    public void setID(int id) {
    	this.id = id;
    }
    
    /**
     * set header value
     * @param header
     */
    public void setHeader(char[] header) {
        this.header = header;
    }

    /**
     * set group value
     * @param group
     */
    public void setGroup(char[] group) {
        this.group = group;
    }

    /**
     * set title value
     * @param header
     */
    public void setTitle(char[] title) {
        this.title = title;
    }

    /**
     * set description value
     * @param shortDescription
     */
    public void setShortDescription(char[] shortDescription) {
        this.shortDescription = shortDescription;
    }

    /**
     * set keywords value
     * @param keywords
     */
    public void setKeywords(char[] keywords) {
        this.keywords = keywords;
    }

    /**
     * set body value
     * @param body
     */
    public void setBody(char[] body) {
        this.body = body;
    }

    /**
     * set references value
     * @param references
     */
    public void setReferences(char[] references) {
        this.references = references;
    }

    /**
     * set sensitive title value
     * @param sensitiveTitle
     */
    public void setSensitiveTitle(char[] sensitiveTitle) {
        this.sensitiveTitle = sensitiveTitle;
    }

    /**
     * set sensitive description value
     * @param sensitiveDescription
     */
    public void setAuthor(char[] author) {
        this.author = author;
    }
}
