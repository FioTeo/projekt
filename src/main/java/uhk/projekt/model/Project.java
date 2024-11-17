package uhk.projekt.model;

import jakarta.validation.constraints.Size;

import java.util.Date;

public class Project {
    private int id = -1;
    private String name;
    private String description;
    private User creator;
    private float budget;
    private Date createdAt;

    public Project() {
    }

    public Project(int id, String name, String description, User creator, float budget, Date createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.budget = budget;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
