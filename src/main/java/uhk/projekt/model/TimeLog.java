package uhk.projekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_logs")
public class TimeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Popis je povinný")
    @Size(max = 255, message = "Popis může mít maximálně 255 znaků")
    @Column(nullable = false, length = 255)
    private String description;

    @Min(value = 0, message = "Čas musí být kladný")
    @Column(nullable = false)
    private float time;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TimeLog() {

    }

    public TimeLog(Integer id, Task task, User user, String description, float time, LocalDateTime createdAt) {
        this.id = id;
        this.task = task;
        this.user = user;
        this.description = description;
        this.time = time;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
