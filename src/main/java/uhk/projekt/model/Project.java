package uhk.projekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Název projektu je povinný")
    @Size(max = 100, message = "Název projektu může mít maximálně 100 znaků")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Popis projektu může mít maximálně 500 znaků")
    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Min(value = 0, message = "Rozpočet musí být kladný")
    @Column(nullable = false)
    private int budget;

    @Column(nullable = false)
    private String createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    public Project() {
    }

    public Project(int id, String name, String description, User creator, float budget, String createdAt) {
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
