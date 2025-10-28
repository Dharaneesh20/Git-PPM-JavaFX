package com.ppm.gitppm.service;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileWatcherService {
    private WatchService watchService;
    private ExecutorService executorService;
    private boolean running = false;
    private Path repositoryPath;
    private FileChangeListener listener;
    private Map<WatchKey, Path> watchKeys = new HashMap<>();

    public interface FileChangeListener {
        void onFileChanged(String filePath, String changeType);
    }

    public FileWatcherService() {
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("FileWatcherThread");
            return thread;
        });
    }

    /**
     * Start watching a repository directory for changes
     */
    public void startWatching(String repositoryPath, FileChangeListener listener) throws IOException {
        if (running) {
            stopWatching();
        }

        this.repositoryPath = Paths.get(repositoryPath);
        this.listener = listener;
        this.watchService = FileSystems.getDefault().newWatchService();

        // Register the repository directory and subdirectories
        registerDirectory(this.repositoryPath);
        
        running = true;

        executorService.submit(() -> {
            try {
                watchForChanges();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Register a directory and all its subdirectories with the watch service
     */
    private void registerDirectory(Path directory) throws IOException {
        // Don't watch .git directory
        if (directory.endsWith(".git")) {
            return;
        }

        WatchKey key = directory.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
        );
        watchKeys.put(key, directory);

        // Register subdirectories
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry) && !entry.getFileName().toString().equals(".git")) {
                    registerDirectory(entry);
                }
            }
        }
    }

    /**
     * Watch for file system changes
     */
    private void watchForChanges() throws InterruptedException {
        while (running) {
            WatchKey key = watchService.take();
            Path directory = watchKeys.get(key);

            if (directory == null) {
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();
                Path fullPath = directory.resolve(fileName);

                // Ignore .git directory changes
                if (fullPath.toString().contains(".git")) {
                    continue;
                }

                // Get relative path from repository root
                String relativePath = repositoryPath.relativize(fullPath).toString();
                String changeType = getChangeType(kind);

                // Notify listener on JavaFX thread
                if (listener != null) {
                    Platform.runLater(() -> listener.onFileChanged(relativePath, changeType));
                }

                // If a new directory was created, register it
                if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(fullPath)) {
                    try {
                        registerDirectory(fullPath);
                    } catch (IOException e) {
                        System.err.println("Failed to register new directory: " + fullPath);
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.remove(key);
                if (watchKeys.isEmpty()) {
                    break;
                }
            }
        }
    }

    /**
     * Convert WatchEvent.Kind to readable string
     */
    private String getChangeType(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
            return "CREATED";
        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            return "MODIFIED";
        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
            return "DELETED";
        }
        return "UNKNOWN";
    }

    /**
     * Stop watching for changes
     */
    public void stopWatching() {
        running = false;
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                System.err.println("Error closing watch service: " + e.getMessage());
            }
        }
        watchKeys.clear();
    }

    /**
     * Check if currently watching
     */
    public boolean isWatching() {
        return running;
    }

    /**
     * Shutdown the service
     */
    public void shutdown() {
        stopWatching();
        executorService.shutdownNow();
    }
}
