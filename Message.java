package ProjectUI;

/*******
* <p> Message Class. </p>
*
* <p> Description: This is the class that sets up the values for messages that users send to the system
*
* @author <Zach>
* @version 1.00 11/15/2024
*/

//UML DESCRIPTION
// In our architecture (specifically the Message Handling Service in the architecture components) and the state diagram for students,
// we wanted students to be able to send messaged to instructors when they needed help. To have this design flow to the code,
// this class creates the messages that users make to be stored in the system which allows them to be delivered to the proper
// instructors. 
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