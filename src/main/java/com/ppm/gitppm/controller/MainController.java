package com.ppm.gitppm.controller;

import com.ppm.gitppm.model.CommitInfo;
import com.ppm.gitppm.model.FileChange;
import com.ppm.gitppm.model.GitCredentials;
import com.ppm.gitppm.service.FileWatcherService;
import com.ppm.gitppm.service.GitService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {
    // Top toolbar
    @FXML private Label repoNameLabel;
    @FXML private ComboBox<String> branchComboBox;
    @FXML private Label statusMessageLabel;
    @FXML private Label fileWatcherStatusLabel;
    
    // Left panel - Changes
    @FXML private Label changesCountLabel;
    @FXML private ListView<FileChange> changesListView;
    @FXML private TextArea commitMessageArea;
    
    // Right panel - History & Info
    @FXML private Label commitsCountLabel;
    @FXML private ListView<CommitInfo> commitsListView;
    @FXML private Label repoPathInfoLabel;
    @FXML private Label currentBranchInfoLabel;
    @FXML private Label remoteUrlInfoLabel;
    @FXML private ListView<String> branchesListView;
    @FXML private TextArea consoleArea;

    private GitService gitService;
    private GitCredentials credentials;
    private FileWatcherService fileWatcherService;
    private ObservableList<FileChange> fileChanges;
    private ObservableList<CommitInfo> commits;

    @FXML
    public void initialize() {
        this.fileChanges = FXCollections.observableArrayList();
        this.commits = FXCollections.observableArrayList();
        setupUI();
    }

    public void initializeData(GitService gitService, GitCredentials credentials) {
        this.gitService = gitService;
        this.credentials = credentials;
        
        loadRepositoryData();
        startFileWatcher();
    }

    /**
     * Setup UI components
     */
    private void setupUI() {
        // Setup changes list view with custom cell factory
        changesListView.setItems(fileChanges);
        changesListView.setCellFactory(param -> new ListCell<FileChange>() {
            @Override
            protected void updateItem(FileChange item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Create checkbox for staging
                    CheckBox checkBox = new CheckBox();
                    checkBox.setSelected(item.isStaged());
                    checkBox.setOnAction(e -> {
                        try {
                            if (checkBox.isSelected()) {
                                gitService.stageFiles(List.of(item.getFilePath()));
                                item.setStaged(true);
                                logToConsole("Staged: " + item.getFilePath());
                            } else {
                                gitService.unstageFiles(List.of(item.getFilePath()));
                                item.setStaged(false);
                                logToConsole("Unstaged: " + item.getFilePath());
                            }
                        } catch (Exception ex) {
                            showError("Failed to stage/unstage file: " + ex.getMessage());
                        }
                    });
                    
                    // Create icon based on change type
                    FontIcon icon = new FontIcon();
                    switch (item.getChangeType()) {
                        case ADDED:
                            icon.setIconLiteral("fa-plus-circle");
                            icon.setIconColor(Color.web("#4CAF50"));
                            break;
                        case MODIFIED:
                            icon.setIconLiteral("fa-edit");
                            icon.setIconColor(Color.web("#FF9800"));
                            break;
                        case DELETED:
                            icon.setIconLiteral("fa-minus-circle");
                            icon.setIconColor(Color.web("#F44336"));
                            break;
                        case UNTRACKED:
                            icon.setIconLiteral("fa-question-circle");
                            icon.setIconColor(Color.web("#2196F3"));
                            break;
                        case CONFLICTING:
                            icon.setIconLiteral("fa-exclamation-triangle");
                            icon.setIconColor(Color.web("#9C27B0"));
                            break;
                        default:
                            icon.setIconLiteral("fa-file");
                            icon.setIconColor(Color.web("#757575"));
                    }
                    icon.setIconSize(14);
                    
                    // Create label with file path
                    Label label = new Label(item.getFilePath());
                    label.setStyle("-fx-padding: 0 0 0 8;");
                    
                    // Create HBox with all elements
                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5, checkBox, icon, label);
                    hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    
                    setGraphic(hbox);
                }
            }
        });

        // Setup commits list view
        commitsListView.setItems(commits);
        commitsListView.setCellFactory(param -> new ListCell<CommitInfo>() {
            @Override
            protected void updateItem(CommitInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                    String dateStr = sdf.format(Date.from(item.getTimestamp()));
                    
                    Label idLabel = new Label(item.getShortId());
                    idLabel.setStyle("-fx-font-family: monospace; -fx-font-weight: bold;");
                    
                    Label messageLabel = new Label(item.getMessage());
                    messageLabel.setWrapText(true);
                    messageLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label authorLabel = new Label("by " + item.getAuthor());
                    authorLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11;");
                    
                    Label dateLabel = new Label(dateStr);
                    dateLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");
                    
                    javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(3);
                    javafx.scene.layout.HBox header = new javafx.scene.layout.HBox(8, idLabel, messageLabel);
                    javafx.scene.layout.HBox footer = new javafx.scene.layout.HBox(15, authorLabel, dateLabel);
                    
                    vbox.getChildren().addAll(header, footer);
                    setGraphic(vbox);
                }
            }
        });

        // Setup branches list
        branchesListView.setItems(FXCollections.observableArrayList());
    }

    /**
     * Load repository data
     */
    private void loadRepositoryData() {
        try {
            // Set repository name
            File repoDir = gitService.getRepositoryDirectory();
            repoNameLabel.setText(repoDir.getName());
            repoPathInfoLabel.setText(repoDir.getAbsolutePath());
            
            // Load branches
            List<String> branches = gitService.getBranches();
            branchComboBox.setItems(FXCollections.observableArrayList(branches));
            branchesListView.setItems(FXCollections.observableArrayList(branches));
            
            // Set current branch
            String currentBranch = gitService.getCurrentBranch();
            branchComboBox.setValue(currentBranch);
            currentBranchInfoLabel.setText(currentBranch);
            
            // Load changes
            refreshChanges();
            
            // Load commit history
            refreshCommits();
            
            logToConsole("Repository loaded: " + repoDir.getName());
            updateStatusMessage("Ready", false);
            
        } catch (Exception e) {
            showError("Failed to load repository data: " + e.getMessage());
        }
    }

    /**
     * Start file watcher service
     */
    private void startFileWatcher() {
        fileWatcherService = new FileWatcherService();
        try {
            String repoPath = gitService.getRepositoryDirectory().getAbsolutePath();
            fileWatcherService.startWatching(repoPath, (filePath, changeType) -> {
                logToConsole("File " + changeType + ": " + filePath);
                refreshChanges();
            });
            
            fileWatcherStatusLabel.setText("● File Watcher: Active");
            fileWatcherStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
        } catch (IOException e) {
            fileWatcherStatusLabel.setText("● File Watcher: Inactive");
            fileWatcherStatusLabel.setStyle("-fx-text-fill: #F44336;");
            logToConsole("Failed to start file watcher: " + e.getMessage());
        }
    }

    /**
     * Refresh file changes
     */
    private void refreshChanges() {
        try {
            List<FileChange> changes = gitService.getStatus();
            Platform.runLater(() -> {
                fileChanges.clear();
                fileChanges.addAll(changes);
                changesCountLabel.setText(changes.size() + " file" + (changes.size() != 1 ? "s" : ""));
            });
        } catch (Exception e) {
            showError("Failed to refresh changes: " + e.getMessage());
        }
    }

    /**
     * Refresh commits
     */
    private void refreshCommits() {
        try {
            List<CommitInfo> commitList = gitService.getRecentCommits(50);
            Platform.runLater(() -> {
                commits.clear();
                commits.addAll(commitList);
                commitsCountLabel.setText(commitList.size() + " commit" + (commitList.size() != 1 ? "s" : ""));
            });
        } catch (Exception e) {
            showError("Failed to refresh commits: " + e.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        logToConsole("Refreshing repository status...");
        refreshChanges();
        refreshCommits();
        try {
            String currentBranch = gitService.getCurrentBranch();
            branchComboBox.setValue(currentBranch);
            currentBranchInfoLabel.setText(currentBranch);
        } catch (IOException e) {
            showError("Failed to refresh branch: " + e.getMessage());
        }
        logToConsole("Refresh complete");
    }

    @FXML
    private void onStageAll() {
        try {
            gitService.stageAll();
            logToConsole("All changes staged");
            refreshChanges();
        } catch (Exception e) {
            showError("Failed to stage all: " + e.getMessage());
        }
    }

    @FXML
    private void onUnstageAll() {
        try {
            List<String> stagedFiles = fileChanges.stream()
                    .filter(FileChange::isStaged)
                    .map(FileChange::getFilePath)
                    .collect(Collectors.toList());
            
            if (!stagedFiles.isEmpty()) {
                gitService.unstageFiles(stagedFiles);
                logToConsole("All changes unstaged");
                refreshChanges();
            }
        } catch (Exception e) {
            showError("Failed to unstage all: " + e.getMessage());
        }
    }

    @FXML
    private void onDiscardChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Discard Changes");
        alert.setHeaderText("Are you sure you want to discard all uncommitted changes?");
        alert.setContentText("This action cannot be undone!");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Note: Implement discard logic using JGit checkout
            logToConsole("Discard changes functionality - implement with git checkout");
            showError("Discard functionality will be implemented in next version");
        }
    }

    @FXML
    private void onCommit() {
        String message = commitMessageArea.getText();
        if (message == null || message.trim().isEmpty()) {
            showError("Please enter a commit message");
            return;
        }

        try {
            String commitId = gitService.commit(message, credentials.getUsername(), 
                    credentials.getUsername() + "@git.local");
            logToConsole("Committed: " + commitId.substring(0, 7));
            commitMessageArea.clear();
            refreshChanges();
            refreshCommits();
            updateStatusMessage("Changes committed successfully", false);
        } catch (Exception e) {
            showError("Failed to commit: " + e.getMessage());
        }
    }

    @FXML
    private void onCommitAndPush() {
        onCommit();
        
        // Wait a bit for commit to complete, then push
        new Thread(() -> {
            try {
                Thread.sleep(500);
                Platform.runLater(this::onPush);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void onPush() {
        updateStatusMessage("Pushing to remote...", true);
        logToConsole("Pushing changes to remote repository...");
        
        new Thread(() -> {
            try {
                gitService.push();
                Platform.runLater(() -> {
                    logToConsole("Push completed successfully");
                    updateStatusMessage("Push completed", false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showError("Failed to push: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void onPull() {
        updateStatusMessage("Pulling from remote...", true);
        logToConsole("Pulling changes from remote repository...");
        
        new Thread(() -> {
            try {
                gitService.pull();
                Platform.runLater(() -> {
                    logToConsole("Pull completed successfully");
                    updateStatusMessage("Pull completed", false);
                    refreshChanges();
                    refreshCommits();
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showError("Failed to pull: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void onFetch() {
        updateStatusMessage("Fetching from remote...", true);
        logToConsole("Fetching changes from remote repository...");
        
        new Thread(() -> {
            try {
                gitService.fetch();
                Platform.runLater(() -> {
                    logToConsole("Fetch completed successfully");
                    updateStatusMessage("Fetch completed", false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> 
                    showError("Failed to fetch: " + e.getMessage())
                );
            }
        }).start();
    }

    @FXML
    private void onBranchChanged() {
        String selectedBranch = branchComboBox.getValue();
        if (selectedBranch != null) {
            try {
                String currentBranch = gitService.getCurrentBranch();
                if (!selectedBranch.equals(currentBranch)) {
                    gitService.checkoutBranch(selectedBranch);
                    logToConsole("Switched to branch: " + selectedBranch);
                    currentBranchInfoLabel.setText(selectedBranch);
                    refreshChanges();
                    refreshCommits();
                }
            } catch (Exception e) {
                showError("Failed to checkout branch: " + e.getMessage());
            }
        }
    }

    @FXML
    private void onNewBranch() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New Branch");
        dialog.setHeaderText("Enter new branch name");
        dialog.setContentText("Branch name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(branchName -> {
            try {
                gitService.createBranch(branchName);
                gitService.checkoutBranch(branchName);
                logToConsole("Created and switched to new branch: " + branchName);
                
                // Refresh branches
                List<String> branches = gitService.getBranches();
                branchComboBox.setItems(FXCollections.observableArrayList(branches));
                branchComboBox.setValue(branchName);
                branchesListView.setItems(FXCollections.observableArrayList(branches));
                currentBranchInfoLabel.setText(branchName);
            } catch (Exception e) {
                showError("Failed to create branch: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onOpenInExplorer() {
        try {
            File repoDir = gitService.getRepositoryDirectory();
            Desktop.getDesktop().open(repoDir);
        } catch (Exception e) {
            showError("Failed to open file explorer: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenInTerminal() {
        // This is platform-specific
        logToConsole("Open in terminal functionality - platform specific implementation needed");
        showError("This feature will be implemented for your platform");
    }

    @FXML
    private void onClearConsole() {
        consoleArea.clear();
    }

    /**
     * Log message to console
     */
    private void logToConsole(String message) {
        Platform.runLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            consoleArea.appendText("[" + timestamp + "] " + message + "\n");
        });
    }

    /**
     * Update status message
     */
    private void updateStatusMessage(String message, boolean isLoading) {
        Platform.runLater(() -> {
            statusMessageLabel.setText((isLoading ? "⟳ " : "● ") + message);
            statusMessageLabel.setStyle(isLoading ? 
                    "-fx-text-fill: #2196F3;" : "-fx-text-fill: #4CAF50;");
        });
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            
            logToConsole("ERROR: " + message);
            statusMessageLabel.setText("● " + message);
            statusMessageLabel.setStyle("-fx-text-fill: #F44336;");
        });
    }

    /**
     * Cleanup when closing
     */
    public void cleanup() {
        if (fileWatcherService != null) {
            fileWatcherService.shutdown();
        }
        if (gitService != null) {
            gitService.close();
        }
    }
}
