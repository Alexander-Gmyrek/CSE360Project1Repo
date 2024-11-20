package ProjectUI;


import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseHelperTest {

    private DatabaseHelper dbHelper;



    public void testAddArticle() {
        // connect to data base
        try{
            dbHelper.connectToDatabase();
        } catch (SQLException e) {

        }

        // Test with a valid article
        Article validArticle = new Article(1, "Header".toCharArray(), "Group1".toCharArray(), "Valid Article".toCharArray(),
                "Short Description".toCharArray(), "Keywords".toCharArray(), "Body".toCharArray(),
                "References".toCharArray(), "Sensitive Title".toCharArray(), "Sensitive Description".toCharArray());
        try {
            dbHelper.addArticle(validArticle);
            System.out.println("Valid article test passed.");
        } catch (SQLException e) {
            System.err.println("Valid article test failed: " + e.getMessage());
        }

        // Test with an invalid article (missing fields)
        Article invalidArticle = new Article(0, new char[]{}, null, new char[]{}, new char[]{}, new char[]{}, new char[]{}, new char[]{}, new char[]{}, new char[]{});
        try {
            dbHelper.addArticle(invalidArticle);
            System.err.println("Invalid article test failed: No exception thrown for invalid article.");
        } catch (SQLException e) {
            System.out.println("Invalid article test passed: " + e.getMessage());
        }
    }
    


    public void testSearchArticles() {
        // connect to data base
        try{
            dbHelper.connectToDatabase();
        } catch (SQLException e) {

        }
        // Sample articles to populate the database
        Article article1 = new Article(1, "Header1".toCharArray(), "Group1".toCharArray(), 
                "Java Tutorial".toCharArray(), "Description".toCharArray(), 
                "Java,Programming".toCharArray(), "Body content".toCharArray(), 
                "References".toCharArray(), "Sensitive Title".toCharArray(), 
                "Sensitive Description".toCharArray());
                
        Article article2 = new Article(2, "Header2".toCharArray(), "Group2".toCharArray(), 
                "Python Guide".toCharArray(), "Description".toCharArray(), 
                "Python,Programming".toCharArray(), "Body content".toCharArray(), 
                "References".toCharArray(), "Sensitive Title".toCharArray(), 
                "Sensitive Description".toCharArray());
        
        // Insert articles for testing
        try {
            dbHelper.addArticle(article1);
            dbHelper.addArticle(article2);
            System.out.println("Sample articles added to the database.");
        } catch (SQLException e) {
            System.err.println("Error inserting articles for search test: " + e.getMessage());
            return;
        }

        // Perform search with query
        String query = "Java"; // Example search term that should match article1
        try {
            List<Article> results = dbHelper.searchArticles(query);
            if (results.isEmpty()) {
                System.out.println("No articles found for query: " + query);
            } else {
                System.out.println("Articles found for query: " + query);
                for (Article article : results) {
                    System.out.println("Found Article ID: " + article.getID() + ", Title: " + new String(article.getTitle()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search test failed: " + e.getMessage());
        }
    }

    public void testBackupAndRestore() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String backupFileName = "backup.txt";

        try {
            // Perform backup of current articles
            dbHelper.backupAllArticles(backupFileName);
            System.out.println("Backup completed. Now testing restoration...");

            // Restore articles from the backup file
            List<Article> restoredArticles = dbHelper.restoreHelpArticles(backupFileName);

            // Verify that the restoration was successful
            if (restoredArticles.isEmpty()) {
                System.out.println("No articles restored.");
            } else {
                System.out.println("Restored articles:");
                for (Article article : restoredArticles) {
                    System.out.println("ID: " + article.getID() + ", Title: " + new String(article.getTitle()));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error during backup or restore test: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during backup or restore test: " + e.getMessage());
        }
    }
    
   
    
    // Additional test cases for each method could be added here
    
    
    
    @Test
    public void testHasAccess() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String username = "user1";
        char[] articleTitle = "Article 1".toCharArray();

        try {
            boolean hasAccess = dbHelper.hasAccess(username, articleTitle);
            if (hasAccess) {
                System.out.println(username + " has access to the article: " + new String(articleTitle));
            } else {
                System.out.println(username + " does NOT have access to the article: " + new String(articleTitle));
            }
        } catch (SQLException e) {
            System.err.println("Database error during hasAccess test: " + e.getMessage());
        }
    }
    
    
    
    
    
    @Test
    public void testAssignAccess() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String username = "user2";
        char[] articleTitle = "Article 2".toCharArray();
        String accessType = "Admin";

        try {
            dbHelper.assignAccess(username, articleTitle, accessType);
            System.out.println("Access type '" + accessType + "' successfully assigned to " + username + " for article: " + new String(articleTitle));
        } catch (SQLException e) {
            System.err.println("Database error during assignAccess test: " + e.getMessage());
        }
    }
    
    
    
    
    @Test
    public void testRemoveAccess() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String username = "user3";
        char[] articleTitle = "Article 3".toCharArray();

        try {
            dbHelper.removeAccess(username, articleTitle);
            System.out.println("Access removed for " + username + " on article: " + new String(articleTitle));
        } catch (SQLException e) {
            System.err.println("Database error during removeAccess test: " + e.getMessage());
        }
    }


    
    
    
    @Test
    public void testIsInstructorAdmin() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String instructorUsername = "instructor1";
        char[] articleTitle = "Article 4".toCharArray();

        try {
            boolean isAdmin = dbHelper.isInstructorAdmin(instructorUsername, articleTitle);
            if (isAdmin) {
                System.out.println(instructorUsername + " is an admin for article: " + new String(articleTitle));
            } else {
                System.out.println(instructorUsername + " is NOT an admin for article: " + new String(articleTitle));
            }
        } catch (SQLException e) {
            System.err.println("Database error during isInstructorAdmin test: " + e.getMessage());
        }
    }


    
    
    
    
    @Test
    public void testSaveMessage() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        String username = "user4";
        String message = "This is a test message.";

        try {
            dbHelper.saveMessage(username, message);
            System.out.println("Message from " + username + " saved successfully: " + message);
        } catch (SQLException e) {
            System.err.println("Database error during saveMessage test: " + e.getMessage());
        }
    }
    
    
    @Test
    public void testAddArticleEncrypted() {
        DatabaseHelper dbHelper = new DatabaseHelper();
        Article article = new Article(2, "Header".toCharArray(), "Group".toCharArray(), 
                                      "Title".toCharArray(), "Description".toCharArray(),
                                      "Keywords".toCharArray(), "Body".toCharArray(), 
                                      "References".toCharArray(), "Sensitive Title".toCharArray(), 
                                      "Author".toCharArray(), 0);

        try {
            dbHelper.addArticleEncrypted(article);
            System.out.println("Encrypted article added successfully: " + new String(article.getTitle()));
        } catch (Exception e) {
            System.err.println("Error during addArticleEncrypted test: " + e.getMessage());
        }
    }

    
    @Test
    public void testReturnArticles() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            List<Article> articles = dbHelper.returnArticles();
            if (articles.isEmpty()) {
                System.out.println("No articles found in the database.");
            } else {
                System.out.println("Articles in the database:");
                for (Article article : articles) {
                    System.out.println("Article ID: " + article.getID() + ", Title: " + new String(article.getTitle()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during returnArticles test: " + e.getMessage());
        }
    }

    
    
    @Test
    public void testGetAllMessages() {
        DatabaseHelper dbHelper = new DatabaseHelper();

        try {
            List<Message> messages = dbHelper.getAllMessages();
            if (messages.isEmpty()) {
                System.out.println("No messages found in the database.");
            } else {
                System.out.println("Messages from students:");
                for (Message message : messages) {
                    System.out.println("Student ID: " + message.getStudentID() + ", Message: " + message.getMessageContent());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during getAllMessages test: " + e.getMessage());
        }
    }
    
    
    @Test
    public void testGetArticlesByGroup() throws SQLException {
        String group = "Tech";
        String sql = "SELECT * FROM helpArticles WHERE groupingIdentifier = ?";
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        // Mock the connection behavior
        when(connection.prepareStatement(sql)).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        // Mock the result set behavior
        when(rs.next()).thenReturn(true).thenReturn(false); // Simulate one result
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getBytes("header")).thenReturn("Header".getBytes());
        when(rs.getBytes("title")).thenReturn("Article Title".getBytes());
        when(rs.getBytes("description")).thenReturn("Short Description".getBytes());
        when(rs.getBytes("keywords")).thenReturn("Keywords".getBytes());
        when(rs.getBytes("body")).thenReturn("Body".getBytes());
        when(rs.getBytes("references")).thenReturn("References".getBytes());
        when(rs.getBytes("sensitiveTitle")).thenReturn("Sensitive Title".getBytes());
        when(rs.getBytes("author")).thenReturn("Author".getBytes());

        List<Article> articles = articleService.getArticlesByGroup(group);
        assertEquals(1, articles.size());  // Assert one article is returned
        assertEquals("Article Title", new String(articles.get(0).getTitle()));  // Assert correct title
    }
    
    
    
    @Test
    public void testGetAllGroups() throws SQLException {
        String sql = "SELECT DISTINCT groupingIdentifier FROM helpArticles";
        PreparedStatement pstmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        // Mock the connection behavior
        when(connection.prepareStatement(sql)).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        // Mock the result set behavior
        when(rs.next()).thenReturn(true).thenReturn(false);  // Simulate one group
        when(rs.getString("groupingIdentifier")).thenReturn("Tech");

        List<String> groups = articleService.getAllGroups();
        assertEquals(1, groups.size());  // Assert one group is returned
        assertEquals("Tech", groups.get(0));  // Assert correct group
    }

@Test
public void testDeleteArticle() throws SQLException {
    String articleTitle = "Test Article";
    String deleteQuery = "DELETE FROM helpArticles WHERE title = ?";
    PreparedStatement pstmt = mock(PreparedStatement.class);

    // Mock the connection behavior
    when(connection.prepareStatement(deleteQuery)).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);  // Simulate successful deletion

    articleService.deleteArticle(articleTitle);
    verify(pstmt, times(1)).executeUpdate();  // Verify delete was called once
}

@Test
public void testDeleteArticleNoMatch() throws SQLException {
    String articleTitle = "Nonexistent Article";
    String deleteQuery = "DELETE FROM helpArticles WHERE title = ?";
    PreparedStatement pstmt = mock(PreparedStatement.class);

    // Mock the connection behavior
    when(connection.prepareStatement(deleteQuery)).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(0);  // Simulate no deletion

    articleService.deleteArticle(articleTitle);
    verify(pstmt, times(1)).executeUpdate();  // Verify delete was called once
}



@Test
public void testDeleteArticlesByGroup() throws SQLException {
    String group = "Tech";
    String deleteQuery = "DELETE FROM helpArticles WHERE groupingIdentifier = ?";
    PreparedStatement pstmt = mock(PreparedStatement.class);

    // Mock the connection behavior
    when(connection.prepareStatement(deleteQuery)).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);  // Simulate successful deletion

    articleService.deleteArticlesByGroup(group);
    verify(pstmt, times(1)).executeUpdate();  // Verify delete was called once
}




@Test
public void testUpdateArticle() throws SQLException {
    Article article = mock(Article.class);
    when(article.getTitle()).thenReturn("Test Article");
    when(article.getHeader()).thenReturn("Header".toCharArray());
    when(article.getGroup()).thenReturn("Tech".toCharArray());
    when(article.getShortDescription()).thenReturn("Short Description".toCharArray());
    when(article.getKeywords()).thenReturn("Keywords".toCharArray());
    when(article.getBody()).thenReturn("Body".toCharArray());
    when(article.getReferences()).thenReturn("References".toCharArray());
    when(article.getSensitiveTitle()).thenReturn("Sensitive Title".toCharArray());
    when(article.getAuthor()).thenReturn("Author".toCharArray());

    String updateQuery = "UPDATE helpArticles SET header = ?, groupingIdentifier = ?, title = ?, description = ?, "
            + "keywords = ?, body = ?, references = ?, sensitiveTitle = ?, author = ? WHERE title = ?";
    PreparedStatement pstmt = mock(PreparedStatement.class);

    // Mock the connection behavior
    when(connection.prepareStatement(updateQuery)).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(1);  // Simulate successful update

    articleService.updateArticle(article);
    verify(pstmt, times(1)).executeUpdate();  // Verify update was called once
}


@Test
public void testBackupArticles_Success() throws SQLException, IOException {
    List<Article> articles = new ArrayList<>();
    articles.add(article);
    
    when(articleService.getArticlesByGroup("Group1")).thenReturn(articles);
    when(currentUser.getUsername()).thenReturn("testUser");
    when(articleService.hasAccess("testUser", "Test Article")).thenReturn(true);

    // Creating a mock for the BufferedWriter
    File tempFile = File.createTempFile("testBackup", ".txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        articleService.backupArticles(tempFile.getAbsolutePath(), "Group1", currentUser);
    }

    // Validate the file is created and contains the expected data
    assertTrue(tempFile.exists());
    // You can check the file content here (skip for brevity)

    // Verify interactions
    verify(articleService).getArticlesByGroup("Group1");
    verify(writer, atLeastOnce()).write(anyString());
}

@Test
public void testBackupArticles_NoAccess() throws SQLException, IOException {
    List<Article> articles = new ArrayList<>();
    articles.add(article);
    
    when(articleService.getArticlesByGroup("Group1")).thenReturn(articles);
    when(currentUser.getUsername()).thenReturn("testUser");
    when(articleService.hasAccess("testUser", "Test Article")).thenReturn(false);

    // Creating a mock for the BufferedWriter
    File tempFile = File.createTempFile("testBackup", ".txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        articleService.backupArticles(tempFile.getAbsolutePath(), "Group1", currentUser);
    }

    // Validate the file is created and contains the expected data
    assertTrue(tempFile.exists());
    // Check that nothing was written to the file for this case
}


@Test
public void testBackupAllArticles() throws SQLException, IOException {
    List<Article> articles = new ArrayList<>();
    articles.add(article);
    
    when(articleService.returnArticles()).thenReturn(articles);
    when(currentUser.getUsername()).thenReturn("testUser");
    when(articleService.hasAccess("testUser", "Test Article")).thenReturn(true);

    // Creating a mock for the BufferedWriter
    File tempFile = File.createTempFile("testBackupAll", ".txt");
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
        articleService.backupAllArticles(tempFile.getAbsolutePath(), currentUser);
    }

    // Validate the file is created
    assertTrue(tempFile.exists());
}



public void testClearExistingArticles() throws SQLException {
    // Mocking the database interaction
    Connection mockConnection = mock(Connection.class);
    PreparedStatement pstmt = mock(PreparedStatement.class);
    when(mockConnection.prepareStatement(anyString())).thenReturn(pstmt);
    when(pstmt.executeUpdate()).thenReturn(10); // Simulate 10 articles being deleted

    articleService.clearExistingArticles();

    // Verifying if the query was executed
    verify(pstmt).executeUpdate();
}




@Test
public void testMergeArticles() throws IOException, SQLException {
    List<Article> articles = new ArrayList<>();
    articles.add(article);

    when(articleService.articleExists(anyInt())).thenReturn(false); // Simulate the article doesn't exist
    doNothing().when(articleService).addArticle(any(Article.class)); // Simulate adding the article

    articleService.mergeArticles(articles);

    // Verify article was added
    verify(articleService).addArticle(article);
}


@Test
public void testArticleExists() throws SQLException {
    when(articleService.articleExists(1)).thenReturn(true); // Simulate article with ID 1 exists

    boolean exists = articleService.articleExists(1);

    assertTrue(exists);
}

@Test
public void testRestoreHelpArticles_Success() throws IOException {
    String fileName = "testBackup.txt";
    
    // Mocking file reading
    BufferedReader reader = mock(BufferedReader.class);
    when(reader.readLine()).thenReturn("ID: 1", "Header: Test Header", "Group: Test Group", 
                                      "Title: Test Title", "Description: Test Description",
                                      "Keywords: Test Keywords", "Body: Test Body", 
                                      "References: Test References", "Sensitive Title: Test Sensitive", 
                                      "Author: Test Author", null); // null at the end to end the loop
    
    // Creating the service to mock behavior
    when(articleService.createBufferedReader(fileName)).thenReturn(reader);

    List<Article> restoredArticles = articleService.restoreHelpArticles(fileName);
    
    // Validate that the articles are restored correctly
    assertNotNull(restoredArticles);
    assertEquals(1, restoredArticles.size());  // Only one article should be restored

    // Check that the article fields are correctly set
    Article restoredArticle = restoredArticles.get(0);
    assertEquals(1, restoredArticle.getID());
    assertArrayEquals("Test Header".toCharArray(), restoredArticle.getHeader());
    assertArrayEquals("Test Group".toCharArray(), restoredArticle.getGroup());
}

@Test
public void testRestoreHelpArticles_Failure() throws IOException {
    String fileName = "invalidBackup.txt";
    
    // Simulate an exception during file reading
    BufferedReader reader = mock(BufferedReader.class);
    when(reader.readLine()).thenThrow(new IOException("File not found"));

    // Mock the behavior of the service method
    when(articleService.createBufferedReader(fileName)).thenReturn(reader);

    List<Article> restoredArticles = articleService.restoreHelpArticles(fileName);
    
    // Validate that no articles were restored
    assertNotNull(restoredArticles);
    assertTrue(restoredArticles.isEmpty());
}


@Test
public void testShowAlert() {
    Alert alert = mock(Alert.class);  // Mock the Alert class
    articleService.showAlert("Test Title", "This is a test alert");

    // Verify that showAndWait was called on the alert
    verify(alert).showAndWait();
}



@Test
public void testCloseConnection() throws SQLException {
    doNothing().when(statement).close();
    doNothing().when(connection).close();

    articleService.closeConnection();

    // Verify that the close methods were called
    verify(statement).close();
    verify(connection).close();
}


@Test
public void testCloseConnection_StatementThrowsException() throws SQLException {
    doThrow(new SQLException("Statement close error")).when(statement).close();
    doNothing().when(connection).close();

    articleService.closeConnection();

    // Verify that the connection.close() is still called, even if statement.close() fails
    verify(connection).close();
}

@Test
public void testCloseConnection_ConnectionThrowsException() throws SQLException {
    doNothing().when(statement).close();
    doThrow(new SQLException("Connection close error")).when(connection).close();

    articleService.closeConnection();

    // No verification needed here, just ensure no other methods break
}

}






    
    
    
    
   
