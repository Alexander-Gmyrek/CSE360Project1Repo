package ProjectUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HelpSystemUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Help System User Information");

        // GridPane for layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        System.out.println("hi");

        // Email Address
        Label emailLabel = new Label("Email Address:");
        grid.add(emailLabel, 0, 0);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 0);

        // Username
        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);
        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        // Password
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        // One-time Password Checkbox
        CheckBox otpCheckBox = new CheckBox("One-time Password");
        grid.add(otpCheckBox, 1, 3);

        // Password Expiry Date
        Label expiryDateLabel = new Label("Expiry Date:");
        grid.add(expiryDateLabel, 0, 4);
        DatePicker expiryDatePicker = new DatePicker();
        grid.add(expiryDatePicker, 1, 4);

        // Name Fields (First, Middle, Last, Preferred)
        Label nameLabel = new Label("Name (First, Middle, Last, Preferred):");
        grid.add(nameLabel, 0, 5);
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Middle Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        TextField preferredNameField = new TextField();
        preferredNameField.setPromptText("Preferred Name");

        // Horizontal box for name fields
        grid.add(firstNameField, 1, 5);
        grid.add(middleNameField, 2, 5);
        grid.add(lastNameField, 3, 5);
        grid.add(preferredNameField, 4, 5);

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

        // Scene setup
        Scene scene = new Scene(grid, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
