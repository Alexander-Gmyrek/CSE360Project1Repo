package ProjectUI;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<User> users = new ArrayList<>();
    
    public boolean isFirstUser() {
        return users.isEmpty();
    }

    public void createAccount(String username, String password, List<String> roles) {
        users.add(new User(username, password, roles));
    }

    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                return user;
            }
        }
        return null;
    }
    
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
            	return user;
            }
        }
        return null;
    }

    public List<User> listUsers() {
        return users;
    }

    // Method to generate a random one-time password (OTP)
    private String generateOneTimePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(8); // 8-character long password
        
        for (int i = 0; i < 8; i++) {
            otp.append(chars.charAt(random.nextInt(chars.length())));
        }
        return otp.toString();
    }

    // Method to reset a user's password with a one-time password and expiration time
    public String resetUserPassword(String username, LocalDateTime expirationDateTime) {
        // Check if the expiration date and time is valid (not in the past)
        LocalDateTime currentTime = LocalDateTime.now();
        if (expirationDateTime.isBefore(currentTime)) {
            return "Expiration date and time cannot be in the past.";
        }

        // Generate a one-time password (OTP)
        String oneTimePassword = generateOneTimePassword();
        return oneTimePassword; // Return the OTP for further use
    }
}
