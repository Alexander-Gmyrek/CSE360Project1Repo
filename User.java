package ProjectUI;

import java.util.ArrayList;
import java.util.List;

/*******
* <p> User Class. </p>
*
* <p> Description:   Represents a user in the application with associated details such as username, password,
*  roles, and personal information.
*
* @author <Zach>
* @version 1.00 10/16/2024
*/


public class User {
    private String username;
    private String password;
    private List<String> roles;
    private String email;
    private String firstName;
    private String lastName;
    private boolean setupComplete = false;

    /**
     * Constructs a User with the specified username, password, and initial roles.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param roles    A list of roles assigned to the user.
     */
    public User(String username, String password, List<String> roles) {
        this.username = username; // Set the username
        this.password = password; // Set the password
        this.roles = new ArrayList<>(); // Initialize the roles list
        this.roles = roles; // Assign initial roles
    }

    /**
     * Retrieves the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Retrieves the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Retrieves the list of roles assigned to the user.
     *
     * @return A list of roles associated with the user.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Adds a new role to the user's list of roles.
     *
     * @param role The role to be added to the user.
     */
    public void addRole(String role) {
        this.roles.add(role); // Add the new role to the roles list
    }

    /**
     * Sets the roles for the user.
     *
     * @param roles A list of roles to be assigned to the user.
     */
    public void setRoles(List<String> roles) {
        this.roles = roles; // Set the user's roles
    }

    /**
     * Sets the email of the user and marks the account setup as complete.
     *
     * @param email The email address to be set for the user.
     */
    public void setEmail(String email) {
        this.email = email; // Set the user's email
        this.setupComplete = true;  // Mark the setup as complete
    }

    /**
     * Sets a new password for the user.
     *
     * @param password The new password to be set for the user.
     */
    public void setPassword(String password) {
        this.password = password; // Set the user's password
    }

    /**
     * Retrieves the user's password.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password; // Return the user's password
    }

    /**
     * Sets the first and last names of the user.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     */
    public void setNames(String firstName, String lastName) {
        this.firstName = firstName; // Set the first name
        this.lastName = lastName;   // Set the last name
    }

    /**
     * Checks if the account setup is complete.
     *
     * @return True if the setup is complete, false otherwise.
     */
    public boolean isSetupComplete() {
        return setupComplete; // Return the setup completion status
    }

    /**
     * Checks if the provided password matches the user's password.
     *
     * @param password The password to check against the user's password.
     * @return True if the passwords match, false otherwise.
     */
    public boolean checkPassword(String password) {
        return this.password.equals(password); // Check if the passwords match
    }
}