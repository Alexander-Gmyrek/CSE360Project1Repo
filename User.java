package ProjectUI;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String password;
    private List<String> roles;
    private String email;
    private String firstName;
    private String lastName;
    private boolean setupComplete = false;

    // Constructor for User with username, password, and initial role
    public User(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>();
        this.roles = roles;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Getter for firstName
    public String getFirstName() {
        return firstName;
    }

    // Getter for lastName
    public String getLastName() {
        return lastName;
    }

    // Getter for roles
    public List<String> getRoles() {
        return roles;
    }

    // Add a new role to the user
    public void addRole(String role) {
        this.roles.add(role);
    }

    // Set email and mark account setup as complete
    public void setEmail(String email) {
        this.email = email;
        this.setupComplete = true;  // Mark as setup completed
    }
    
    public void setPassword(String password) {
    	this.password = password;
    }
    
    public String getPassword() {
    	return password;
    }

    // Set first and last names
    public void setNames(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Check if setup is complete
    public boolean isSetupComplete() {
        return setupComplete;
    }

    // Check if the provided password matches the user's password
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}