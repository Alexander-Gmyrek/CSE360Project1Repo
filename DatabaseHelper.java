package ProjectUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javafx.scene.control.Alert;


/*******
* <p> DatabaseHelper Class. </p>
*
* <p> Description: This is the class that sets up the database to hold all the articles. It can create  tables, 
* list  articles, add articles to the tables, gather the articles, backup the articles, and restore the articles.
*
* @author <Zach>
* @version 1.00 10/16/2024
*/

class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	private Connection connection = null;
	private Statement statement = null; 
	
	/**
	 * Connect to the database so that the user can perform article operations upon it
	 * @throws SQLException
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Creates SQL tables that will hold the article information for the database
	 * @throws SQLException
	 */
	private void createTables() throws SQLException {
	    String articleTable = "CREATE TABLE IF NOT EXISTS helpArticles ("
	            + "id BIGINT PRIMARY KEY, " // BIGINT for unique long ID
	            + "header VARCHAR(255), " // Header value stored in VARCHAR
	            + "groupingIdentifier VARCHAR(100), "  // Group value stored in VARCHAR
	            + "title VARCHAR(255), "   // Title value stored in VARCHAR
	            + "description VARCHAR(500), "   // Description value stored in VARCHAR
	            + "keywords VARCHAR(255), "  //Keywords value stored in VARCHAR
	            + "body TEXT, "  // Body value stored in TEXT
	            + "references TEXT, "   //References value stored in TEXT
	            + "sensitiveTitle VARCHAR(255), "   //Sensitive title value stored in VARCHAR
	            + "sensitiveDescription VARCHAR(500) "  //Sensitive description value stored in VARCHAR
	            + ")";
	    //Execute the change in the database
	    statement.execute(articleTable); 
	}
	
	
	/**
	 * Add a article to the database based of an article object that is passed in with all the information filled
	 * @param article
	 * @throws Exception
	 */
	public void addArticle(Article article) throws SQLException {
	    //Database insertion process
	    String insertArticle = "INSERT INTO helpArticles (id, header, groupingIdentifier, title, description, keywords, body, references, sensitiveTitle, sensitiveDescription) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
	    	//Set the ? values in the database based on the article information entered in by the user.
	    	//Convert char array to bytes so it isn't stored in plaintext
	        pstmt.setInt(1, article.getID());
	        pstmt.setBytes(2, new String(article.getHeader()).getBytes());  
	        pstmt.setBytes(3, new String(article.getGroup()).getBytes());
	        pstmt.setBytes(4, new String(article.getTitle()).getBytes());    
	        pstmt.setBytes(5, new String(article.getShortDescription()).getBytes());  
	        pstmt.setBytes(6, new String(article.getKeywords()).getBytes());  
	        pstmt.setBytes(7, new String(article.getBody()).getBytes());     
	        pstmt.setBytes(8, new String(article.getReferences()).getBytes());
	        pstmt.setBytes(9, new String(article.getSensitiveTitle()).getBytes());  
	        pstmt.setBytes(10, new String(article.getSensitiveDescription()).getBytes()); 

	        // Execute the update to insert the article
	        pstmt.executeUpdate();
	    }
	}
	
	/**
	 * Search for an article in the database based on a search query entered in by the user
	 * @param query
	 * @return
	 * @throws SQLException
	 */
	public List<Article> searchArticles(String query) throws SQLException {
	    List<Article> articles = new ArrayList<>();
	    
	    // This SQL query selects all articles
	    String sql = "SELECT * FROM helpArticles";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	    	 //Execute search in database
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            // Read fields from ResultSet and convert bytes to char arrays
	        	int id = rs.getInt("id");
	            char[] header = new String(rs.getBytes("header")).toCharArray();
	            char[] group = new String(rs.getBytes("groupingIdentifier")).toCharArray();
	            char[] title = new String(rs.getBytes("title")).toCharArray();
	            char[] shortDescription = new String(rs.getBytes("description")).toCharArray();
	            char[] keywords = new String(rs.getBytes("keywords")).toCharArray();
	            char[] body = new String(rs.getBytes("body")).toCharArray();
	            char[] references = new String(rs.getBytes("references")).toCharArray();
	            char[] sensitiveTitle = new String(rs.getBytes("sensitiveTitle")).toCharArray();
	            char[] sensitiveDescription = new String(rs.getBytes("sensitiveDescription")).toCharArray();

	            // Create an Article object based on above information
	            Article article = new Article(id, header, group, title, shortDescription, keywords,
	            		body, references, sensitiveTitle, sensitiveDescription);

	            // Perform search on title or keywords and look for a match
	           	if (new String(title).contains(query) || new String(keywords).contains(query)) {
	            	articles.add(article);  // Add to the result list if a match is found
	           	}
	       	}
	    }
	    //Return the list of articles found
	    return articles;
	}
	
	/**
	 * Return all current articles from the database
	 * @return
	 * @throws SQLException
	 */
	public List<Article> returnArticles() throws SQLException {
	    List<Article> articles = new ArrayList<>();
	    
	    // This SQL query selects all articles
	    String sql = "SELECT * FROM helpArticles";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	    	//Execute search query (looking for all articles)
	    	ResultSet rs = pstmt.executeQuery()) {
	        
	    		while (rs.next()) {
	            	// Read fields from ResultSet and convert bytes to char arrays
	    			int id = rs.getInt("id");
	        		char[] header = new String(rs.getBytes("header")).toCharArray();
	        		char[] group = new String(rs.getBytes("groupingIdentifier")).toCharArray();
	        		char[] title = new String(rs.getBytes("title")).toCharArray();
	        		char[] shortDescription = new String(rs.getBytes("description")).toCharArray();
	        		char[] keywords = new String(rs.getBytes("keywords")).toCharArray();
	        		char[] body = new String(rs.getBytes("body")).toCharArray();
	        		char[] references = new String(rs.getBytes("references")).toCharArray();
	        		char[] sensitiveTitle = new String(rs.getBytes("sensitiveTitle")).toCharArray();
	        		char[] sensitiveDescription = new String(rs.getBytes("sensitiveDescription")).toCharArray();

	        		// Create an Article object
	        		Article article = new Article(id, header, group, title, shortDescription, keywords,
	        				body, references, sensitiveTitle, sensitiveDescription);
	        		//Add the article to the current list
	        		articles.add(article);
	        	}
	    }		
	    //Return every current article in the database
	    return articles;
	}

	/**
	 * Gather all articles found in a specific group to be used for viewing and deleting purposes
	 * in the system.
	 * @param group
	 * @return
	 * @throws SQLException
	 */
	public List<Article> getArticlesByGroup(String group) throws SQLException {
	    List<Article> articles = new ArrayList<>();
	    
	    // This SQL query selects articles for a specific group
	    String sql = "SELECT * FROM helpArticles WHERE groupingIdentifier = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setBytes(1, group.getBytes()); // Set the grouping identifier as bytes
	        
	        //Try to find groups using the specified group to search for according to the user
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                // Read fields from ResultSet and convert bytes to char arrays
	            	int id = rs.getInt("id");
	                char[] header = new String(rs.getBytes("header")).toCharArray();
	                char[] title = new String(rs.getBytes("title")).toCharArray();
	                char[] shortDescription = new String(rs.getBytes("description")).toCharArray();
	                char[] keywords = new String(rs.getBytes("keywords")).toCharArray();
	                char[] body = new String(rs.getBytes("body")).toCharArray();
	                char[] references = new String(rs.getBytes("references")).toCharArray();
	                char[] sensitiveTitle = new String(rs.getBytes("sensitiveTitle")).toCharArray();
	                char[] sensitiveDescription = new String(rs.getBytes("sensitiveDescription")).toCharArray();

	                //Add the article to the list to be returned
	                articles.add(new Article(id, header, group.toCharArray(), title, shortDescription, keywords,
	                        body, references, sensitiveTitle, sensitiveDescription));
	            }
	        }
	    }
	    //Return all grouped articles
	    return articles;
	}
	
	/**
	 * Get all current groups in the database and stored in a list
	 * @return
	 * @throws SQLException
	 */
	public List<String> getAllGroups() throws SQLException {
		//Array list to hold current groups
	    List<String> groups = new ArrayList<>();
	    String sql = "SELECT DISTINCT groupingIdentifier FROM helpArticles"; // Query to get unique groups

	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	    	 //Search for and gather all groups in the database
	         ResultSet rs = pstmt.executeQuery()) {
	         while (rs.next()) {
	              String group = rs.getString("groupingIdentifier");
	              //Add to return list
	              groups.add(group);
	        }
	    }
	    //Return all the current groups in the database
	    return groups;
	}
    
	/**
	 * Delete article from the database based off of the title entered in by tbe user
	 * @param articleTitle   title of the article
	 */
	public void deleteArticle(String articleTitle) {
        String deleteQuery = "DELETE FROM helpArticles WHERE title = ?"; // Delete search query
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setString(1, articleTitle); // Set the title to delete
            int affectedRows = pstmt.executeUpdate(); // Execute the delete operation

            //Check to see if change was enacted
            if (affectedRows > 0) {
            	//Let user know the deletion was successful
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Article deleted successfully.");
                alert.showAndWait();
            } else {
            	//Deletion failed and let user know
                Alert alert = new Alert(Alert.AlertType.ERROR, "No article found with that title.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
        	//Show error deleting article and let user know
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting article.");
            alert.showAndWait();
        }
    }
	
	/**
	 * Gather groups of articles and delete the specific group listed by the user
	 * @param group   group article belongs to
	 * @throws SQLException
	 */
	public void deleteArticlesByGroup(String group) throws SQLException {
	    String sql = "DELETE FROM helpArticles WHERE groupingIdentifier = ?"; //Group deletion query
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	    	//Delete the entire group and update database
	        pstmt.setString(1, group);
	        pstmt.executeUpdate();
	    }
	}
	
	/** 
	 * Update an article's information if it already exists in the database and enact the changes in
	 * the database
	 * @param article  the item being updated
	 * @throws SQLException
	 */
	public void updateArticle(Article article) throws SQLException {
	    // SQL update query to set each field where the article ID matches
	    String updateQuery = "UPDATE helpArticles SET "
	            + "header = ?, "
	            + "groupingIdentifier = ?, "
	            + "title = ?, "
	            + "description = ?, "
	            + "keywords = ?, "
	            + "body = ?, "
	            + "references = ?, "
	            + "sensitiveTitle = ?, "
	            + "sensitiveDescription = ? "
	            + "WHERE title = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        // Set each field value from the Article object, converted to bytes
	    	// This will update the old article information values
	        pstmt.setBytes(1, new String(article.getHeader()).getBytes());
	        pstmt.setBytes(2, new String(article.getGroup()).getBytes());
	        pstmt.setBytes(3, new String(article.getTitle()).getBytes());
	        pstmt.setBytes(4, new String(article.getShortDescription()).getBytes());
	        pstmt.setBytes(5, new String(article.getKeywords()).getBytes());
	        pstmt.setBytes(6, new String(article.getBody()).getBytes());
	        pstmt.setBytes(7, new String(article.getReferences()).getBytes());
	        pstmt.setBytes(8, new String(article.getSensitiveTitle()).getBytes());
	        pstmt.setBytes(9, new String(article.getSensitiveDescription()).getBytes());

	        // Use the article's title as the unique identifier to locate the row to update
	        pstmt.setString(10, new String(article.getTitle()));

	        // Execute the update
	        int affectedRows = pstmt.executeUpdate();

	        if (affectedRows > 0) {
	        	//Look for enacted changes and let user know update was successful
	            System.out.println("Article updated successfully.");
	        } else {
	        	//User typed in wrong title
	            System.out.println("No article found with the given title.");
	        }
	    } catch (SQLException e) {
	    	//Show error to user for updating
	        System.err.println("Error updating article: " + e.getMessage());
	        throw e;  // Rethrow the exception for further handling if needed
	    }
	}
	
	/**
	 * Backsup articles in the database into a file of the user's choice and based on the 
	 * groups of a user's choice
	 * @param filename
	 * @param groupInput
	 * @throws SQLException
	 */
	public void backupArticles(String filename, String groupInput) throws SQLException {
		//Gather all grouped articles
		List<Article> articles = getArticlesByGroup(groupInput);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			//Use a buffered writer to write info out for each article
	        for (Article article : articles) {
	        	//Write out article ID, header, group, title, description,
	        	//keywords, body, references, and sensitive info
	        	writer.write("ID: " + article.getID() + "\n");
	        	System.out.println(article.getID());
	            writer.write("Header: " + new String(article.getHeader()) + "\n");
	            writer.write("Group: " + new String(article.getGroup()) + "\n");
	            writer.write("Title: " + new String(article.getTitle()) + "\n");
	            writer.write("Description: " + new String(article.getShortDescription()) + "\n");
	            writer.write("Keywords: " + new String(article.getKeywords()) + "\n");
	            writer.write("Body: " + new String(article.getBody()) + "\n");
	            writer.write("References: " + new String(article.getReferences()) + "\n");
	            writer.write("Sensitive Title: " + new String(article.getSensitiveTitle()) + "\n");
	            writer.write("Sensitive Description: " + new String(article.getSensitiveDescription()) + "\n");
	            writer.write("\n"); // Separate each article with a newline
	        }
	        //Writing successful
	        System.out.println("Backup completed successfully.");
	    } catch (IOException e) {
	    	//Writing not successful
	        System.err.println("Backup failed: " + e.getMessage());
	    }
		
	}
	
	/**
	 * Backs up all articles not based on groups. Every single current article will be 
	 * saved from the database
	 * @param filename
	 * @throws SQLException
	 */
	public void backupAllArticles(String filename) throws SQLException {
		//Gather all current articles
		List<Article> articles = returnArticles();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			//Write every current article to the file
	        for (Article article : articles) {
	        	//All Article information contained in file
	        	writer.write("ID: " + article.getID() + "\n");
	        	System.out.println(article.getID());
	            writer.write("Header: " + new String(article.getHeader()) + "\n");
	            writer.write("Group: " + new String(article.getGroup()) + "\n");
	            writer.write("Title: " + new String(article.getTitle()) + "\n");
	            writer.write("Description: " + new String(article.getShortDescription()) + "\n");
	            writer.write("Keywords: " + new String(article.getKeywords()) + "\n");
	            writer.write("Body: " + new String(article.getBody()) + "\n");
	            writer.write("References: " + new String(article.getReferences()) + "\n");
	            writer.write("Sensitive Title: " + new String(article.getSensitiveTitle()) + "\n");
	            writer.write("Sensitive Description: " + new String(article.getSensitiveDescription()) + "\n");
	            writer.write("\n"); // Separate each article with a newline
	        }
	        //Writing successful
	        System.out.println("Backup of all articles completed successfully.");
	    } catch (IOException e) {
	    	//Writing failed
	        System.err.println("Backup failed: " + e.getMessage());
	    }
		
	}
	
	/**
	 * Clears all articles in the database
	 */
	public void clearExistingArticles() {
		//Deletion of all articles query
        String deleteAllArticlesQuery = "DELETE FROM helpArticles";
        
        try (PreparedStatement statement = connection.prepareStatement(deleteAllArticlesQuery)) {
        	//Execute deletion of all articles
            int rowsDeleted = statement.executeUpdate();
            //Let user know how many articles are gone
            System.out.println(rowsDeleted + " articles cleared from the database.");
        } catch (SQLException e) {
            System.err.println("Error clearing articles from the database: " + e.getMessage());
        }
    }
	
	/**
	 * Merges the articles from the backup in with the old files if they were not cleared
	 * @param articles
	 * @throws IOException
	 * @throws SQLException
	 */
	public void mergeArticles(List<Article> articles) throws IOException, SQLException {
		//Check for matching Article ID
        for (Article article : articles) {
            if (!articleExists(article.getID())) {
            	//Not a duplicate so add into database
                addArticle(article);
            }
        }
    }
	
	/**
	 * Check if an article exists based on it's unique ID value
	 * @param articleId
	 * @return
	 * @throws SQLException
	 */
	public boolean articleExists(long articleId) throws SQLException {
		//ID check query
        String checkQuery = "SELECT COUNT(*) FROM helpArticles WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
        	//Check for article ID in database
            statement.setLong(1, articleId);
            return statement.executeQuery().next() && statement.getResultSet().getInt(1) > 0;
        }
    }
	
	/**
	 * Restores articles from the file of the user's choosing into the database.
	 * @param backupFileName
	 * @return
	 */
	public List<Article> restoreHelpArticles(String backupFileName) {
		//Create list to hold the articles
        List<Article> restoredArticles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(backupFileName))) {
        	//Read in new articles using Article article
            String line;
            Article article = null; // Keep the article reference outside the loop

            while ((line = reader.readLine()) != null) {
                // Check for the start of a new article
                if (line.startsWith("ID: ")) {
                    if (article != null) {
                        restoredArticles.add(article); // Add the previous article if exists
                    }
                    article = new Article(); // Create a new article for the next entry
                    
                    // Parse the ID and set it
                    String idStr = line.substring("ID: ".length()).trim();
                    article.setID(Integer.parseInt(idStr)); // Assuming ID is stored as long
                } else if (article != null) { // Ensure article exists before setting its properties
                	//Read in article values
                    if (line.startsWith("Header: ")) {
                        article.setHeader(line.substring("Header: ".length()).toCharArray());
                    } else if (line.startsWith("Group: ")) {
                        article.setGroup(line.substring("Group: ".length()).toCharArray());
                    } else if (line.startsWith("Title: ")) {
                        article.setTitle(line.substring("Title: ".length()).toCharArray());
                    } else if (line.startsWith("Description: ")) {
                        article.setShortDescription(line.substring("Description: ".length()).toCharArray());
                    } else if (line.startsWith("Keywords: ")) {
                        article.setKeywords(line.substring("Keywords: ".length()).toCharArray());
                    } else if (line.startsWith("Body: ")) {
                        article.setBody(line.substring("Body: ".length()).toCharArray());
                    } else if (line.startsWith("References: ")) {
                        article.setReferences(line.substring("References: ".length()).toCharArray());
                    } else if (line.startsWith("Sensitive Title: ")) {
                        article.setSensitiveTitle(line.substring("Sensitive Title: ".length()).toCharArray());
                    } else if (line.startsWith("Sensitive Description: ")) {
                        article.setSensitiveDescription(line.substring("Sensitive Description: ".length()).toCharArray());
                    }
                }
            }
            
            // Add the last article if it exists
            if (article != null) {
                restoredArticles.add(article);
            }
             
            //Restoration successful
            System.out.println("Help articles restored successfully from " + backupFileName);
        } catch (IOException e1) {
        	//Restoration not successful
            System.err.println("Failed to restore help articles: " + e1.getMessage());
        }

        return restoredArticles; // Return the list of restored articles
    }

    /**
     * Close connection to the database at the end of session
     */
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
