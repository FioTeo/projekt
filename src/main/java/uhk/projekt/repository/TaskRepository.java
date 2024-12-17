package uhk.projekt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uhk.projekt.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByProjectId(Integer projectId);
    List<Task> findBySolverId(Integer userId);
    List<Task> findByCreatorId(Integer userId);
}