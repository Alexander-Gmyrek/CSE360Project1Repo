package ProjectUI;

/*******
* <p> Message Class. </p>
*
* <p> Description: This is the class that sets up the values for messages that users send to the system
*
* @author <Zach>
* @version 1.00 11/15/2024
*/

public class Message {
	//Private instance variables
    private String studentID;
    private String messageContent;
    private String timestamp;

    /**
     * Message constructor that updates studentID, messageContent, and time stamp
     * @param studentID
     * @param messageContent
     * @param timestamp
     */
    public Message(String studentID, String messageContent, String timestamp) {
        this.studentID = studentID;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    /**
     * Get the student user info (username)
     * @return
     */
    public String getStudentID() { return studentID; }
    
    /**
     * Get the student message content info
     * @return
     */
    public String getMessageContent() { return messageContent; }
    
    /**
     * Get the student timestamp info
     * @return
     */
    public String getTimestamp() { return timestamp; }

    @Override
    /**
     * Append message info in a formatted way
     */
    public String toString() {
        return "Student ID: " + studentID + "\nMessage: " + messageContent + "\nTimestamp: " + timestamp + "\n";
    }
}