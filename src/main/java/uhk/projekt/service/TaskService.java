package uhk.projekt.service;

import uhk.projekt.model.Task;
import uhk.projekt.model.User;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasks();
    List<Task> getAllTasksByProject(Integer projectId);
    List<Task> getAllTasksByUser(Integer userId); // Může být pro řešitele nebo tvůrce
    Optional<Task> getTaskById(Integer id);
    void saveTask(Task task);
    void deleteTaskById(Integer id);
    Optional<Task> getTaskByIdWithProject(Integer id);
    List<Task> getTasksAssignedTo(User user);
}