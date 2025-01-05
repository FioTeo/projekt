package uhk.projekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Název úkolu je povinný")
    @Size(max = 100, message = "Název úkolu může mít maximálně 100 znaků")
    @Column(nullable = false, length = 100)
    private String name; // Změna z 'title' na 'name'

    @Size(max = 500, message = "Popis úkolu může mít maximálně 500 znaků")
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solver_id")
    private User solver;

    @Min(value = 1, message = "Priorita musí být alespoň 1")
    @Column(nullable = false)
    private int priority;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeLog> timeLogs;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Task() {

    }

    public Task(Integer id, String name, String description, User creator, User solver, int priority, Project project) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.solver = solver;
        this.priority = priority;
        this.project = project;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter a Setter pro 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) { // Opravený setter
        this.name = name;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<TimeLog> getTimeLogs() {
        return timeLogs;
    }

    public void setTimeLogs(List<TimeLog> timeLogs) {
        this.timeLogs = timeLogs;
    }
}