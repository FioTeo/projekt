package uhk.projekt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uhk.projekt.model.TimeLog;
import uhk.projekt.model.User;
import uhk.projekt.model.Task;
import uhk.projekt.model.Project;

import java.util.List;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Integer> {
    List<TimeLog> findByUserId(Integer userId);
    List<TimeLog> findByTaskId(Integer taskId);
    List<TimeLog> findByTask_ProjectId(Integer projectId);
}