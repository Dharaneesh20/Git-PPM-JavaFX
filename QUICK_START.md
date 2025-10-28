# Git PPM - Quick Start Guide

## 🚀 Running the Application

### Method 1: Using Maven (Recommended)
```bash
cd /home/ninja/Desktop/local-pickup-scheduler/Git-PPM
mvn clean javafx:run
```

### Method 2: Using the JAR file
```bash
cd /home/ninja/Desktop/local-pickup-scheduler/Git-PPM
mvn clean package
java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -jar target/Git-PPM-1.0-SNAPSHOT.jar
```

## 📖 Quick Setup

### 1. **First Launch**
- Run the application using Maven: `mvn javafx:run`
- You'll see the login screen with a beautiful gradient background

### 2. **Choose Authentication**
Select from the dropdown:
- **Username & Password**: For basic Git authentication
- **Personal Access Token**: Recommended for GitHub (more secure)
- **Sign in with GitHub**: OAuth guide provided

### 3. **Open or Clone Repository**

#### Opening Existing Repository:
1. Click "Browse" button
2. Navigate to your Git repository folder
3. Enter your credentials
4. Click "Open Repository"

#### Cloning New Repository:
1. Click "Browse" to select destination folder
2. Enter credentials
3. Click "Clone Repository"
4. Enter remote repository URL (e.g., https://github.com/username/repo.git)
5. Wait for cloning to complete

### 4. **Using the Main Dashboard**

#### Left Panel - File Changes:
- ✅ **Check files** to stage them for commit
- 📝 **Write commit message** in the text area
- 🟢 **Commit** button to commit staged changes
- 🚀 **Commit & Push** to commit and push in one action

#### Top Toolbar:
- 🌿 **Branch dropdown**: Switch between branches
- ➕ **New Branch**: Create a new branch
- 🔄 **Refresh**: Reload repository status
- ⬇️ **Pull**: Pull changes from remote
- ⬆️ **Push**: Push commits to remote

#### Right Panel - Tabs:
- 📜 **Commit History**: View recent commits
- ℹ️ **Repository Info**: See repo details and all branches
- 💻 **Console**: Monitor Git operations in real-time

## 🔑 GitHub Personal Access Token Setup

1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Give it a descriptive name (e.g., "Git PPM")
4. Select scopes:
   - ✅ `repo` (Full control of private repositories)
   - ✅ `workflow` (if using GitHub Actions)
5. Click "Generate token"
6. **Copy the token immediately** (you won't see it again!)
7. In Git PPM:
   - Select "Personal Access Token" auth method
   - Username: Your GitHub username
   - Token: Paste the generated token

## 🎨 Features at a Glance

| Feature | Description |
|---------|-------------|
| 🔍 **Auto File Detection** | Real-time monitoring of file changes |
| ✅ **Interactive Staging** | Checkbox-based file staging |
| 🎨 **Color Coding** | Visual indicators for file status |
| 🌿 **Branch Management** | Create and switch branches easily |
| 📝 **Commit History** | View recent commits with details |
| 🔄 **Pull/Push/Fetch** | Full remote repository sync |
| 💻 **Console Logging** | Real-time operation feedback |
| 📊 **Repository Info** | Complete repository details |

## 🎯 Common Workflows

### Making Changes and Committing:
1. Edit files in your repository
2. See changes appear automatically in the app
3. Check boxes next to files you want to commit
4. Write a commit message
5. Click "Commit" or "Commit & Push"

### Pulling Latest Changes:
1. Click "Pull" button in toolbar
2. Wait for operation to complete
3. See console output for details

### Creating a New Branch:
1. Click "New Branch" button
2. Enter branch name
3. New branch is created and checked out automatically

### Switching Branches:
1. Use the branch dropdown in toolbar
2. Select desired branch
3. Working directory updates automatically

## 🐛 Troubleshooting

### "Repository not initialized" error
- Make sure you've selected a valid Git repository
- Check that the folder contains a `.git` directory

### Authentication failures
- Verify username and password/token are correct
- For GitHub, use Personal Access Token instead of password
- Check internet connectivity

### File changes not detected
- Check the "File Watcher: Active" status in status bar
- Click "Refresh" button to manually update

### Build errors
```bash
# Clean and rebuild
mvn clean install

# If modules error, delete target folder
rm -rf target/
mvn clean compile
```

## 📝 Development Info

**Built with:**
- JavaFX 17 - UI Framework
- JGit 6.7.0 - Git Implementation
- Java 17 - Runtime
- Maven - Build Tool

**Project Structure:**
- `controller/` - UI Controllers
- `model/` - Data Models
- `service/` - Business Logic
- `resources/` - FXML & CSS files

## 👤 Credits

**Made by Dharaneesh R S**

A modern, user-friendly Git client built with JavaFX and JGit.

---

For more information, see [README.md](README.md)
