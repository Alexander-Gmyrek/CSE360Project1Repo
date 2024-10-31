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
}
