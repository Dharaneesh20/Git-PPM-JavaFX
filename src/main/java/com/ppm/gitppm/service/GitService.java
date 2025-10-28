package com.ppm.gitppm.service;

import com.ppm.gitppm.model.CommitInfo;
import com.ppm.gitppm.model.FileChange;
import com.ppm.gitppm.model.GitCredentials;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GitService {
    private Git git;
    private Repository repository;
    private GitCredentials credentials;
    private CredentialsProvider credentialsProvider;

    public GitService() {
    }

    /**
     * Open an existing Git repository
     */
    public void openRepository(String repositoryPath, GitCredentials credentials) throws IOException {
        this.credentials = credentials;
        File repoDir = new File(repositoryPath);
        
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(new File(repoDir, ".git"))
                .readEnvironment()
                .findGitDir()
                .build();
        
        git = new Git(repository);
        
        // Setup credentials provider
        if (credentials != null) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(
                    credentials.getUsername(),
                    credentials.getEffectivePassword()
            );
        }
    }

    /**
     * Clone a repository from remote URL
     */
    public void cloneRepository(String remoteUrl, String localPath, GitCredentials credentials) 
            throws GitAPIException {
        this.credentials = credentials;
        
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(
                credentials.getUsername(),
                credentials.getEffectivePassword()
        );

        git = Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(new File(localPath))
                .setCredentialsProvider(provider)
                .call();
        
        repository = git.getRepository();
        credentialsProvider = provider;
    }

    /**
     * Get the status of the repository (changed files)
     */
    public List<FileChange> getStatus() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        Status status = git.status().call();
        List<FileChange> changes = new ArrayList<>();

        // Added files
        for (String file : status.getAdded()) {
            changes.add(new FileChange(file, FileChange.ChangeType.ADDED, true));
        }

        // Modified files
        for (String file : status.getModified()) {
            changes.add(new FileChange(file, FileChange.ChangeType.MODIFIED, false));
        }

        // Changed files (staged modifications)
        for (String file : status.getChanged()) {
            changes.add(new FileChange(file, FileChange.ChangeType.MODIFIED, true));
        }

        // Removed files
        for (String file : status.getRemoved()) {
            changes.add(new FileChange(file, FileChange.ChangeType.DELETED, true));
        }

        // Missing files
        for (String file : status.getMissing()) {
            changes.add(new FileChange(file, FileChange.ChangeType.MISSING, false));
        }

        // Untracked files
        for (String file : status.getUntracked()) {
            changes.add(new FileChange(file, FileChange.ChangeType.UNTRACKED, false));
        }

        // Conflicting files
        for (String file : status.getConflicting()) {
            changes.add(new FileChange(file, FileChange.ChangeType.CONFLICTING, false));
        }

        return changes;
    }

    /**
     * Stage files for commit
     */
    public void stageFiles(List<String> filePaths) throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        for (String filePath : filePaths) {
            git.add().addFilepattern(filePath).call();
        }
    }

    /**
     * Stage all changes
     */
    public void stageAll() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        git.add().addFilepattern(".").call();
    }

    /**
     * Unstage files
     */
    public void unstageFiles(List<String> filePaths) throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        for (String filePath : filePaths) {
            git.reset().addPath(filePath).call();
        }
    }

    /**
     * Commit staged changes
     */
    public String commit(String message, String authorName, String authorEmail) throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        RevCommit commit = git.commit()
                .setMessage(message)
                .setAuthor(authorName, authorEmail)
                .call();

        return commit.getId().getName();
    }

    /**
     * Push changes to remote repository
     */
    public Iterable<PushResult> push() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        return git.push()
                .setCredentialsProvider(credentialsProvider)
                .call();
    }

    /**
     * Pull changes from remote repository
     */
    public PullResult pull() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        return git.pull()
                .setCredentialsProvider(credentialsProvider)
                .call();
    }

    /**
     * Fetch changes from remote
     */
    public void fetch() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        git.fetch()
                .setCredentialsProvider(credentialsProvider)
                .call();
    }

    /**
     * Get recent commits
     */
    public List<CommitInfo> getRecentCommits(int count) throws GitAPIException, IOException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        List<CommitInfo> commits = new ArrayList<>();
        Iterable<RevCommit> logs = git.log().setMaxCount(count).call();

        for (RevCommit commit : logs) {
            PersonIdent author = commit.getAuthorIdent();
            commits.add(new CommitInfo(
                    commit.getId().getName(),
                    author.getName(),
                    commit.getShortMessage(),
                    author.getWhen().toInstant()
            ));
        }

        return commits;
    }

    /**
     * Get current branch name
     */
    public String getCurrentBranch() throws IOException {
        if (repository == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        return repository.getBranch();
    }

    /**
     * Get all branches
     */
    public List<String> getBranches() throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        List<String> branches = new ArrayList<>();
        List<Ref> refs = git.branchList().call();

        for (Ref ref : refs) {
            String name = ref.getName();
            if (name.startsWith("refs/heads/")) {
                branches.add(name.substring("refs/heads/".length()));
            }
        }

        return branches;
    }

    /**
     * Create a new branch
     */
    public void createBranch(String branchName) throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        git.branchCreate()
                .setName(branchName)
                .call();
    }

    /**
     * Checkout a branch
     */
    public void checkoutBranch(String branchName) throws GitAPIException {
        if (git == null) {
            throw new IllegalStateException("Repository not initialized");
        }

        git.checkout()
                .setName(branchName)
                .call();
    }

    /**
     * Get repository directory
     */
    public File getRepositoryDirectory() {
        if (repository == null) {
            return null;
        }
        return repository.getDirectory().getParentFile();
    }

    /**
     * Check if repository is initialized
     */
    public boolean isRepositoryInitialized() {
        return git != null && repository != null;
    }

    /**
     * Close the repository
     */
    public void close() {
        if (git != null) {
            git.close();
        }
        if (repository != null) {
            repository.close();
        }
    }
}
