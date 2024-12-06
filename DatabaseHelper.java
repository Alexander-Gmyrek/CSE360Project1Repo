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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
import javafx.scene.control.Alert;


/*******
* <p> DatabaseHelper Class. </p>
*
* <p> Description: This is the class that sets up the database to hold all the articles. It can create  tables, 
* list  articles, add articles to the tables, gather the articles, backup the articles, and restore the articles.
* In addition, to articles, it also stores information for help article management, such as the users and their access
* to specific articles. Messaging systems are also put into place here and store user queries for instructor
* knowledge.
*
* @author <Zach>
* @version 1.00 10/16/2024
* @version 2.00 11/15/2024
*/

//UML DESCRIPTION
// This is the secure database section of the major architectural components and is key to the functionality of the system.
// We wanted to implement a secure database that would hold our user, message, and help article information securely. To have this
// design flow through to the code, we created an H2 database which stores users, messages, and help articles which allows for
// users to be added, articles to be added, edited, etc, and for user's activity to be logged and stored securely for instructors
// to view.
class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/firstDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	private Connection connection = null;
	private Statement statement = null; 
	private EncryptionHelper encryptionHelper;
	private String ivVal = "znvwew12445ge2083";
	
	/**
	 * Connect to the database so that the user can perform article operations upon it
	 * @throws Exception 
	 */
	public void connectToDatabase() throws Exception {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			encryptionHelper = new EncryptionHelper();
			createTables();  // Create the necessary tables if they don't exist
			skillsTable();
			messageTable();
			userTable();
			userAccessTable();
			searchQueryTable();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Creates SQL tables that will hold the article information for the database
	 * @throws SQLException
	 */
	// UML DESCRIPTION
	// This is the SQL table that will hold our help article information
	private void createTables() throws SQLException {
	    String articleTable = "CREATE TABLE IF NOT EXISTS helpArticles ("
	            + "id BIGINT PRIMARY KEY, " // BIGLONG for unique long ID
	            + "header VARCHAR(255), " // Header value stored in VARCHAR
	            + "groupingIdentifier VARCHAR(100), "  // Group value stored in VARCHAR
	            + "title VARCHAR(255), "   // Title value stored in VARCHAR
	            + "description VARCHAR(500), "   // Description value stored in VARCHAR
	            + "keywords VARCHAR(255), "  //Keywords value stored in VARCHAR
	            + "body TEXT, "  // Body value stored in TEXT
	            + "references TEXT, "   //References value stored in TEXT
	            + "sensitiveTitle VARCHAR(255), "   //Sensitive title value stored in VARCHAR
	            + "author VARCHAR(500) "  //Sensitive description value stored in VARCHAR
	            + ")";
	    //Execute the change in the database
	    statement.execute(articleTable); 
	}
	
	/**
	 * Create SQL table that will hold the messages that students create for the database
	 * @throws SQLException
	 */
	// UML DESCRIPTION
	// This is the table that stores the messages that users send to our system
	private void messageTable() throws SQLException {
		//Create a table containing a student user name, the message content, and the time stamp of the message.
		String messageTable = "CREATE TABLE IF NOT EXISTS Messages ("
			    + "studentID VARCHAR(50), "
			    + "messageContent TEXT, "
			    + "timestamp TIMESTAMP "
			    + ")";
		//Create the table
		statement.execute(messageTable);
	}
	
	/**
	 * Creates a table that will store user search queries for instructors to view
	 * @throws SQLException
	 */
	// UML DESCRIPTION
	// This is the table that stores user search queries into the system
	private void searchQueryTable() throws SQLException {
		//Create a table containing a student user name, the message content, and the time stamp of the message.
		String messageTable = "CREATE TABLE IF NOT EXISTS searchQuery ("
			    + "studentID VARCHAR(50), "
			    + "contentLevel VARCHAR(50), "
			    + "query VARCHAR(50), "
			    + "groups VARCHAR(50), "
			    + "timestamp TIMESTAMP "
			    + ")";
		//Create the table
		statement.execute(messageTable);
	}
	
	/**
	 * Save the search query made by the user into the database table for instructors to view
	 * @param username    user who made the search
	 * @param contentLevel   content level in the search
	 * @param query    specific search query used
	 * @param group    group of article searched for
	 */
	// UML DESCRIPTION
	// For our system, we wanted to log user activity as seen in our Monitoring and Logging service in the major architectural 
	// components. To implement this, the next two methods below allow for queries to be saved into the database securely and
	// allow for instructors to retrieve the queries from the database to view and learn more about their students.
	public void saveSearchQuery(String username, String contentLevel, String query, String groups) {
		//Query for inserting the user message
        String sql = "INSERT INTO searchQuery (studentID, contentLevel, query, groups, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        	//Get the username, message, and timestamp
            pstmt.setString(1, username);
            pstmt.setString(2, contentLevel);
            pstmt.setString(3, query);
            pstmt.setString(4, groups);
            pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            //Show user if message was saved
            showAlert("Success", "Message sent successfully.");
        } catch (SQLException e) {
        	//Message not saved to database
            e.printStackTrace();
            showAlert("Error", "Failed to send message.");
        }
    }
	
	/**
	 * Returns all search queries in the database currently
	 * @return
	 */
	public List<Search> getAllSearchQueries() {
		//Array list that will store the messages
        List<Search> searches = new ArrayList<>();
        
        //Query for searching for the student messages
        String sql = "SELECT studentID, contentLevel, query, groups, timestamp FROM searchQuery ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
            	//Gather the student message information
                String studentID = rs.getString("studentID");
                String level = rs.getString("contentLevel");
                String query = rs.getString("query");
                String group = rs.getString("groups");
                String timestamp = rs.getTimestamp("timestamp").toString();

                // Add each message to the ArrayList
                searches.add(new Search(studentID, level, query, group, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //Return the list of messages students have made
        return searches;
    }
	
	/**
	 * Create a table that holds the current users in the system
	 * @throws SQLException
	 */
	// UML DESCRIPTION
	// This is the table that stores users in the system.
	private void userTable() throws SQLException {
		//Store usernames 
		String userTable = "CREATE TABLE IF NOT EXISTS Users ("
				+ "username VARCHAR(255), "
				+ "password VARCHAR(255) "
				+ ")";
		//Create the table
		statement.execute(userTable);
	}
	
	/**
	 * Stores user skills for instructors to view
	 * @throws SQLException
	 */
	// UML DESCRIPTION
	// This is the table that stores the skills of users in the system
	private void skillsTable() throws SQLException {
		//Create table holding skill values for java, eclipse. javafx, and github
		String skillsTable = "CREATE TABLE IF NOT EXISTS Skills ("
				+ "username VARCHAR(255), "
				+ "javaSkill VARCHAR(255), "
				+ "eclipseSkill VARCHAR(255), "
				+ "javaFxSkill VARCHAR(255), "
				+ "gitHubSkill VARCHAR(255) "
				+ ")";
		//Create the table into the system
		statement.execute(skillsTable);
	}
	
	/**
	* Adds a user's skill levels to the Skills table in the database.
	 *
	 * @param username      The username of the user.
	 * @param javaSkill     The skill level for Java.
	 * @param eclipseSkill  The skill level for Eclipse.
	 * @param javaFxSkill   The skill level for JavaFX.
	 * @param gitHubSkill   The skill level for GitHub.
	 */
	// UML DESCRIPTION
	// According to our Logging and Monitoring section of the architecture components, we wanted to be able to get more
	// insights into the users. The next two methods allow for the skills of users to be stored in our secure database
	// and for instructors to be able to retrieve the skills and be able to view them.
	public void addSkills(String username, String javaSkill, String eclipseSkill, String javaFxSkill, String gitHubSkill) {
	    // SQL query for inserting a new row into the Skills table
	    String sql = "INSERT INTO Skills (username, javaSkill, eclipseSkill, javaFxSkill, gitHubSkill) VALUES (?, ?, ?, ?, ?)";

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        // Set the values for each column
	        stmt.setString(1, username);
	        stmt.setString(2, javaSkill);
	        stmt.setString(3, eclipseSkill);
	        stmt.setString(4, javaFxSkill);
	        stmt.setString(5, gitHubSkill);

	        // Execute the insertion
	        stmt.executeUpdate();

	        // Confirm success to the user
	        showAlert("Success", "Skills for user: " + username + " have been added.");
	    } catch (SQLException e) {
	        // Handle any SQL errors
	        e.printStackTrace();
	        showAlert("Error", "Failed to add skills for user: " + username);
	    }
	}
	
	/**
	 * Queries all skill level information from the Skills table and formats it into a readable string.
	 *
	 * @return A string containing all skill level information, formatted for display.
	 */
	public String getAllSkills() {
	    // SQL query to fetch all records from the Skills table
	    String sql = "SELECT * FROM Skills";
	    StringBuilder result = new StringBuilder();

	    try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
	        // Append a header row for readability
	        result.append("Username\tJava\tEclipse\tJavaFX\tGitHub\n");
	        result.append("--------------------------------------------------\n");

	        // Process each row in the result set
	        while (rs.next()) {
	            String username = rs.getString("username");
	            String javaSkill = rs.getString("javaSkill");
	            String eclipseSkill = rs.getString("eclipseSkill");
	            String javaFxSkill = rs.getString("javaFxSkill");
	            String gitHubSkill = rs.getString("gitHubSkill");

	            // Append each row of data to the result
	            result.append(username).append("\t")
	                  .append(javaSkill).append("\t")
	                  .append(eclipseSkill).append("\t")
	                  .append(javaFxSkill).append("\t")
	                  .append(gitHubSkill).append("\n");
	        }

	    } catch (SQLException e) {
	        // Handle any SQL errors
	        e.printStackTrace();
	        return "Error retrieving skills from the database.";
	    }

	    // Return the formatted result
	    return result.toString();
	}
	
	/**
	 * Adds a user into the database based on information like their username
	 * @param username   user identifier
	 * @throws Exception 
	 */
	// UML DESCRIPTION
	// Security has always been the main priority of this system so we thought it would be crucial that for the code,
	// user's passwords are stored in an encrypted manner so that they can't be leaked and have anyone simply just
	// read the plaintext.
    public void addUser(String username, String password) throws Exception {
    	
    	//Encrypt the user's password in the database
    	byte[] pass = password.getBytes();
    	String encryptedPassword = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(pass, EncryptionUtils.getInitializationVector(ivVal.toCharArray()))
		);
    	
    	//Query for insertion into table
        String sql = "INSERT INTO Users (username) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        	//Set username and update according table
            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword);
            stmt.executeUpdate();
            //Adding user was a success
            showAlert("Success", "User: " + username +  " added");
        } catch (SQLException e) {
        	//Adding user failed
            //e.printStackTrace();
            //showAlert("Error", "Failed to add User.");
        }
    }
    
    /**
    * Returns all the current users in the database except for the user that is currently logged in
    * @param currentUser    user that is logged in currently
    * @return
    */
    public ArrayList<String> getAllUsersExceptCurrent(String currentUser) {
    	//Create a list to store the users later on
        ArrayList<String> usersList = new ArrayList<>();

        //Query for finding all users
        String query = "SELECT username FROM Users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
            	//Search for users based on username
                String username = rs.getString("username");
                String password = rs.getString("password");
                // Exclude the current user from the list
                if (!username.equals(currentUser)) {
                    usersList.add(username);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Return the list of all users
        return usersList;
    }
    
    /**
     * Get all users in the access table for ensuring a user has access
     * @return
     */
    public ArrayList<String> getAllUsers() {
    	//Create list of users to be returned later on
        ArrayList<String> users = new ArrayList<>();
        
        //Select distinct usernames from the list
        String query = "SELECT DISTINCT username FROM User_Article_Access";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
            	//Add based on username
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Return the list of all users
        return users;
    }
	
    /**
     * Table for managing users based on username and their access to certain articles.
     * @throws SQLException
     */
    // UML DESCRIPTION
    // In our Role Management Service component and user use cases in the design, we wanted users to be able to have access
    // to articles and this access could either be General (view general articles in system) or Sensitive (certain users
    // can view certain articles in the system. The methods below allow for user access to be stored so it can be managed,
    // user access to be assigned by those who have admin access to the group, and for access to be removed if an instructor/admin
    // with access wants a student to not be able to view the article for any reason.
	private void userAccessTable() throws SQLException{
		//Create the table of users and access where the values are unique
		String userAccessTable = "CREATE TABLE IF NOT EXISTS User_Article_Access ("
		    + "username VARCHAR(255), "
		    + "title VARCHAR(255), "
		    + "accessType VARCHAR(255), "
		    + "PRIMARY KEY (username, title) "
		    + ")";
	    //Create the tab;e
	    statement.execute(userAccessTable);
	}
	
	/**
	 * Check if a user has access to a specific article to ensure they can read the unencrypted article
	 * if necessary
	 * @param username    user where access is being checked
	 * @param articleTitle    article whose access is being checked
	 * @return
	 */
	public boolean hasAccess(String username, char[] articleTitle) {
		//Search query for user and article title
	    String sql = "SELECT accessType FROM User_Article_Access WHERE username = ? AND title = ?";
	    String title = new String(articleTitle);

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setString(1, username);
	        stmt.setString(2, title);

	        try (ResultSet rs = stmt.executeQuery()) {
	            // Iterate through the results to see if there's a match for the required access type
	            while (rs.next()) {
	            	//Check for proper access type which is View or Admin
	                String accessType = rs.getString("accessType");
	                if ((accessType.equalsIgnoreCase("View")) || (accessType.equalsIgnoreCase("Admin"))) {
	                	//Has access
	                    return true;
	                }
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;  // No matching access type found
	}
	
	/**
	 * If a user has administrator access to a special group, they can assign access to their group
	 * through this method using a username, article title, and access type they want to give.
	 * @param username    username to have access assigned
	 * @param usernamei2 
	 * @param articleTitle     article to have access assigned to
	 * @param accessType    access type of the user to be assigned
	 */
    public void assignAccess(String username, char[] articleTitle, String accessType) {
    	//Search query for assigning access
        String sql = "INSERT INTO User_Article_Access (username, title, accessType) VALUES (?, ?, ?)";
        //Create string version of title for displaing purposes.
        String str = new String(articleTitle);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, str);
            stmt.setString(3, accessType); // 'view' or 'admin'
            stmt.executeUpdate();
            //Show successful access message with username, title, and access type
            showAlert("Success", "Access assigned: " + username + " -> " + str + " (" + accessType + ")");
        } catch (SQLException e) {
        	//Access could not be assigned
            e.printStackTrace();
            showAlert("Error", "Failed to add User Access.");
        }
    }
    
    /**
     * Allows an instructor or admin with proper permissions to remove users from their group
     * with this method.
     * @param username    username of user to be removed from group
     * @param usernamei2 
     * @param articleTitle     article where user will be removed from.
     */
    public void removeAccess(String username, char[] articleTitle) {
    	//Query for searching for user that will be removed
        String sql = "DELETE FROM User_Article_Access WHERE username = ? AND title = ?";
        
        //Create string version of title for displaying
        String str = new String(articleTitle);
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, str);
            int rowsAffected = stmt.executeUpdate();
            
            //Show user that change in access was successful if there was a deletion
            //that occurred
            if (rowsAffected > 0) {
                //showAlert("Success", "Access removed for: " + username + " -> " + str);
            } else {
            	//No deletion so let user user know since access didn't exist
                showAlert("Warning", "No access found for user: " + username + " on article: " + str);
            }
        } catch (SQLException e) {
        	//Access existed but user couldn't be deleted
            e.printStackTrace();
            showAlert("Error", "Failed to remove User Access.");
        }
    }
    
    /**
     * Method to ensure that an instructor (or admin if given privileges) has the proper admin rights for 
     * deleting, adding, viewing, and editing groups and their articles/students with sensitive access rights.
     * @param instructorUsername     instructor user
     * @param articleTitle     article whose access is being checked
     * @return
     */
    // UML DESCRIPTION
    // Security is a big priority so we wanted in our code, to ensure that only users with proper admin access
    // can manage article viewing rights.
    public boolean isInstructorAdmin(String instructorUsername, char[] articleTitle) {
    	//Query for checking if a user has admin rights
        String query = "SELECT accessType FROM User_Article_Access WHERE username = ? AND title = ?";   
        String str = new String(articleTitle);
            // Set the parameters for the query (instructor's username and article title)
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, instructorUsername);
            stmt.setString(2, str);
            try (ResultSet rs = stmt.executeQuery()) {
                // Check if the instructor has admin access for the specified article
                while (rs.next()) {
                    String accessType = rs.getString("accessType");
                    //Check if user has correct rights
                    if (accessType.equals("Admin")) {
                    	return true; //User has proper admin rights
                    }
                }
            }
           } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Default: not an admin
    }
	
    /**
     * A place to save messages that users write to the help system about things they are confused about
     * or questions they have so that instructors can view them.
     * @param username    user who sent the message
     * @param message     message sent by the user
     */
	public void saveMessage(String username, String message) {
		//Query for inserting the user message
        String sql = "INSERT INTO Messages (studentID, messageContent, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        	//Get the username, message, and timestamp
            pstmt.setString(1, username);
            pstmt.setString(2, message);
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            //Show user if message was saved
            showAlert("Success", "Message sent successfully.");
        } catch (SQLException e) {
        	//Message not saved to database
            e.printStackTrace();
            showAlert("Error", "Failed to send message.");
        }
    }
	
	/**
	 * Add a article to the database based of an article object that is passed in with all the information filled
	 * @param article
	 * @throws Exception
	 */
	// UML DESCRIPTION
	// Help articles are the heart of our system and is the main functionality as seen in our architecture and design.
	// This method allows articles to be added into the system so that they can be removed, updated, backed up, etc,
	// have access assigned/removed, and be read by students so that the system can function.
	public void addArticle(Article article) throws SQLException {
	    //Database insertion process
	    String insertArticle = "INSERT INTO helpArticles (id, header, groupingIdentifier, title, description, keywords, body, references, sensitiveTitle, author) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
	    	//Set the ? values in the database based on the article information entered in by the user.
	    	//Convert char array to bytes so it isn't stored in plaintext
	        pstmt.setLong(1, article.getID());
	        pstmt.setBytes(2, new String(article.getHeader()).getBytes());  
	        pstmt.setBytes(3, new String(article.getGroup()).getBytes());
	        pstmt.setBytes(4, new String(article.getTitle()).getBytes());    
	        pstmt.setBytes(5, new String(article.getShortDescription()).getBytes());  
	        pstmt.setBytes(6, new String(article.getKeywords()).getBytes());  
	        pstmt.setBytes(7, new String(article.getBody()).getBytes());     
	        pstmt.setBytes(8, new String(article.getReferences()).getBytes());
	        pstmt.setBytes(9, new String(article.getSensitiveTitle()).getBytes());  
	        pstmt.setBytes(10, new String(article.getAuthor()).getBytes()); 

	        // Execute the update to insert the article
	        pstmt.executeUpdate();
	    }
	  
	}
	
	/**
	 * Add articles but encrypt the body since the article has a sensitive identifier
	 * @param article   article whose body will be encrypted
	 * @throws Exception
	 */
	// UML DESCRIPTION
	// Security is a big priority for our system so articles that are given the Sensitive description by the creator
	// have their body encrypted so that only users with proper access can view the article. Anyone without access will not
	// be able to decipher the help article body.
	public void addArticleEncrypted(Article article) throws Exception {
		
		//Convert body to a string value so it can be converted to bytes
		String body = new String(article.getBody());
		//Encrypt the body using the encryptionHelper
		String encryptedBody = Base64.getEncoder().encodeToString(
				encryptionHelper.encrypt(body.getBytes(), EncryptionUtils.getInitializationVector(ivVal.toCharArray()))
		);
		
	    //Database insertion process
	    String insertArticle = "INSERT INTO helpArticles (id, header, groupingIdentifier, title, description, keywords, body, references, sensitiveTitle, author) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(insertArticle)) {
	    	//Set the ? values in the database based on the article information entered in by the user.
	    	//Convert char array to bytes so it isn't stored in plaintext
	        pstmt.setLong(1, article.getID());
	        pstmt.setBytes(2, new String(article.getHeader()).getBytes());  
	        pstmt.setBytes(3, new String(article.getGroup()).getBytes());
	        pstmt.setBytes(4, new String(article.getTitle()).getBytes());    
	        pstmt.setBytes(5, new String(article.getShortDescription()).getBytes());  
	        pstmt.setBytes(6, new String(article.getKeywords()).getBytes());  
	        pstmt.setBytes(7, encryptedBody.getBytes());     
	        pstmt.setBytes(8, new String(article.getReferences()).getBytes());
	        pstmt.setBytes(9, new String(article.getSensitiveTitle()).getBytes());  
	        pstmt.setBytes(10, new String(article.getAuthor()).getBytes()); 

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
	// UML DESCRIPTION
	// As seen in the user/instructor use cases and state/sequence diagrams, we needed users to be able to search for articles
	// to get the information they need. To implement this in the code, these next methods allow for specific articles and article
	// groups to be queried in the database and returned to the user for their searching and reading needs.
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
	            char[] author = new String(rs.getBytes("author")).toCharArray();

	            // Create an Article object based on above information
	            Article article = new Article(id, header, group, title, shortDescription, keywords,
	            		body, references, sensitiveTitle, author, 0);

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
	        		char[] author = new String(rs.getBytes("author")).toCharArray();

	        		// Create an Article object
	        		Article article = new Article(id, header, group, title, shortDescription, keywords,
	        				body, references, sensitiveTitle, author, 0);
	        		//Add the article to the current list
	        		articles.add(article);
	        	}
	    }		
	    //Return every current article in the database
	    return articles;
	}
	
	/**
	 * Return all the messages that users have written and uploaded to the database
	 * @return
	 */
	public List<Message> getAllMessages() {
		//Array list that will store the messages
        List<Message> messages = new ArrayList<>();
        
        //Query for searching for the student messages
        String sql = "SELECT studentID, messageContent, timestamp FROM Messages ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
            	//Gather the student message information
                String studentID = rs.getString("studentID");
                String messageContent = rs.getString("messageContent");
                String timestamp = rs.getTimestamp("timestamp").toString();

                // Add each message to the ArrayList
                messages.add(new Message(studentID, messageContent, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //Return the list of messages students have made
        return messages;
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
	                char[] author = new String(rs.getBytes("author")).toCharArray();

	                //Add the article to the list to be returned
	                articles.add(new Article(id, header, group.toCharArray(), title, shortDescription, keywords,
	                        body, references, sensitiveTitle, author, 0));
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
	// UML DESCRIPTION
	// The final methods below for this class allow for the the articles in the system to be managed and updated so that the
	// Help Article Management section of the architecture can function properly. The correlation for the code is that we included
	// methods that update the database based on deleting the article, updating the article, backing up the article, and restoring
	// the article so that user's can have the most up to date and accurate help articles.
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
	            + "author = ? "
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
	        pstmt.setBytes(9, new String(article.getAuthor()).getBytes());

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
	public void backupArticles(String filename, String groupInput, User currentUser) throws SQLException {
		//Gather all grouped articles
		List<Article> articles = getArticlesByGroup(groupInput);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			//Use a buffered writer to write info out for each article
	        for (Article article : articles) {
	        	String sensTitle = new String(article.getSensitiveTitle());
	        	boolean hasAccess = hasAccess(currentUser.getUsername(), article.getTitle());
	        	if (hasAccess == true || sensTitle.equals("General")) {
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
	            	writer.write("Author: " + new String(article.getAuthor()) + "\n");
	            	writer.write("\n"); // Separate each article with a newline
	        	}
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
	public void backupAllArticles(String filename, User currentUser) throws SQLException {
		//Gather all current articles
		List<Article> articles = returnArticles();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
			//Write every current article to the file
	        for (Article article : articles) {
	        	String sensTitle = new String(article.getSensitiveTitle());
	        	boolean hasAccess = hasAccess(currentUser.getUsername(), article.getTitle());
	        	if (hasAccess == true || sensTitle.equals("General")) {
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
	            writer.write("Author: " + new String(article.getAuthor()) + "\n");
	            writer.write("\n"); // Separate each article with a newline
	        }
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
	public boolean articleExists(int articleId) throws SQLException {
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
                    } else if (line.startsWith("Author: ")) {
                        article.setAuthor(line.substring("Author: ".length()).toCharArray());
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
	 * Method for sending alerts to update user on database entries, removals, updates, etc
	 * @param title
	 * @param content
	 */
	private void showAlert(String title, String content) {
		//Create the alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //Set it's details like the title and content
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        //Show user the alert
        alert.showAndWait();
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
