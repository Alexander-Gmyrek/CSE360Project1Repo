package ProjectUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.scene.layout.ColumnConstraints;

public class HelpSystemUI extends Application {

    private UserManager userManager = new UserManager();  // Initialize UserManager
    private User currentUser;  // Track the currently logged-in user
    private boolean first = true;
    private String usernameInvitation;
    private String invitationCode;
    private String inviteRole;
    private String oneTimePassword;
    private String oneTimePasswordUsername;
 
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Help System");

        // Initial login or account creation
        showLoginScreen(primaryStage);
    }

    // Display the login screen
    private void showLoginScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
     // Make columns responsive
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30); // 30% width
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70); // 70% width
        grid.getColumnConstraints().addAll(column1, column2);

        // Username and password fields
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 0);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        Label passwordLabel2 = new Label("Confirm Password:");
        grid.add(passwordLabel2, 0, 2);
        PasswordField passwordField2 = new PasswordField();
        grid.add(passwordField2, 1, 2);
        
        Label invitationCodeLabel = new Label("Enter Invitation Code");
        grid.add(invitationCodeLabel, 0, 3);
        TextArea invitationCodeArea = new TextArea();
        grid.add(invitationCodeArea, 1, 3);
        
        Label oneTimePasswordL = new Label("Enter Your One Time Password:");
        grid.add(oneTimePasswordL, 0, 4);
        TextArea oneTimePasswordText = new TextArea();
        grid.add(oneTimePasswordText, 1, 4);
        
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 5);
        
        

        // Allow fields to grow with the window size
        GridPane.setHgrow(usernameField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField2, Priority.ALWAYS);
        GridPane.setHgrow(loginButton, Priority.ALWAYS);
        
        // Event handling for login
        
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = passwordField2.getText();
            
            if (invitationCodeArea.getText().equals(invitationCode)) {
            	currentUser = userManager.login(usernameInvitation, password);
        		currentUser.addRole(inviteRole);
        		showInviteCreationScreen(stage);
            }
            
            if (username.equals(oneTimePasswordUsername) && oneTimePasswordText.getText().equals(oneTimePassword)){
            	currentUser = userManager.login(oneTimePasswordUsername, oneTimePassword);
            	showPasswordResetScreen(stage);
            }
            
            if (password.equals(confirmPassword) && !password.equals("")) {
            if (first == true) {
                // First user becomes Admin
            	List<String> adminRole = new ArrayList<>();
            	adminRole.add("Admin");
                userManager.createAccount(username, password, adminRole);
                currentUser = userManager.login(username, password);
                first = false;
                showAccountSetupScreen(stage);
            } else if (userManager.login(username, password) == null) {
            	List<String> roles = new ArrayList<>();
                userManager.createAccount(username, password, roles);
                currentUser = userManager.login(username, password);
                if (currentUser.isSetupComplete()) {
                    showRoleSelectionScreen(stage);
                } else {
                    showAccountSetupScreen(stage);
                }
            } else if (userManager.login(username,password) != null){
            		currentUser = userManager.login(username, password);
            		List<String> userRole = currentUser.getRoles();	
            		if (userRole.contains("Student") && userRole.contains("Instructor")) {
            			showSessionRoleSelectionScreen(stage);
            		}else if (userRole.contains("Admin")) {
            			adminRoleHomePage(stage, "Admin");
            		}else if (userRole.contains("Student")) {
            			studentRoleHomePage(stage, "Student");
            		}else {
            			instructorRoleHomePage(stage, "Instructor");
            		}	
            }else {
            	 showAlert("Login Failed", "Invalid username or password.");
            }
            }
        });
    
        // Scene setup
        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    // Display the account setup screen
    private void showAccountSetupScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25); // 25% width
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25); // 25% width
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25); // 25% width
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25); // 25% width
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);
        
        // Email Address
        Label emailLabel = new Label("Email Address:");
        grid.add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 0);

        // Name Fields (First, Middle, Last, Preferred)
        Label nameLabel = new Label("Name (First, Middle, Last, Preferred):");
        grid.add(nameLabel, 0, 1);
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("Preferred Name");

        // Horizontal box for name fields
        grid.add(firstNameField, 1, 1);
        grid.add(middleNameField, 2, 1);
        grid.add(lastNameField, 3, 1);
        grid.add(preferredNameField, 1, 2);

        // Skill Levels (Beginner, Intermediate, Advanced, Expert)
        Label skillsLabel = new Label("Skill Levels (Select level for each topic):");
        grid.add(skillsLabel, 0, 6);
        String[] topics = {"Java", "Eclipse", "JavaFX", "GitHub"};
        for (int i = 0; i < topics.length; i++) {
            Label topicLabel = new Label(topics[i]);
            grid.add(topicLabel, 0, 7 + i);
            ComboBox<String> skillLevelCombo = new ComboBox<>();
            skillLevelCombo.getItems().addAll("Beginner", "Intermediate", "Advanced", "Expert");
            skillLevelCombo.setValue("Intermediate"); // Default
            grid.add(skillLevelCombo, 1, 7 + i);
        }

        // Submit Button
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 11);

        // Event handling for submit
        submitButton.setOnAction(e -> {
            // Handle submit actions
            System.out.println("User info submitted");
        });
        // Submit button
        submitButton.setOnAction(e -> {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            currentUser.setEmail(email);
            currentUser.setNames(firstName, lastName);
            if (!currentUser.getRoles().contains("Admin")) {
            	showRoleSelectionScreen(stage);
            }else {
            	adminRoleHomePage(stage, "Admin");
            }
        });

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    // Display role selection screen
    private void showRoleSelectionScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);

        Label roleLabel = new Label("Select Role (Use both boxes for multiple roles):");
        grid.add(roleLabel, 0, 0);
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox, 1, 0);
        
        ComboBox<String> roleComboBox2 = new ComboBox<>();
        roleComboBox2.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox2, 1, 1);

        Button selectButton = new Button("Select");
        grid.add(selectButton, 1, 2);

        selectButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (roleComboBox2.getValue() == "Student" || roleComboBox2.getValue() == "Instructor") {
            	showSessionRoleSelectionScreen(stage);
            }
            else if (selectedRole == "Student") {
            	studentRoleHomePage(stage, selectedRole);
            }else {
            	instructorRoleHomePage(stage, selectedRole);
            }
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    // Display home page for the student
    private void studentRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

        Label homePageLabel = new Label(role + " Home Page");
        grid.add(homePageLabel, 0, 0);

        Button logoutButton = new Button("Log Out");
        grid.add(logoutButton, 0, 1);

        logoutButton.setOnAction(e -> showLoginScreen(stage));

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
    
    private void instructorRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

        Label homePageLabel = new Label(role + " Home Page");
        grid.add(homePageLabel, 0, 0);

        Button logoutButton = new Button("Log Out");
        grid.add(logoutButton, 0, 1);

        logoutButton.setOnAction(e -> showLoginScreen(stage));

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
    
    private void adminRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

        Label welcomeLabel = new Label("Welcome, " + role);
        grid.add(welcomeLabel, 0, 0);

        // Button to invite a new user
        Button inviteUserButton = new Button("Invite User");
        inviteUserButton.setOnAction(e -> inviteUser(stage));
        grid.add(inviteUserButton, 0, 1);

        // Button to reset a user account
        Button resetUserButton = new Button("Reset User Account");
         resetUserButton.setOnAction(e -> resetUserAccount(stage));
        grid.add(resetUserButton, 0, 2);

        // Button to delete a user account
        Button deleteUserButton = new Button("Delete User Account");
   //     deleteUserButton.setOnAction(e -> deleteUserAccount(stage));
        grid.add(deleteUserButton, 0, 3);

        // Button to list all user accounts
        Button listUsersButton = new Button("List User Accounts");
   //     listUsersButton.setOnAction(e -> listUserAccounts(stage));
        grid.add(listUsersButton, 0, 4);

        // Button to manage (add/remove) roles from a user
        Button manageRolesButton = new Button("Manage User Roles");
    //    manageRolesButton.setOnAction(e -> manageUserRoles(stage));
        grid.add(manageRolesButton, 0, 5);

        // Button to log out
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen(stage));
        grid.add(logoutButton, 0, 6);

        // Adjust the layout
        Scene scene = new Scene(grid, 400, 400);
        stage.setScene(scene);
        stage.show();
    }
    
 // Invite User Method
    private void inviteUser(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 0, 1);

        Label roleLabel = new Label("Select Role (Use both boxes for multiple roles):");
        grid.add(roleLabel, 1, 0);
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox, 0, 2);
        
        ComboBox<String> roleComboBox2 = new ComboBox<>();
        roleComboBox2.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox2, 0, 3);

        Button inviteButton = new Button("Send Invitation");
        grid.add(inviteButton, 1, 4);

        inviteButton.setOnAction(e -> {
            usernameInvitation = usernameField.getText();
            String selectedRole = roleComboBox.getValue();
            List<String> role = new ArrayList<>();
            if (selectedRole.equals("Student")) {
            	role.add("Student");
            	inviteRole = "Student";
            }else {
            	role.add("Instructor");
            	inviteRole = "Instructor";
            }
            // Generate a one-time code and send invitation (implement the logic here)
            invitationCode = UUID.randomUUID().toString();
            userManager.createAccount(usernameInvitation, "", role);
            showAlert("Invitation Sent", "An invitation has been sent to " + usernameInvitation);
            System.out.println(invitationCode);
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

     //Reset User Account Method
    private void resetUserAccount(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label expirationLabel = new Label("Expiration (yyyy-MM-dd HH:mm):");
        TextField expirationField = new TextField();
        grid.add(expirationLabel, 0, 1);
        grid.add(expirationField, 1, 1);

        Button resetButton = new Button("Reset Password");
        grid.add(resetButton, 1, 2);

        resetButton.setOnAction(e -> {
            String username = usernameField.getText();
            String expirationString = expirationField.getText();
            oneTimePasswordUsername = username;
            LocalDateTime expirationDateTime = LocalDateTime.parse(expirationString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            oneTimePassword = userManager.resetUserPassword(username, expirationDateTime);
            showAlert("Password Reset", "A one-time password has been set for " + username + ": " + oneTimePassword);
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
/*
    // Delete User Account Method
    private void deleteUserAccount(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Button deleteButton = new Button("Delete User");
        grid.add(deleteButton, 1, 1);

        deleteButton.setOnAction(e -> {
            String username = usernameField.getText();
            // Confirmation Dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user: " + username + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    userManager.deleteUser(username);
                    showAlert("User Deleted", "User " + username + " has been deleted.");
                    adminRoleHomePage(stage, "Admin");
                }
            });
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    // List User Accounts Method
    private void listUserAccounts(Stage stage) {
        List<User> users = userManager.getAllUsers(); // Assuming this method exists in UserManager
        StringBuilder userList = new StringBuilder("User Accounts:\n");

        for (User user : users) {
            userList.append(user.getUsername()).append(" - Roles: ").append(user.getRoles()).append("\n");
        }

        TextArea textArea = new TextArea(userList.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> adminRoleHomePage(stage, "Admin"));

        VBox vbox = new VBox(textArea, closeButton);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    // Manage User Roles Method
    private void manageUserRoles(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        Label rolesLabel = new Label("New Role(s) (comma-separated):");
        TextField rolesField = new TextField();
        grid.add(rolesLabel, 0, 1);
        grid.add(rolesField, 1, 1);

        Button updateRolesButton = new Button("Update Roles");
        grid.add(updateRolesButton, 1, 2);

        updateRolesButton.setOnAction(e -> {
            String username = usernameField.getText();
            String[] roles = rolesField.getText().split(",");
            userManager.updateUserRoles(username, roles);
            showAlert("Roles Updated", "Roles for user " + username + " have been updated.");
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    */
 // Method to show session role selection screen
    private void showSessionRoleSelectionScreen(Stage stage) {
        // New layout for session role selection
        GridPane roleGrid = new GridPane();
        roleGrid.setPadding(new Insets(10, 10, 10, 10));
        roleGrid.setVgap(10);
        roleGrid.setHgap(10);
        
     // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        roleGrid.getColumnConstraints().addAll(col1, col2);

        Label selectRoleLabel = new Label("Choose Role for Current Session:");
        roleGrid.add(selectRoleLabel, 0, 0);

        // ComboBox for selecting which role to use for the session
        ComboBox<String> sessionRoleComboBox = new ComboBox<>();
        sessionRoleComboBox.getItems().addAll("Student", "Instructor");  // Since the user has both roles
        roleGrid.add(sessionRoleComboBox, 1, 0);

        Button confirmRoleButton = new Button("Confirm Role");
        roleGrid.add(confirmRoleButton, 1, 1);

        // Event handling for role confirmation
        confirmRoleButton.setOnAction(e -> {
            String selectedSessionRole = sessionRoleComboBox.getValue();
            if (selectedSessionRole == "Student") {
                // Proceed with the selected session role
                studentRoleHomePage(stage, "Student");
            } else {
            	instructorRoleHomePage(stage, "Instructor");
            }
        });

        // Set up the scene and show the stage
        Scene sessionRoleScene = new Scene(roleGrid, 400, 200);
        stage.setScene(sessionRoleScene);
        stage.show();
    }
    
    private void showInviteCreationScreen(Stage stage) {
    	 GridPane grid = new GridPane();
         grid.setPadding(new Insets(10));
         grid.setVgap(10);
         grid.setHgap(10);
    	// Email Address
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 0);
        TextField userNameField = new TextField();
        grid.add(userNameField, 1, 0);

        // Username
        Label passwordLabel = new Label("New Password:");
        grid.add(passwordLabel, 0, 1);
        TextField passwordField = new TextField();
        grid.add(passwordField, 1, 1);
        
        Label passwordLabel2 = new Label("Confirm Password:");
        grid.add(passwordLabel2, 0, 2);
        TextField passwordField2 = new TextField();
        grid.add(passwordField2, 1, 2);
        
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 3);
        
        submitButton.setOnAction(e -> {
        	if (passwordField.getText().equals(passwordField2.getText())) {
        	currentUser.setPassword(passwordField2.getText());
        	currentUser.addRole(inviteRole);
        	if (currentUser.getRoles().equals("Student") && currentUser.getRoles().equals("Instructor")) {
        		showSessionRoleSelectionScreen(stage);
        	}
        	if (currentUser.getRoles().equals("Student")) {
        		studentRoleHomePage(stage, "Student");
        	}else {
        		instructorRoleHomePage(stage, "Instructor");
        	}
        	}
        });

        Scene scene = new Scene(grid, 400, 200);
        stage.setScene(scene);
        stage.show();   
                 
    }

    private void showPasswordResetScreen(Stage stage) {
    	GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
   	
       Label passwordLabel = new Label("New Password:");
       grid.add(passwordLabel, 0, 1);
       TextField passwordField = new TextField();
       grid.add(passwordField, 1, 1);
       

       Button submitButton = new Button("Submit");
       grid.add(submitButton, 1, 2);
       
       submitButton.setOnAction(e -> {
    	   User user = userManager.findUser(oneTimePasswordUsername);
           user.setPassword(passwordField.getText());
           showLoginScreen(stage);
       });

       Scene scene = new Scene(grid, 400, 200);
       stage.setScene(scene);
       stage.show();   
       
    }
    
    // Display alert dialog
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}