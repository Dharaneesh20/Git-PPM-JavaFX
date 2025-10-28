package com.ppm.gitppm.controller;

import com.ppm.gitppm.model.GitAuthType;
import com.ppm.gitppm.model.GitCredentials;
import com.ppm.gitppm.service.GitService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class LoginController {
    @FXML private ComboBox<GitAuthType> authTypeComboBox;
    @FXML private TextField repoPathField;
    @FXML private Button browseFolderButton;
    
    // Username/Password section
    @FXML private VBox usernamePasswordSection;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    
    // PAT section
    @FXML private VBox patSection;
    @FXML private TextField usernameFieldPAT;
    @FXML private PasswordField patField;
    
    // GitHub OAuth section
    @FXML private VBox githubOAuthSection;
    @FXML private Button githubSignInButton;
    @FXML private Label githubStatusLabel;
    
    @FXML private Button openRepoButton;
    @FXML private Button cloneRepoButton;
    @FXML private Label statusLabel;

    private GitService gitService;

    @FXML
    public void initialize() {
        gitService = new GitService();
        
        // Setup auth type combo box
        authTypeComboBox.getItems().addAll(GitAuthType.values());
        authTypeComboBox.setValue(GitAuthType.USERNAME_PASSWORD);
        
        // Listen for auth type changes
        authTypeComboBox.setOnAction(e -> updateAuthSections());
        
        // Show initial auth section
        updateAuthSections();
    }

    /**
     * Update visible authentication sections based on selected auth type
     */
    private void updateAuthSections() {
        GitAuthType selectedType = authTypeComboBox.getValue();
        
        // Hide all sections
        usernamePasswordSection.setVisible(false);
        usernamePasswordSection.setManaged(false);
        patSection.setVisible(false);
        patSection.setManaged(false);
        githubOAuthSection.setVisible(false);
        githubOAuthSection.setManaged(false);
        
        // Show selected section
        if (selectedType == GitAuthType.USERNAME_PASSWORD) {
            usernamePasswordSection.setVisible(true);
            usernamePasswordSection.setManaged(true);
        } else if (selectedType == GitAuthType.PERSONAL_ACCESS_TOKEN) {
            patSection.setVisible(true);
            patSection.setManaged(true);
        } else if (selectedType == GitAuthType.GITHUB_OAUTH) {
            githubOAuthSection.setVisible(true);
            githubOAuthSection.setManaged(true);
        }
    }

    @FXML
    private void onBrowseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Repository Folder");
        
        File selectedDirectory = directoryChooser.showDialog(browseFolderButton.getScene().getWindow());
        if (selectedDirectory != null) {
            repoPathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void onOpenRepository() {
        String repoPath = repoPathField.getText();
        
        if (repoPath == null || repoPath.trim().isEmpty()) {
            showError("Please select a repository folder");
            return;
        }

        GitCredentials credentials = collectCredentials();
        if (credentials == null) {
            return;
        }

        // Validate repository exists
        File repoDir = new File(repoPath);
        File gitDir = new File(repoDir, ".git");
        
        if (!gitDir.exists() || !gitDir.isDirectory()) {
            showError("The selected folder is not a Git repository.\nPlease select a valid Git repository or use Clone Repository.");
            return;
        }

        credentials.setRepositoryPath(repoPath);

        try {
            gitService.openRepository(repoPath, credentials);
            showSuccess("Repository opened successfully!");
            
            // Open main window
            openMainWindow(credentials);
        } catch (Exception e) {
            showError("Failed to open repository: " + e.getMessage());
        }
    }

    @FXML
    private void onCloneRepository() {
        // Create a dialog to get remote URL
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Clone Repository");
        dialog.setHeaderText("Enter Remote Repository URL");
        dialog.setContentText("URL:");
        
        dialog.showAndWait().ifPresent(remoteUrl -> {
            String localPath = repoPathField.getText();
            
            if (localPath == null || localPath.trim().isEmpty()) {
                showError("Please select a destination folder");
                return;
            }

            GitCredentials credentials = collectCredentials();
            if (credentials == null) {
                return;
            }

            credentials.setRemoteUrl(remoteUrl);
            credentials.setRepositoryPath(localPath);

            // Show progress
            statusLabel.setText("Cloning repository...");
            statusLabel.setStyle("-fx-text-fill: #2196F3;");

            // Clone in background thread
            new Thread(() -> {
                try {
                    gitService.cloneRepository(remoteUrl, localPath, credentials);
                    
                    Platform.runLater(() -> {
                        showSuccess("Repository cloned successfully!");
                        openMainWindow(credentials);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> 
                        showError("Failed to clone repository: " + e.getMessage())
                    );
                }
            }).start();
        });
    }

    @FXML
    private void onGitHubSignIn() {
        // Note: Full OAuth implementation requires a web server to handle callback
        // For now, we'll show instructions to use PAT instead
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GitHub Sign In");
        alert.setHeaderText("GitHub OAuth Authentication");
        alert.setContentText("For this version, please use Personal Access Token authentication.\n\n" +
                "1. Go to GitHub Settings > Developer settings > Personal access tokens\n" +
                "2. Generate a new token with 'repo' scope\n" +
                "3. Select 'Personal Access Token' from the authentication method\n" +
                "4. Enter your username and token");
        alert.showAndWait();
    }

    @FXML
    private void onHelpPAT() {
        try {
            Desktop.getDesktop().browse(new URI("https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token"));
        } catch (Exception e) {
            showError("Could not open browser. Please visit:\nhttps://docs.github.com/en/authentication");
        }
    }

    /**
     * Collect credentials from the form
     */
    private GitCredentials collectCredentials() {
        GitCredentials credentials = new GitCredentials();
        credentials.setAuthType(authTypeComboBox.getValue());

        if (credentials.getAuthType() == GitAuthType.USERNAME_PASSWORD) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            
            if (username == null || username.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                showError("Please enter username and password");
                return null;
            }
            
            credentials.setUsername(username);
            credentials.setPassword(password);
            
        } else if (credentials.getAuthType() == GitAuthType.PERSONAL_ACCESS_TOKEN) {
            String username = usernameFieldPAT.getText();
            String token = patField.getText();
            
            if (username == null || username.trim().isEmpty() || 
                token == null || token.trim().isEmpty()) {
                showError("Please enter username and personal access token");
                return null;
            }
            
            credentials.setUsername(username);
            credentials.setPersonalAccessToken(token);
        }

        return credentials;
    }

    /**
     * Open the main application window
     */
    private void openMainWindow(GitCredentials credentials) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ppm/gitppm/main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);
            
            // Add CSS
            scene.getStylesheets().add(getClass().getResource("/com/ppm/gitppm/styles.css").toExternalForm());
            
            MainController controller = loader.getController();
            controller.initializeWithGitService(gitService, credentials);
            
            Stage stage = new Stage();
            stage.setTitle("Git PPM - " + credentials.getRepositoryPath());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
            // Close login window
            Stage loginStage = (Stage) openRepoButton.getScene().getWindow();
            loginStage.close();
            
        } catch (IOException e) {
            showError("Failed to open main window: " + e.getMessage());
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #F44336;");
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setStyle("-fx-text-fill: #4CAF50;");
    }
}
