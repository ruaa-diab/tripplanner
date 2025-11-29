package com.example.tripplanner;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String title;
    private String category;
    private String priority;
    private String dueDate;
    private String notes;
    private boolean completed;

    public Task() {}

    public Task(String id, String title, String category, String priority,
                String dueDate, String notes, boolean completed) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.dueDate = dueDate;
        this.notes = notes;
        this.completed = completed;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}