package uhk.projekt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Jméno je povinné")
    @Size(max = 50, message = "Jméno může mít maximálně 50 znaků")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Příjmení je povinné")
    @Size(max = 50, message = "Příjmení může mít maximálně 50 znaků")
    @Column(nullable = false, length = 50)
    private String surname;

    @NotBlank(message = "Email je povinný")
    @Email(message = "Neplatný formát emailu")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Heslo je povinné")
    @Size(min = 6, message = "Heslo musí mít alespoň 6 znaků")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String createdAt;

    public User() {
    }

    public User(int id, String name, String surname, String email, String password, String createdAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
