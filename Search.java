package ProjectUI;

/*******
* <p> Message Class. </p>
*
* <p> Description: This is the class that sets up the values for messages that users send to the system
*
* @author <Zach>
* @version 1.00 11/15/2024
*/

public class Search {
	//Private instance variables
    private String studentID;
    private String level;
    private String query;
    private String group;
    private String timestamp;

    /**
     * Message constructor that updates studentID, messageContent, and time stamp
     * @param studentID
     * @param messageContent
     * @param timestamp
     */
    public Search(String studentID, String level, String query, String group, String timestamp) {
        this.studentID = studentID;
        this.level = level;
        this.query = query;
        this.group = group;
        this.timestamp = timestamp;
    }

    @Override
    /**
     * Append message info in a formatted way
     */
    public String toString() {
        return "Student ID: " + studentID + "\nLevel: " +  level + "\nQuery: " + query + "\nGroup: " 
        		+ group + "\nTimestamp: " + timestamp + "\n";
    }
}