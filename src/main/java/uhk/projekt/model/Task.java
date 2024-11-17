package uhk.projekt.model;

public class Task {
    private int id = -1;
    private String title;
    private String description;
    private User creator;
    private User solver;
    private int priority;

    public Task() {

    }

    public Task(int id, String title, String description, User creator, User solver, int priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.solver = solver;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void set$title(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getSolver() {
        return solver;
    }

    public void setSolver(User solver) {
        this.solver = solver;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
