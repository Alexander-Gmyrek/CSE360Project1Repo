package ProjectUI;

//import the proper packages for the program
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javafx.scene.layout.ColumnConstraints;

/**
 * HelpSystemUI class that represents the user interface for the Help System application.
 * It handles user login, account creation, and role management using JavaFX.
 */
public class HelpSystemUI extends Application {

	private UserManager userManager = new UserManager();  // Initialize UserManager
    private User currentUser;  // Track the currently logged-in user
    private boolean first = true;  // Flag to indicate if the first user is being created
    private String usernameInvitation;  // Invitation username for account setup
    private String invitationCode;  // Invitation code for new user creation
    private String inviteRole;  // Role associated with the invitation
    private String oneTimePassword;  // One-time password for login
    private String oneTimePasswordUsername;  // Username for one-time password login
    private List<String> usedCodes = new ArrayList<>(); //Track used OTP codes
 
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Help System");
        // Initial login or account creation
        showLoginScreen(primaryStage);
    }

    /**
     * Displays the login screen for the user to enter their credentials.
     * User can enter a user name followed by a password (must be entered twice)
     * User can also use a one-time password or an invite code if invited by an admin
     * @param stage The primary stage of the application.
     */
    private void showLoginScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        //Make columns responsive to window adjustment
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30); // 30% width
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70); // 70% width
        grid.getColumnConstraints().addAll(column1, column2);

        //Username label and username field for user to input username
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 0);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 0);

        //Password label and password field for user to input password
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 1);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 1);

        //Confirmation password label and field for user to confirm their password input
        Label passwordLabel2 = new Label("Confirm Password:");
        grid.add(passwordLabel2, 0, 2);
        PasswordField passwordField2 = new PasswordField();
        grid.add(passwordField2, 1, 2);
        
        //Label indicating a field where a user can use an admin-created invitation code
        Label invitationCodeLabel = new Label("Enter Invitation Code");
        grid.add(invitationCodeLabel, 0, 3);
        TextArea invitationCodeArea = new TextArea();
        grid.add(invitationCodeArea, 1, 3);
        
        //Label indicating a field where a user can use an admin-generated one time password
        Label oneTimePasswordL = new Label("Enter Your One Time Password:");
        grid.add(oneTimePasswordL, 0, 4);
        TextArea oneTimePasswordText = new TextArea();
        grid.add(oneTimePasswordText, 1, 4);
        
        //Login button that starts the action event
        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 5);
        
        //Allow fields to grow with the window size
        GridPane.setHgrow(usernameField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField, Priority.ALWAYS);
        GridPane.setHgrow(passwordField2, Priority.ALWAYS);
        GridPane.setHgrow(loginButton, Priority.ALWAYS);
        
        //Event handling for login
        loginButton.setOnAction(e -> {
        	//get the entered username, password, and password confirmation
            String username = usernameField.getText();
            String password = passwordField.getText();
            String confirmPassword = passwordField2.getText();
            
            //Check if user entered an invitation code and see if it matches the generated invitation code
            if (invitationCodeArea.getText().equals(invitationCode)) {
            	//If there was a match, login as the user, add their role, and show an account 
            	//finalization screen for them to finish their account details
            	currentUser = userManager.login(usernameInvitation, password);
        		currentUser.addRole(inviteRole);
        		showInviteCreationScreen(stage);
            }
            
            //Check if user entered a one-time password alongside for a valid username
            if (username.equals(oneTimePasswordUsername) && oneTimePasswordText.getText().equals(oneTimePassword)){
            	//If there was a match, login as the user and bring them to a screen where they can reset their
            	//password to one of their choosing.
            	//Also, generate a boolean so that codes cannot be reused.
            	boolean flagLeave = false;
            	//Iterate through each code to ensure there are no matches
            	for (int i = 0; i < usedCodes.size(); i++) {
            		if (oneTimePassword.equals(usedCodes.get(i))) {
            			//Match found so code can't be used
            			flagLeave = true;
            		}
            	}
            	if (flagLeave != true) {
            		//New code so let user generate the new password
            		currentUser = userManager.login(oneTimePasswordUsername, oneTimePassword);
            		showPasswordResetScreen(stage);
            		usedCodes.add(oneTimePassword);
            	}else {
            		//Reused code so warn user
                    showAlert("OTP CODE REUSE", "Please generate a new OTP. OTPs cannot be reused!");
            	}
            }
            
            //First, ensure there password field contains input and the two fields match before allowing an
            //account to be created
            if (password.equals(confirmPassword) && !password.equals("")) {
            	
            	//check to see if its the first account being made and if so, it will be the admin account
            	if (first == true) {
            		//First user becomes Admin
            		List<String> adminRole = new ArrayList<>();
            		adminRole.add("Admin");
            		//Create the admin account, login as them, and set the first flag to false as
            		//after this user, the next account will not be the first
            		userManager.createAccount(username, password, adminRole);
            		currentUser = userManager.login(username, password);
            		first = false;
            		//Show screen where admin can setup their information
            		showAccountSetupScreen(stage);
            		
            	//Don't make an admin account but check to see if the entered information matches a valid
            	//account
            	} else if (userManager.login(username, password) == null) {
            		//Credentials did not match any current accounts so create a new account
            		List<String> roles = new ArrayList<>();
            		userManager.createAccount(username, password, roles);
            		currentUser = userManager.login(username, password);
            		//Check to see if user needs to add in additional account information still
            		if (currentUser.isSetupComplete()) {
            			showRoleSelectionScreen(stage);
            		
            		} else {
            			showAccountSetupScreen(stage);
            		}
            	
            	} else if (userManager.login(username,password) != null){
            		//Credentials did match so login as the specified user
            		currentUser = userManager.login(username, password);
            		List<String> userRole = currentUser.getRoles();	
            		
            		//Check the user roles
            		//If user has two roles, bring them to a screen where they can choose a role to use
            		if (userRole.contains("Student") && userRole.contains("Instructor")) {
            			showSessionRoleSelectionScreen(stage);
            		
            		//Otherwise, bring user to the page matching their account role
            		}else if (userRole.contains("Admin")) {
            			adminRoleHomePage(stage, "Admin");
            		
            		}else if (userRole.contains("Student")) {
            			studentRoleHomePage(stage, "Student");
            		
            		}else {
            			instructorRoleHomePage(stage, "Instructor");
            		}	
            	
            	//account information was incorrect so show user that the process failed and that they need to retry
            	}else {
            		showAlert("Login Failed", "Invalid username or password.");
            	}
            }
        });
    
        // Scene setup for login page
        Scene scene = new Scene(grid, 600, 600);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Displays the account setup screen for new users to enter their details.
     * These details will be the personal information of the user which includes
     * a first name field, last name field, middle name field, preferred name field, email field,
     * and a skill level field.
     * @param stage The primary stage of the application.
     */
    private void showAccountSetupScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25); // 25% width
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25); // 25% width
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25); // 25% width
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25); // 25% width
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);
        
        //Email Address label and fields for user to enter their email
        Label emailLabel = new Label("Email Address:");
        grid.add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 0);

        //Name Labels and Fields (First, Middle, Last, Preferred) for user to enter their personal name information
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

        // Skill Levels (Beginner, Intermediate, Advanced, Expert) that the user is able to pick from.
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

        // Submit Button once user has finished creating their account info
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 11);

        // Event handling for submit button to notify user
        submitButton.setOnAction(e -> {
            // Handle submit actions
            System.out.println("User info submitted");
        });
        //Submit button to collect and store user info and display role screen
        submitButton.setOnAction(e -> {
        	//gather email, name, and skill information from fields
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            //set the user's information accordingly
            currentUser.setEmail(email);
            currentUser.setNames(firstName, lastName);
            
            //show user the role selection screen (unless they are an admin where they are 
            //automatically assigned the role of admin
            if (!currentUser.getRoles().contains("Admin")) {
            	showRoleSelectionScreen(stage);
            }else {
            	adminRoleHomePage(stage, "Admin");
            }
        });

        Scene scene = new Scene(grid, 800, 450);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays the role selection screen for users to select their roles.
     * They have the choice of either a student, instructor, or being both a student
     * and an instructor. For now, both roles function the same as they can only logout.
     * @param stage The primary stage of the application.
     */
    private void showRoleSelectionScreen(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        //Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col1, col2);
        
        //Label indicating to user that they need to select a role with a combobox containing two options:
        //a Student and an Instructor
        Label roleLabel = new Label("Select Role (Use both boxes for multiple roles):");
        grid.add(roleLabel, 0, 0);
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox, 1, 0);
        
        //A second combobox in case the user has two roles.
        ComboBox<String> roleComboBox2 = new ComboBox<>();
        roleComboBox2.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox2, 1, 1);

        //select button to start action event
        Button selectButton = new Button("Select");
        grid.add(selectButton, 1, 2);

        //Event handling for role saving
        selectButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            //if there is a value in the second field, the user has two roles
            if (roleComboBox2.getValue() == "Student" || roleComboBox2.getValue() == "Instructor") {
            	//add two roles and bring user to a screen where they can choose a role to use
            	//for the current session
            	showSessionRoleSelectionScreen(stage);
            	currentUser.addRole("Student");
            	currentUser.addRole("Instructor");
            }
            //bring user to student home page since they are a student
            else if (selectedRole == "Student") {
            	studentRoleHomePage(stage, selectedRole);
            	//add student role accordingly
            	currentUser.addRole("Student");
            //bring user to instructor home page since they are an instructor
            }else {
            	instructorRoleHomePage(stage, selectedRole);
            	//add instructor role accordingly
            	currentUser.addRole("Instructor");
            }
        });

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays the student home screen after successful login and role selection of a student.
     * Only option currently is to logout of the application
     * @param stage The primary stage of the application.
     * @param role The role of the user for the current page
     */
    private void studentRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

        //Title of the page to let user know where they are at
        Label homePageLabel = new Label(role + " Home Page");
        grid.add(homePageLabel, 0, 0);
        
        //Allow user to logout with logout button
        Button logoutButton = new Button("Log Out");
        grid.add(logoutButton, 0, 1);

        //Action event handling that brings user back to login screen from the start
        logoutButton.setOnAction(e -> showLoginScreen(stage));

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Displays the student home screen after successful login and role selection of an instructor.
     * Only option currently is to logout of the application
     * @param stage The primary stage of the application.
     * @param role The role of the user for the current page
     */
    private void instructorRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

       //Title of the page to let user know where they are at
        Label homePageLabel = new Label(role + " Home Page");
        grid.add(homePageLabel, 0, 0);

        //Button that will allow user to sign out of application
        Button logoutButton = new Button("Log Out");
        grid.add(logoutButton, 0, 1);

        //Action event handling that brings user back to login screen from the start
        logoutButton.setOnAction(e -> showLoginScreen(stage));

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Displays the admin home screen after successful login and role selection of an admin.
     * They have the option of inviting new users, reseting user accounts, deleting user accounts,
     * listing user accounts, managing roles, and logging out.
     * @param stage The primary stage of the application.
     * @param role The role of the user for the current page
     */
    private void adminRoleHomePage(Stage stage, String role) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));  // Set padding for the grid
        grid.setVgap(10);  // Set vertical gap between rows
        grid.setHgap(10);  // Set horizontal gap between columns
        
        // Set up column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(100);
        grid.getColumnConstraints().add(col1);

        //Welcome the admin user
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
        deleteUserButton.setOnAction(e -> deleteUserAccount(stage));
        grid.add(deleteUserButton, 0, 3);

        // Button to list all user accounts
        Button listUsersButton = new Button("List User Accounts");
        listUsersButton.setOnAction(e -> listUserAccounts(stage));
        grid.add(listUsersButton, 0, 4);

        // Button to manage (add/remove) roles from a user
        Button manageRolesButton = new Button("Manage User Roles");
        manageRolesButton.setOnAction(e -> manageUserRoles(stage));
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
    
    /**
     * Allows the Admin to invite a new user by entering their username and selecting a role.
     *
     * @param stage The primary stage of the application.
     */
    private void inviteUser(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10)); // Set padding for the grid
        grid.setVgap(10); // Set vertical gap between rows
        grid.setHgap(10); // Set horizontal gap between columns

        //Allows admin to select the username for whome they want to invite
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        //Allows admin to pick the role of the user
        Label roleLabel = new Label("Select Role (Use both boxes for multiple roles):");
        grid.add(roleLabel, 0, 1);
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox, 1, 1);
        
        //Use second box if user has multiple roles
        ComboBox<String> roleComboBox2 = new ComboBox<>();
        roleComboBox2.getItems().addAll("Student", "Instructor");
        grid.add(roleComboBox2, 1, 2);

        //Button to send invite to the user.
        Button inviteButton = new Button("Send Invitation");
        grid.add(inviteButton, 3, 1);

        //Event action handling for the invite button
        inviteButton.setOnAction(e -> {
        	//Gather the information from the fields
            usernameInvitation = usernameField.getText();
            String selectedRole = roleComboBox.getValue();
            List<String> role = new ArrayList<>();
            //Add role according to what was chosen for the user
            if (selectedRole.equals("Student")) {
            	role.add("Student");
            	inviteRole = "Student";
            }else {
            	role.add("Instructor");
            	inviteRole = "Instructor";
            }
            //Generate a one-time code and send invitation by creating the account with the specified role
            invitationCode = UUID.randomUUID().toString();
            userManager.createAccount(usernameInvitation, "", role);
            //Alert admin that the invitation was successfully sent
            showAlert("Invitation Sent", "An invitation has been sent to " + usernameInvitation);
            System.out.println(invitationCode);
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 350);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Enables the Admin to reset a user's password and set a password expiration time.
     *
     * @param stage The primary stage of the application.
     */
    private void resetUserAccount(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10)); // Set padding for the grid
        grid.setVgap(10); // Set vertical gap between rows
        grid.setHgap(10); // Set horizontal gap between columns

        //Allows admin to specify the username of the account to reset password of
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        //Allows admin to set an expriation date in the expiration field
        Label expirationLabel = new Label("Expiration (yyyy-MM-dd HH:mm):");
        TextField expirationField = new TextField();
        grid.add(expirationLabel, 0, 1);
        grid.add(expirationField, 1, 1);

        //Button for reset password action
        Button resetButton = new Button("Reset Password");
        grid.add(resetButton, 1, 2);

        //Event handling for button press
        resetButton.setOnAction(e -> {
            String username = usernameField.getText();
            String expirationString = expirationField.getText();
            oneTimePasswordUsername = username;
            //set an expiration date for a randomly generated password
            LocalDateTime expirationDateTime = LocalDateTime.parse(expirationString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            oneTimePassword = userManager.resetUserPassword(username, expirationDateTime);
            //Show an alert specifying the user with the brand new password (and show the password as well)
            showAlert("Password Reset", "A one-time password has been set for " + username + ": " + oneTimePassword);
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Allows the Admin to delete a user account by specifying a certain username.
     * Contains a confirmation screen as well to ensure no accidental deletion.
     * @param stage The primary stage of the application.
     */
    private void deleteUserAccount(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10)); // Set padding for the grid
        grid.setVgap(10); // Set vertical gap between rows
        grid.setHgap(10); // Set horizontal gap between columns

        //Allows admin to specify the username of the user to delete
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        //Button to begin deletion process
        Button deleteButton = new Button("Delete User");
        grid.add(deleteButton, 1, 1);

        //Event handling for delete button
        deleteButton.setOnAction(e -> {
            String username = usernameField.getText();
            // Confirmation Dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user: " + username + "?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
            	//Allow user to confirm the deletion of the account
                if (response == ButtonType.YES) {
                    boolean deleteUser = userManager.deleteUser(username);
                    //show the user that the account has been fully deleted
                    if (deleteUser == true) {
                    showAlert("User Deleted", "User " + username + " has been deleted.");
                    }
                    adminRoleHomePage(stage, "Admin");
                }
            });
        });

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Lists all user accounts managed by the Admin.
     * Shows their role, name, and username
     * @param stage The primary stage of the application.
     */
    private void listUserAccounts(Stage stage) {
        //Collect every current user
    	List<User> users = userManager.getAllUsers(); 
        StringBuilder userList = new StringBuilder("User Accounts:\n");
        
        //Display the username, first and last name, and roles of each user.
        for (User user : users) {
            //userList.append(user.getUsername()).append(" - Roles: ").append(user.getRoles()).append("\n");
            userList.append("Username: ").append(user.getUsername()).append(" - Name: ").append(user.getFirstName()).append(" ")
            .append(user.getLastName()).append(" - Roles: ").append(user.getRoles()).append("\n");
        }

        TextArea textArea = new TextArea(userList.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        
        //Close out of the listing screen
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> adminRoleHomePage(stage, "Admin"));

        VBox vbox = new VBox(textArea, closeButton);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Allows the Admin to manage roles assigned to a user.
     * Admin can add/delete roles as necessary by typing in the roles they want for the user
     * @param stage The primary stage of the application.
     */
    private void manageUserRoles(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10)); // Set padding for the grid
        grid.setVgap(10); // Set vertical gap between rows
        grid.setHgap(10); // Set horizontal gap between columns
        
        //Allows admin to specify the username of the user they want to adjust the roles of
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);

        //A textfield where the roles can be entered
        Label rolesLabel = new Label("New Role(s) (comma-separated):");
        TextField rolesField = new TextField();
        grid.add(rolesLabel, 0, 1);
        grid.add(rolesField, 1, 1);

        //Submit button once all roles are in
        Button updateRolesButton = new Button("Update Roles");
        grid.add(updateRolesButton, 1, 2);

        //Event handling for submit button
        updateRolesButton.setOnAction(e -> {
        	//Collect all the role strings from the text field first
        	ArrayList<String> roles = new ArrayList<>();
            String username = usernameField.getText();
            String[] rolesT = rolesField.getText().split(",");
            //add the roles to an arraylist so they can be given to the user
            roles.addAll(Arrays.asList(rolesT));
            //update the user roles accordingly
            userManager.updateUserRoles(username, roles);
            //alert to user that the roles have been updated for the specified username
            showAlert("Roles Updated", "Roles for user " + username + " have been updated.");
            adminRoleHomePage(stage, "Admin");
        });

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();
    }

    
    /**
     * Displays the role selection screen for the application.
     * User can choose if they want to be a student or instructor for the given session.
     * @param stage The primary stage of the application.
     */
    private void showSessionRoleSelectionScreen(Stage stage) {
        // New layout for session role selection
        GridPane roleGrid = new GridPane();
        roleGrid.setPadding(new Insets(10)); // Set padding for the grid
        roleGrid.setVgap(10); // Set vertical gap between rows
        roleGrid.setHgap(10); // Set horizontal gap between columns
        
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
            	// Proceed with the selected Instructor session
            	instructorRoleHomePage(stage, "Instructor");
            }
        });

        // Set up the scene and show the stage
        Scene sessionRoleScene = new Scene(roleGrid, 400, 600);
        stage.setScene(sessionRoleScene);
        stage.show();
    }
    
    /**
     * Displays the screen for creating an invitation for a new user account.
     * This contains the username and the invite code for the user to enter
     * @param stage The primary stage of the application.
     */
    private void showInviteCreationScreen(Stage stage) {
    	 GridPane grid = new GridPane();
    	 grid.setPadding(new Insets(10)); // Set padding for the grid
         grid.setVgap(10); // Set vertical gap between rows
         grid.setHgap(10); // Set horizontal gap between columns
    	
        //Username field for user to enter in information
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 0);
        TextField userNameField = new TextField();
        grid.add(userNameField, 1, 0);

        //Allows user to create a password for their newly created account
        Label passwordLabel = new Label("New Password:");
        grid.add(passwordLabel, 0, 1);
        TextField passwordField = new TextField();
        grid.add(passwordField, 1, 1);
        
        //Allows user to confirm their password for their newly created account
        Label passwordLabel2 = new Label("Confirm Password:");
        grid.add(passwordLabel2, 0, 2);
        TextField passwordField2 = new TextField();
        grid.add(passwordField2, 1, 2);
        
        //Button to submit info once done
        Button submitButton = new Button("Submit");
        grid.add(submitButton, 1, 3);
        
        //Event handling for submit button
        submitButton.setOnAction(e -> {
        	//Ensure both password fields match
        	if (passwordField.getText().equals(passwordField2.getText())) {
        		//Update the password of the user
        		currentUser.setPassword(passwordField2.getText());
        		//Add the role of the user as well
        		currentUser.addRole(inviteRole);
        			//Show the proper screen based on the user role
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

        Scene scene = new Scene(grid, 400, 600);
        stage.setScene(scene);
        stage.show();   
                 
    }


/**
 * Displays the screen for resetting a user's password.
 * The admin enters a username and is given a one-time password
 * @param stage The primary stage of the application.
 */
    private void showPasswordResetScreen(Stage stage) {
    	GridPane grid = new GridPane();
        grid.setPadding(new Insets(10)); // Set padding for the grid
        grid.setVgap(10); // Set vertical gap between rows
        grid.setHgap(10); // Set horizontal gap between columns
   	
       //Allows user to enter in a brand new password
       Label passwordLabel = new Label("New Password:");
       grid.add(passwordLabel, 0, 1);
       TextField passwordField = new TextField();
       grid.add(passwordField, 1, 1);
       
       Button submitButton = new Button("Submit");
       grid.add(submitButton, 1, 2);
       
       //Event handling for submit button
       submitButton.setOnAction(e -> {
    	   //Finds the user with the accoridng username and updates their password
    	   User user = userManager.findUser(oneTimePasswordUsername);
           user.setPassword(passwordField.getText());
           showLoginScreen(stage);
       });

       Scene scene = new Scene(grid, 400, 600);
       stage.setScene(scene);
       stage.show();   
       
    }
    
    /**
     * Displays an alert dialog with a specified title and message.
     *
     * @param title The title of the alert dialog.
     * @param message The message to display in the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * The main entry point for launching the application.
     *
     * @param args Command-line arguments for the application (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
