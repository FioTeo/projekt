package uhk.projekt.model;

import java.util.Date;

public class Message {
    private int id =-1;
    private Task task;
    private String message;
    private User user;
    private Date createdAt;

    public Message() {

    }

    public Message(int id, Task task, String message, User user, Date createdAt) {
        this.id = id;
        this.task = task;
        this.message = message;
        this.user = user;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
