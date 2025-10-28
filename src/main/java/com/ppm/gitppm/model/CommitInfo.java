package com.ppm.gitppm.model;

import java.time.Instant;

public class CommitInfo {
    private String commitId;
    private String author;
    private String message;
    private Instant timestamp;
    private String shortId;

    public CommitInfo(String commitId, String author, String message, Instant timestamp) {
        this.commitId = commitId;
        this.author = author;
        this.message = message;
        this.timestamp = timestamp;
        this.shortId = commitId.substring(0, Math.min(7, commitId.length()));
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getShortId() {
        return shortId;
    }

    @Override
    public String toString() {
        return String.format("%s - %s: %s", shortId, author, message);
    }
}
