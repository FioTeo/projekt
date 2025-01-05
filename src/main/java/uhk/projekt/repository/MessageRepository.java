package uhk.projekt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uhk.projekt.model.Message;
import uhk.projekt.model.Project;
import uhk.projekt.model.Task;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByTask(Task task);
    List<Message> findByTask_Project_Id(Integer projectId);
    List<Message> findByTaskId(Integer taskId);

}

