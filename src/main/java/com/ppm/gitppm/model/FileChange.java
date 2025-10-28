package com.ppm.gitppm.model;

public class FileChange {
    private String filePath;
    private ChangeType changeType;
    private boolean staged;

    public enum ChangeType {
        ADDED("Added", "#4CAF50"),
        MODIFIED("Modified", "#FF9800"),
        DELETED("Deleted", "#F44336"),
        UNTRACKED("Untracked", "#2196F3"),
        CONFLICTING("Conflicting", "#9C27B0"),
        MISSING("Missing", "#FF5722");

        private final String displayName;
        private final String color;

        ChangeType(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getColor() {
            return color;
        }
    }

    public FileChange(String filePath, ChangeType changeType, boolean staged) {
        this.filePath = filePath;
        this.changeType = changeType;
        this.staged = staged;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public boolean isStaged() {
        return staged;
    }

    public void setStaged(boolean staged) {
        this.staged = staged;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", 
            staged ? "✓" : " ", 
            changeType.getDisplayName(), 
            filePath);
    }
}
