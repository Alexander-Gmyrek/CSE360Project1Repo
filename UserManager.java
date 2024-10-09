package ProjectUI;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages user accounts and provides functionality for user authentication, creation, and management.
 */
public class UserManager {
    private List<User> users = new ArrayList<>(); // List to store user accounts

    /**
     * Checks if there is at least one user account in the system.
     *
     * @return True if no users exist, false otherwise.
     */
    public boolean isFirstUser() {
        return users.isEmpty(); // Return true if the users list is empty
    }

    /**
     * Creates a new user account with the specified username, password, and roles.
     *
     * @param username The username for the new account.
     * @param password The password for the new account.
     * @param roles    A list of roles assigned to the new user.
     */
    public void createAccount(String username, String password, List<String> roles) {
        users.add(new User(username, password, roles)); // Add the new user to the list
    }

    /**
     * Logs in a user with the specified username and password.
     *
     * @param username The username of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return The logged-in user if authentication is successful; null otherwise.
     */
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                return user; // Return the authenticated user
            }
        }
        return null; // Return null if authentication fails
    }
    
    /**
     * Finds a user by their username.
     *
     * @param username The username of the user to find.
     * @return The user if found; null otherwise.
     */
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user; // Return the found user
            }
        }
        return null; // Return null if user is not found
    }

    /**
     * Lists all users in the system.
     *
     * @return A list of all users.
     */
    public List<User> listUsers() {
        return users; // Return the list of users
    }

    /**
     * Generates a random one-time password (OTP) for user verification.
     *
     * @return A randomly generated 8-character long OTP.
     */
    private String generateOneTimePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Characters for OTP
        SecureRandom random = new SecureRandom(); // Secure random number generator
        StringBuilder otp = new StringBuilder(8); // 8-character long password
        
        for (int i = 0; i < 8; i++) {
            otp.append(chars.charAt(random.nextInt(chars.length()))); // Append random character
        }
        return otp.toString(); // Return the generated OTP
    }

    /**
     * Resets a user's password using a one-time password and expiration time.
     *
     * @param username         The username of the user whose password is being reset.
     * @param expirationDateTime The expiration date and time for the OTP.
     * @return A one-time password for the user or an error message if the expiration is in the past.
     */
    public String resetUserPassword(String username, LocalDateTime expirationDateTime) {
        LocalDateTime currentTime = LocalDateTime.now(); // Get the current time
        // Check if the expiration date and time is valid (not in the past)
        if (expirationDateTime.isBefore(currentTime)) {
            return "Expiration date and time cannot be in the past."; // Return error message
        }

        // Generate a one-time password (OTP)
        String oneTimePassword = generateOneTimePassword();
        return oneTimePassword; // Return the OTP for further use
    }

    /**
     * Deletes a user account by username.
     *
     * @param username The username of the user to be deleted.
     * @return True if the user was successfully deleted; false otherwise.
     */
    public boolean deleteUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                users.remove(user); // Remove the user from the list
                return true; // Return success
            }
        }
        return false; // Return failure if user is not found
    }

    /**
     * Retrieves all users in the system.
     *
     * @return A list of all user accounts.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users); // Return a new list containing all users
    }

    /**
     * Updates the roles of a specified user.
     *
     * @param username The username of the user whose roles are to be updated.
     * @param roles    A list of new roles to assign to the user.
     */
    public void updateUserRoles(String username, List<String> roles) {
        User user = findUser(username); // Find the user
        user.setRoles(roles); // Set the new roles for the user
    }
}