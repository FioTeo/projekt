package uhk.projekt.model;

import java.util.Date;

public class TimeLog {
    private int id = -1;
    private Task task;
    private User user;
    private String description;
    private float time;
    private Date createdAt;

    public TimeLog() {

    }

    public TimeLog(int id, Task task, User user, String description, float time, Date createdAt) {
        this.id = id;
        this.task = task;
        this.user = user;
        this.description = description;
        this.time = time;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
