package com.mayankgoyal.clonegoogletasks;

public class Task {
    private String taskName;
    private String date;
    private String key;
    public Task() {}

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }
}
