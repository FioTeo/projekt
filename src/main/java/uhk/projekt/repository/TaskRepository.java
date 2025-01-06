package uhk.projekt.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uhk.projekt.model.Task;
import uhk.projekt.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @NotNull
    @EntityGraph(attributePaths = {"project"})
    Optional<Task> findById(@NotNull Integer id);
    List<Task> findByProjectId(Integer projectId);
    List<Task> findBySolverId(Integer userId);
    List<Task> findByCreatorId(Integer userId);
    List<Task> findBySolver(User solver);
}