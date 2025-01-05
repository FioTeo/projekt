package uhk.projekt.service;

import uhk.projekt.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    List<Project> getAllProjects();
    Optional<Project> getProjectById(Integer id);
    void saveProject(Project project);
    void deleteProjectById(Integer id);
}
