package ProjectUI;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;

/*******
* <p> ClassJUnit Class. </p>
*
* <p> Description: This is the class that will be testing all required functionality of the system. This includes JUnit
* tests for all four phases and their requirements.
*
* @author <Zach>
* @version 1.00 11/18/2024
* @version 2.00 12/3/2024
*/
public class ClassJUnit {
	// **************************************************
	// PHASE 1 Requirements
	// **************************************************
	
	private UserManager userManager;
	private DatabaseHelper database;
	private EncryptionHelper encryptionHelper;
	// JDBC driver name and database URL 
		static final String JDBC_DRIVER = "org.h2.Driver";   
		static final String DB_URL = "jdbc:h2:~/firstDatabase";  

		//  Database credentials 
		static final String USER = "sa"; 
		static final String PASS = ""; 
		private Connection connection = null;
		private Statement statement = null; 
	 	/**
	 	 * Creates a new UserManager instance to be used for testing purposes
	 	 * @throws SQLException 
	 	 */
	    @BeforeEach
	    void setUp() throws SQLException {
	    	// userManager will be the new UserManager for this class
	        userManager = new UserManager();
	        database = new DatabaseHelper();
	        encryptionHelper = null;
	        connection = DriverManager.getConnection("jdbc:h2:mem:testdb");
	        statement = connection.createStatement();
	    }

	    /**
	     * Test if the first user to login to properly marked as the first user and have an admin account
	     * made for them
	     */
	    @Test
	    void testIsFirstUser() {
	    	// Test for first user
	        assertTrue(userManager.isFirstUser(), "Expected no users initially (isFirstUser should return true).");

	        //Create admin account
	        userManager.createAccount("admin", "password123", new ArrayList<>());
	        assertFalse(userManager.isFirstUser(), "Expected users to exist after creating an account (isFirstUser should return false).");
	    }

	    /**
	     * Testing the ability of the system to create and store new user accounts
	     */
	    @Test
	    void testCreateAccount() {
	    	//Create a new student account
	        userManager.createAccount("user1", "password123", List.of("Student"));

	        //Try and locate the user in the system
	        User user = userManager.findUser("user1");
	        assertNotNull(user, "Expected to find the created user.");
	        assertEquals("user1", user.getUsername(), "Username should match.");
	        assertTrue(user.checkPassword("password123"), "Password should match.");
	        assertTrue(user.getRoles().contains("Student"), "Role should include 'Student'.");
	    }

	    /**
	     * Test the ability of a user to log in back to their account
	     */
	    @Test
	    void testLoginSuccess() {
	    	//Create new user account
	        userManager.createAccount("user1", "password123", List.of("Instructor"));

	        //Try and login as the user
	        User loggedInUser = userManager.login("user1", "password123");
	        assertNotNull(loggedInUser, "Login should succeed for valid credentials.");
	        assertEquals("user1", loggedInUser.getUsername(), "Logged-in user should have the correct username.");
	    }

	    /**
	     * Testing for a failure in login with non-matching passwords and non-existent account
	     */
	    @Test
	    void testLoginFailure() {
	    	//Create the user account
	        userManager.createAccount("user1", "password123", List.of("Instructor"));

	        //Attempt to login with wrong password
	        User loggedInUser = userManager.login("user1", "wrongPassword");
	        assertNull(loggedInUser, "Login should fail for invalid password.");

	        //Attempt to login with non-existent account
	        loggedInUser = userManager.login("nonExistentUser", "password123");
	        assertNull(loggedInUser, "Login should fail for non-existent username.");
	    }

	    /**
	     * Test for resetting a user password via an admin account
	     */
	    @Test
	    void testResetUserPassword() {
	        // Get the time plus some leeway so it doesn't expire
	    	LocalDateTime validExpiration = LocalDateTime.now().plusMinutes(10);
	    	// Get an expired time
	        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(10);

	        //Reset a user's password with a valid expiration date
	        String otp = userManager.resetUserPassword("user1", validExpiration);
	        assertNotNull(otp, "OTP should be generated for a valid expiration time.");

	        //Invalid expiration time so should fail upon reset
	        String errorMessage = userManager.resetUserPassword("user1", expiredTime);
	        assertEquals("Expiration date and time cannot be in the past.", errorMessage, "Expected error message for expired time.");
	    }

	    /**
	     * Test for deleting a user account in the system
	     */
	    @Test
	    void testDeleteUser() {
	    	// Create an account for deleting
	        userManager.createAccount("user1", "password123", List.of("Admin"));
	        //Delete the new account
	        assertTrue(userManager.deleteUser("user1"), "Deleting an existing user should return true.");

	        // Try and delete a non-existing account
	        assertFalse(userManager.deleteUser("user1"), "Deleting a non-existent user should return false.");
	    }

	    /**
	     * Test for listing all current users in the system
	     */
	    @Test
	    void testListUsers() {
	    	//Create two new accounts
	        userManager.createAccount("user1", "password123", List.of("Student"));
	        userManager.createAccount("user2", "password456", List.of("Instructor"));

	        //Get the current users
	        List<User> users = userManager.listUsers();
	        assertEquals(2, users.size(), "There should be 2 users in the system.");
	        assertEquals("user1", users.get(0).getUsername(), "First user's username should match.");
	        assertEquals("user2", users.get(1).getUsername(), "Second user's username should match.");
	    }

	    /**
	     * Test for updating the role of a user via an admin account
	     */
	    @Test
	    void testUpdateUserRoles() {
	    	//Create a new student account
	        userManager.createAccount("user1", "password123", List.of("Student"));
	        //Update roles to be an "Admin" and an "Instructor"
	        userManager.updateUserRoles("user1", Arrays.asList("Admin", "Instructor"));

	        //Try and find the user
	        User user = userManager.findUser("user1");
	        assertNotNull(user, "User should exist.");
	        //Roles should match new ones
	        assertTrue(user.getRoles().containsAll(List.of("Admin", "Instructor")), "Roles should include 'Admin' and 'Instructor'.");
	    }
	    
	    // **************************************************
		// PHASE 2 Requirements
	    // **************************************************
	    
	    /**
	     * Test for adding an article into our help system
	     * @throws SQLException
	     */
	    @Test
	    public void testAddArticle() throws SQLException {
	        // Create new article with example values inserted in
	        Article article = new Article(1, "Header".toCharArray(), "Group1".toCharArray(),
	                "Title1".toCharArray(), "Short Desc".toCharArray(),
	                "Keywords".toCharArray(), "Body".toCharArray(),
	                "References".toCharArray(), "Sensitive Title".toCharArray(),
	                "Author".toCharArray(), 0);
	        
	        // Add to database
	        database.addArticle(article);
	        List<Article> articles = database.returnArticles();

	        // Check if article was added
	        assertEquals(1, articles.size());
	        assertArrayEquals("Title1".toCharArray(), articles.get(0).getTitle());
	    }
	    
	    /**
	     * Test for retrieving articles from the database
	     * @throws SQLException
	     */
	    @Test
	    public void testReturnArticles() throws SQLException {
	        // Create an example article
	        Article article1 = new Article(1, "Header1".toCharArray(), "Group1".toCharArray(),
	                "Title1".toCharArray(), "Short Desc1".toCharArray(),
	                "Keywords1".toCharArray(), "Body1".toCharArray(),
	                "References1".toCharArray(), "Sensitive Title1".toCharArray(),
	                "Author1".toCharArray(), 0);
	        //Create another example article
	        Article article2 = new Article(2, "Header2".toCharArray(), "Group2".toCharArray(),
	                "Title2".toCharArray(), "Short Desc2".toCharArray(),
	                "Keywords2".toCharArray(), "Body2".toCharArray(),
	                "References2".toCharArray(), "Sensitive Title2".toCharArray(),
	                "Author2".toCharArray(), 0);

	        //Add both to the database
	        database.addArticle(article1);
	        database.addArticle(article2);

	        //Retrieve them
	        List<Article> articles = database.returnArticles();

	        //Articles should be in list
	        assertEquals(2, articles.size());
	    }
	    
	    /**
	     * Test for deleting an article in the help system
	     * @throws SQLException
	     */
	    @Test
	    public void testDeleteArticle() throws SQLException {
	        //Create a new article
	        Article article = new Article(1, "Header".toCharArray(), "Group1".toCharArray(),
	                "Title1".toCharArray(), "Short Desc".toCharArray(),
	                "Keywords".toCharArray(), "Body".toCharArray(),
	                "References".toCharArray(), "Sensitive Title".toCharArray(),
	                "Author".toCharArray(), 0);
	        
	        //Add it to the database
	        database.addArticle(article);

	        //Delete based on title
	        database.deleteArticle("Title1");

	        //Article should not be in list
	        List<Article> articles = database.returnArticles();
	        assertEquals(0, articles.size());
	    }
		
	    /**
	     * Test for getting all articles in a group
	     * @throws SQLException
	     */
	    @Test
	    public void testGetArticlesByGroup() throws SQLException {
	        //Create two new articles
	        Article article1 = new Article(1, "Header1".toCharArray(), "Group1".toCharArray(),
	                "Title1".toCharArray(), "Short Desc1".toCharArray(),
	                "Keywords1".toCharArray(), "Body1".toCharArray(),
	                "References1".toCharArray(), "Sensitive Title1".toCharArray(),
	                "Author1".toCharArray(), 0);

	        Article article2 = new Article(2, "Header2".toCharArray(), "Group2".toCharArray(),
	                "Title2".toCharArray(), "Short Desc2".toCharArray(),
	                "Keywords2".toCharArray(), "Body2".toCharArray(),
	                "References2".toCharArray(), "Sensitive Title2".toCharArray(),
	                "Author2".toCharArray(), 0);

	        //Add articles to the database
	        database.addArticle(article1);
	        database.addArticle(article2);

	        //Get them by group value
	        List<Article> group1Articles = database.getArticlesByGroup("Group1");

	        //Articles should appear
	        assertEquals(1, group1Articles.size());
	        assertArrayEquals("Title1".toCharArray(), group1Articles.get(0).getTitle());
	    }
	    
	    /**
	     * Test for updating an article
	     * @throws SQLException
	     */
	    @Test
	    public void testUpdateArticle() throws SQLException {
	        //Create a new article
	        Article article = new Article(1, "Header".toCharArray(), "Group1".toCharArray(),
	                "Title1".toCharArray(), "Short Desc".toCharArray(),
	                "Keywords".toCharArray(), "Body".toCharArray(),
	                "References".toCharArray(), "Sensitive Title".toCharArray(),
	                "Author".toCharArray(), 0);
	        
	        //Add it to the database
	        database.addArticle(article);

	        //Change the title
	        article.setTitle("Updated Title".toCharArray());
	        database.updateArticle(article);

	        //
	        List<Article> articles = database.returnArticles();
	        assertEquals(1, articles.size());
	        //New Title should match
	        assertArrayEquals("Updated Title".toCharArray(), articles.get(0).getTitle());
	    }
	    
	    /**
	     * Backs up articles in the database into a file based on a user's group selection.
	     * @param filename the file to write the backup to
	     * @param groupInput the group to filter articles by
	     * @param currentUser the user performing the backup
	     * @throws SQLException if an SQL error occurs
	     */
	    public void backupArticles(String filename, String groupInput, User currentUser) throws SQLException {
	        List<Article> articles = database.getArticlesByGroup(groupInput);
	        //Backup article by group
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
	            for (Article article : articles) {
	            	//Search for articles with matching group
	                if (database.hasAccess(currentUser.getUsername(), article.getTitle()) || 
	                    new String(article.getSensitiveTitle()).equals("General")) {
	                    writeArticleToFile(writer, article);
	                }
	            }
	            System.out.println("Backup completed successfully.");
	        } catch (IOException e) {
	            System.err.println("Backup failed: " + e.getMessage());
	        }
	    }
	    
	    /**
	     * Backs up all articles in the database to a specified file.
	     * @param filename the file to write the backup to
	     * @param currentUser the user performing the backup
	     * @throws SQLException if an SQL error occurs
	     */
	    public void backupAllArticles(String filename, User currentUser) throws SQLException {
	        List<Article> articles = database.returnArticles();
	        //Backup all articles
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
	            for (Article article : articles) {
	                if (database.hasAccess(currentUser.getUsername(), article.getTitle()) || 
	                    new String(article.getSensitiveTitle()).equals("General")) {
	                	//Write them to file
	                    writeArticleToFile(writer, article);
	                }
	            }
	            System.out.println("Backup of all articles completed successfully.");
	        } catch (IOException e) {
	            System.err.println("Backup failed: " + e.getMessage());
	        }
	    }
	    
	    /**
	     * Clears all articles from the database.
	     */
	    public void clearExistingArticles() {
	    	//Clear articles before restoring if needed
	        String deleteQuery = "DELETE FROM helpArticles";
	        //Delete all articles from database
	        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
	            int rowsDeleted = statement.executeUpdate();
	            System.out.println(rowsDeleted + " articles cleared from the database.");
	        } catch (SQLException e) {
	            System.err.println("Error clearing articles: " + e.getMessage());
	        }
	    }
	    
	    /**
	     * Merges articles from a backup with existing articles in the database.
	     * @param articles the list of articles to merge
	     * @throws IOException if an I/O error occurs
	     * @throws SQLException if an SQL error occurs
	     */
	    public void mergeArticles(List<Article> articles) throws IOException, SQLException {
	        for (Article article : articles) {
	            if (!database.articleExists(article.getID())) {
	                database.addArticle(article);
	            }
	        }
	    }
	    
	    /**
	     * Restores articles from a backup file into the database.
	     * @param backupFileName the file containing the backup
	     * @return a list of restored articles
	     */
	    public List<Article> restoreHelpArticles(String backupFileName) {
	        List<Article> restoredArticles = new ArrayList<>();
	        //Prepare to restore articles
	        try (BufferedReader reader = new BufferedReader(new FileReader(backupFileName))) {
	            String line;
	            Article article = null;

	            //Collect article information
	            while ((line = reader.readLine()) != null) {
	                if (line.startsWith("ID: ")) {
	                    if (article != null) restoredArticles.add(article);
	                    article = new Article();
	                    article.setID(Integer.parseInt(line.substring("ID: ".length()).trim()));
	                } else if (article != null) {
	                    parseArticleLine(article, line);
	                }
	            }
	            //Add restored articles to list
	            if (article != null) restoredArticles.add(article);
	            System.out.println("Help articles restored successfully from " + backupFileName);
	        } catch (IOException e) {
	            System.err.println("Failed to restore help articles: " + e.getMessage());
	        }
	        return restoredArticles;
	    }
	    
	    /**
	     * Helper method for backing up articles
	     */
	    private void writeArticleToFile(BufferedWriter writer, Article article) throws IOException {
	    	//Write all article information
	        writer.write("ID: " + article.getID() + "\n");
	        writer.write("Header: " + new String(article.getHeader()) + "\n");
	        writer.write("Group: " + new String(article.getGroup()) + "\n");
	        writer.write("Title: " + new String(article.getTitle()) + "\n");
	        writer.write("Description: " + new String(article.getShortDescription()) + "\n");
	        writer.write("Keywords: " + new String(article.getKeywords()) + "\n");
	        writer.write("Body: " + new String(article.getBody()) + "\n");
	        writer.write("References: " + new String(article.getReferences()) + "\n");
	        writer.write("Sensitive Title: " + new String(article.getSensitiveTitle()) + "\n");
	        writer.write("Author: " + new String(article.getAuthor()) + "\n\n");
	    }

	    /**
	     * Helper method for restoring articles
	     * @param article
	     * @param line
	     */
	    private void parseArticleLine(Article article, String line) {
	    	//Parse all article information
	        if (line.startsWith("Header: ")) article.setHeader(line.substring("Header: ".length()).toCharArray());
	        else if (line.startsWith("Group: ")) article.setGroup(line.substring("Group: ".length()).toCharArray());
	        else if (line.startsWith("Title: ")) article.setTitle(line.substring("Title: ".length()).toCharArray());
	        else if (line.startsWith("Description: ")) article.setShortDescription(line.substring("Description: ".length()).toCharArray());
	        else if (line.startsWith("Keywords: ")) article.setKeywords(line.substring("Keywords: ".length()).toCharArray());
	        else if (line.startsWith("Body: ")) article.setBody(line.substring("Body: ".length()).toCharArray());
	        else if (line.startsWith("References: ")) article.setReferences(line.substring("References: ".length()).toCharArray());
	        else if (line.startsWith("Sensitive Title: ")) article.setSensitiveTitle(line.substring("Sensitive Title: ".length()).toCharArray());
	        else if (line.startsWith("Author: ")) article.setAuthor(line.substring("Author: ".length()).toCharArray());
	    }
	    
	    
	    // **************************
		// PHASE 3 Requirements
	    // **************************
	    
	    /**
	     * Test case for saving a search query.
	     * Ensures the query is saved correctly and retrievable.
	     */
	    @Test
	    void testSaveSearchQuery() throws SQLException {
	        database.searchQueryTable(); // Ensure table exists

	        // Save a test search query
	        database.saveSearchQuery("testUser", "Beginner", "Java Basics", "Java");

	        // Verify the search query is saved
	        String sql = "SELECT * FROM searchQuery";
	        try (ResultSet rs = statement.executeQuery(sql)) {
	            assertTrue(rs.next(), "Search query should be saved in the table.");
	            assertEquals("testUser", rs.getString("studentID"));
	            assertEquals("Beginner", rs.getString("contentLevel"));
	            assertEquals("Java Basics", rs.getString("query"));
	            assertEquals("Java", rs.getString("groups"));
	        }
	    }

	    /**
	     * Test case for retrieving all search queries.
	     * Ensures all saved search queries are retrieved correctly.
	     */
	    @Test
	    void testGetAllSearchQueries() throws SQLException {
	    	//Ensure table exists
	        database.searchQueryTable(); // Ensure table exists

	        // Save multiple test search queries
	        database.saveSearchQuery("user1", "Intermediate", "Spring Boot", "Java");
	        database.saveSearchQuery("user2", "Beginner", "Hello World", "Java");

	        // Retrieve all search queries
	        List<Search> searches = database.getAllSearchQueries();

	        // Verify the results
	        assertEquals(2, searches.size(), "Should retrieve 2 search queries.");
	        assertEquals("Intermediate", searches.get(0).getContentLevel());
	    }


	    /**
	     * Test case for assigning user access to an article.
	     * Ensures the user access is saved correctly in the database.
	     */
	    @Test
	    void testAssignAccess() throws SQLException {
	        database.userAccessTable(); // Ensure table exists

	        // Assign access to a user
	        database.assignAccess("testUser", "Test Article".toCharArray(), "View");

	        // Verify the access is saved in the table
	        String sql = "SELECT * FROM User_Article_Access WHERE username = 'testUser' AND title = 'Test Article'";
	        try (ResultSet rs = statement.executeQuery(sql)) {
	            assertTrue(rs.next(), "Access should be assigned to the user.");
	            assertEquals("View", rs.getString("accessType"));
	        }
	    }
	    
	    /**
	     * Test for ensuring a user has access to article
	     * @throws SQLException
	     */
	    @Test
	    void testHasAccess() throws SQLException {
	        database.userAccessTable(); // Ensure table exists

	        // Assign access and test it
	        database.assignAccess("testUser", "Test Article".toCharArray(), "View");
	        assertTrue(database.hasAccess("testUser", "Test Article".toCharArray()), "User should have access to the article.");

	        // Test with a user or article that doesn't exist
	        assertFalse(database.hasAccess("nonexistentUser", "Nonexistent Article".toCharArray()), "User should not have access.");
	    }
	    
	    /**
	     * Test case for removing user access to an article.
	     * Ensures the user access is removed successfully from the database.
	     */
	    @Test
	    void testRemoveAccess() throws SQLException {
	        database.userAccessTable(); // Ensure table exists

	        // Assign and then remove access
	        database.assignAccess("testUser", "Test Article".toCharArray(), "View");
	        database.removeAccess("testUser", "Test Article".toCharArray());

	        // Verify the access is removed
	        String sql = "SELECT * FROM User_Article_Access WHERE username = 'testUser' AND title = 'Test Article'";
	        try (ResultSet rs = statement.executeQuery(sql)) {
	            assertFalse(rs.next(), "Access should be removed from the user.");
	        }
	    }

	    /**
	     * Test case for checking admin rights for an instructor.
	     * Ensures the correct determination of admin rights based on the database records.
	     */
	    @Test
	    void testIsInstructorAdmin() throws SQLException {
	        database.userAccessTable(); // Ensure table exists

	        // Assign admin access to an instructor
	        database.assignAccess("adminUser", "Admin Article".toCharArray(), "Admin");

	        // Verify admin access
	        assertTrue(database.isInstructorAdmin("adminUser", "Admin Article".toCharArray()), "User should have admin access.");

	        // Test with non-admin access
	        database.assignAccess("testUser", "Test Article".toCharArray(), "View");
	        assertFalse(database.isInstructorAdmin("testUser", "Test Article".toCharArray()), "User should not have admin access.");
	    }

	    /**
	     * Test case for saving a message to the "Messages" table.
	     * Ensures the message is stored successfully in the database.
	     */
	    @Test
	    void testSaveMessage() throws SQLException {
	        // Create Messages table
	        String messagesTable = "CREATE TABLE IF NOT EXISTS Messages ("
	                + "studentID VARCHAR(255), "
	                + "messageContent TEXT, "
	                + "timestamp TIMESTAMP)";
	        statement.execute(messagesTable);

	        // Save a message
	        database.saveMessage("testUser", "This is a test message.");

	        // Verify the message is saved
	        String sql = "SELECT * FROM Messages WHERE studentID = 'testUser'";
	        try (ResultSet rs = statement.executeQuery(sql)) {
	            assertTrue(rs.next(), "Message should be saved in the table.");
	            assertEquals("This is a test message.", rs.getString("messageContent"));
	        }
	    }
	    
	    /**
	     * Add article with sensitive information into the system
	     * @throws Exception
	     */
	    @Test
	    void testAddArticleEncrypted() throws Exception {
	        // Prepare the test article data
	        Article testArticle = new Article(
	                113213,                          // ID
	                "Header".toCharArray(),      // Header
	                "Group1".toCharArray(),      // Group
	                "Title".toCharArray(),       // Title
	                "Description".toCharArray(), // Short description
	                "Keywords".toCharArray(),    // Keywords
	                "Sensitive Body".toCharArray(), // Body (to be encrypted)
	                "References".toCharArray(),  // References
	                "Sensitive Title".toCharArray(), // Sensitive title
	                "Author".toCharArray(),       // Author
	                0
	        );

	        // Add the article to the database
	        database.addArticleEncrypted(testArticle);

	        // Retrieve and verify the article from the database
	        String sql = "SELECT * FROM helpArticles WHERE id = ?";
	        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	            pstmt.setLong(1, testArticle.getID());

	            try (ResultSet rs = pstmt.executeQuery()) {
	                assertTrue(rs.next(), "Article should exist in the database.");

	                // Verify encrypted body
	                String encryptedBody = new String(rs.getBytes("body"));
	                String decryptedBody = new String(encryptionHelper.decrypt(
	                        Base64.getDecoder().decode(encryptedBody),
	                        EncryptionUtils.getInitializationVector("ivVal".toCharArray())
	                ));
	                assertEquals("Sensitive Body", decryptedBody, "Decrypted body should match the original.");

	                // Verify other fields
	                assertEquals("Header", new String(rs.getBytes("header")));
	                assertEquals("Group1", new String(rs.getBytes("groupingIdentifier")));
	                assertEquals("Title", new String(rs.getBytes("title")));
	                assertEquals("Description", new String(rs.getBytes("description")));
	                assertEquals("Keywords", new String(rs.getBytes("keywords")));
	                assertEquals("References", new String(rs.getBytes("references")));
	                assertEquals("Sensitive Title", new String(rs.getBytes("sensitiveTitle")));
	                assertEquals("Author", new String(rs.getBytes("author")));
	            }
	        }
	    }
	    
	    // **************************   
	    // PHASE 4 Requirements
	    // **************************
	    
	    /**
	     * Test if user skills are added into the system
	     * @throws SQLException
	     */
	    @Test
	    void testAddSkills() throws SQLException {
	        // Add a user's skills to the database
	        database.addSkills("testUser", "Intermediate", "Beginner", "Advanced", "Intermediate");

	        // Verify the skills were inserted correctly
	        String query = "SELECT * FROM Skills WHERE username = ?";
	        try (PreparedStatement stmt = connection.prepareStatement(query)) {
	            stmt.setString(1, "testUser");

	            try (ResultSet rs = stmt.executeQuery()) {
	                assertTrue(rs.next(), "Skills for testUser should exist.");
	                assertEquals("Intermediate", rs.getString("javaSkill"));
	                assertEquals("Beginner", rs.getString("eclipseSkill"));
	                assertEquals("Advanced", rs.getString("javaFxSkill"));
	                assertEquals("Intermediate", rs.getString("gitHubSkill"));
	            }
	        }
	    }

	    /**
	     * Test if skills can be retrieved from the system
	     * @throws SQLException
	     */
	    @Test
	    void testGetAllSkills() throws SQLException {
	        // Insert multiple users' skills into the database
	        database.addSkills("user1", "Intermediate", "Beginner", "Advanced", "Intermediate");
	        database.addSkills("user2", "Beginner", "Intermediate", "Beginner", "Advanced");

	        // Retrieve all skills
	        String skills = database.getAllSkills();

	        // Verify the returned string contains the expected data
	        String expected = """
	                Username\tJava\tEclipse\tJavaFX\tGitHub
	                --------------------------------------------------
	                user1\tIntermediate\tBeginner\tAdvanced\tIntermediate
	                user2\tBeginner\tIntermediate\tBeginner\tAdvanced
	                """;
	        assertEquals(expected.trim(), skills.trim(), "Retrieved skills should match expected output.");
	    }
	}
		
	
	
	
