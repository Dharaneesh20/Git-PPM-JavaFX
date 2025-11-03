# Git PPM - Professional Git Project Manager

![Made by Dharaneesh R S](https://img.shields.io/badge/Made%20by-Dharaneesh%20R%20S-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-17-orange)
![JGit](https://img.shields.io/badge/JGit-6.7.0-green)

A modern, interactive JavaFX application for managing Git repositories with a beautiful user interface.

## 🚀 Features

### Authentication Options
- **Username & Password**: Traditional Git authentication
- **Personal Access Token (PAT)**: Secure token-based authentication
- **GitHub OAuth**: Sign in with GitHub (guide provided)

### Core Functionality
- 📂 **Open Existing Repository**: Browse and open local Git repositories
- 📥 **Clone Repository**: Clone remote repositories to local machine
- 🔍 **File Change Detection**: Automatic real-time monitoring of file changes
- 📝 **Stage/Unstage Files**: Interactive file staging with checkboxes
- 💬 **Commit Changes**: Create commits with custom messages
- ⬆️ **Push to Remote**: Push committed changes to remote repository
- ⬇️ **Pull from Remote**: Pull latest changes from remote repository
- 🔄 **Fetch Updates**: Fetch remote changes without merging
- 🌿 **Branch Management**: Create, switch, and view branches
- 📊 **Commit History**: View recent commits with detailed information
- 🖥️ **Console Output**: Real-time logging of all Git operations

### User Interface Highlights
- 🎨 Modern gradient design with smooth animations
- 📱 Responsive layout with split-pane views
- 🌈 Color-coded file change indicators:
  - 🟢 Added files
  - 🟠 Modified files
  - 🔴 Deleted files
  - 🔵 Untracked files
  - 🟣 Conflicting files
- 🔔 Real-time file watcher with visual status indicator
- 📋 Interactive lists with custom cell renderers
- 🎯 Intuitive toolbar with quick actions

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git installed on your system

## 🛠️ Installation & Setup

1. **Clone the repository**:
   ```bash
   git clone <your-repo-url>
   cd Git-PPM
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn javafx:run
   ```

## 📖 How to Use

### Initial Setup

1. **Launch the application** - You'll see the login screen with authentication options

2. **Choose Authentication Method**:
   - Select your preferred authentication method from the dropdown
   - Fill in credentials based on your selection

3. **Select Repository**:
   - Click "Browse" to select a repository folder
   - Use "Open Repository" for existing local repos
   - Use "Clone Repository" to clone from a remote URL

### Main Dashboard

Once logged in, you'll see the main dashboard with:

#### Left Panel - Changes & Commits
- View all modified, added, deleted, and untracked files
- Check boxes to stage/unstage individual files
- Use "Stage All" or "Unstage All" for bulk operations
- Write commit messages and commit changes
- Quick "Commit & Push" button for streamlined workflow

#### Right Panel - Information
- **Commit History Tab**: View recent commits with author, date, and message
- **Repository Info Tab**: See repository details, branches, and quick actions
- **Console Tab**: Monitor all Git operations in real-time

#### Top Toolbar
- View current repository and branch
- Create new branches
- Refresh status
- Fetch, Pull, and Push operations
- File watcher status indicator

## 🔐 Setting Up Personal Access Token (PAT)

For GitHub repositories, we recommend using Personal Access Tokens:

1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Click "Generate new token (classic)"
3. Select the following scopes:
   - `repo` (Full control of private repositories)
   - `workflow` (Update GitHub Action workflows)
4. Generate and copy the token
5. Use the token as your password in the app

[📚 Full PAT Documentation](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)

## 🏗️ Project Structure

```
Git-PPM/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ppm/gitppm/
│   │   │       ├── controller/
│   │   │       │   ├── LoginController.java
│   │   │       │   └── MainController.java
│   │   │       ├── model/
│   │   │       │   ├── CommitInfo.java
│   │   │       │   ├── FileChange.java
│   │   │       │   ├── GitAuthType.java
│   │   │       │   └── GitCredentials.java
│   │   │       ├── service/
│   │   │       │   ├── FileWatcherService.java
│   │   │       │   └── GitService.java
│   │   │       ├── HelloApplication.java
│   │   │       └── Launcher.java
│   │   └── resources/
│   │       └── com/ppm/gitppm/
│   │           ├── login-view.fxml
│   │           ├── main-view.fxml
│   │           └── styles.css
│   └── test/
├── pom.xml
└── README.md
```

## 🔧 Technologies Used

- **JavaFX 17**: Modern UI framework
- **JGit 6.7.0**: Pure Java implementation of Git
- **Ikonli**: Icon library (FontAwesome 5)
- **ControlsFX**: Additional JavaFX controls
- **SLF4J**: Logging framework
- **Maven**: Build and dependency management

## 🎨 Key Features Breakdown

### Automatic File Watching
The application uses Java's `WatchService` API to monitor the repository directory for changes in real-time. Any file modifications, creations, or deletions are automatically detected and reflected in the UI.

### Interactive Staging
Each file in the changes list has an associated checkbox for easy staging/unstaging without command-line interaction.

### Visual Feedback
- Real-time status messages
- Color-coded file change types
- Console logging of all operations
- Loading indicators for async operations

### Branch Management
- View all local branches
- Create new branches with custom names
- Switch between branches seamlessly
- Current branch indicator in toolbar

## 🐛 Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Check Maven installation: `mvn -version`
- Clean and rebuild: `mvn clean install`

### Authentication failures
- Verify credentials are correct
- For GitHub, use Personal Access Token instead of password
- Check network connectivity

### File changes not detected
- Ensure repository folder is selected correctly
- Check file watcher status indicator
- Try clicking "Refresh" button

## 📝 Future Enhancements

- [ ] Full GitHub OAuth implementation
- [ ] Diff viewer for file changes
- [ ] Merge conflict resolution UI
- [ ] Tag management
- [ ] Remote repository management
- [ ] Git configuration editor
- [ ] Stash management
- [ ] Cherry-pick and rebase support

## 👨‍💻 Author

**Dharaneesh R S**

A passionate developer creating modern, user-friendly applications.

## 📄 License

This project is open-source and available under the MIT License.

## 🙏 Acknowledgments

- Eclipse JGit team for the excellent Git implementation
- JavaFX community for the powerful UI framework
- FontAwesome for the beautiful icons
---

**Made with ❤️ by Dharaneesh R S**
