package ProjectUI;

/*******
* <p> Message Class. </p>
*
* <p> Description: This is the class that sets up the values for messages that users send to the system
*
* @author <Zach>
* @version 1.00 11/15/2024
*/

// UML DESCRIPTION
// In our architecture, we wanted to implement a logging service to manage and view student activity in the system. This design 
// flowed to the code as in this class, it creates search objects which are the user's search queries they make in the system.
// They are initialized here upon a student search and are stored in the system for instructors to be able to view.

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