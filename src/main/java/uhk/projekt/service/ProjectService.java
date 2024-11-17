package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Project;

import java.util.ArrayList;

@Service
public interface ProjectService {

    ArrayList<Project> getAllProjects();
    Project getProjectById(int id);
    void deleteProjectById(int id);
    void saveProject(Project project);
}
