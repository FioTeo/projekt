package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uhk.projekt.model.Task;
import uhk.projekt.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImp implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksByProject(Integer projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasksByUser(Integer userId) {
        return taskRepository.findBySolverId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    @Override
    @Transactional
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(Integer id) {
        taskRepository.deleteById(id);
    }
}