package uhk.projekt.service;

import org.springframework.stereotype.Service;
import uhk.projekt.model.Project;

import java.util.ArrayList;

@Service
public class ProjectServiceImp implements ProjectService {

    ArrayList<Project> projects = new ArrayList<>();

    @Override
    public ArrayList<Project> getAllProjects() {
        return projects;
    }

    @Override
    public Project getProjectById(int id) {
        if(id > -1 && id < projects.size()) {
            return projects.get(id);
        }
        return null;
    }

    @Override
    public void deleteProjectById(int id) {
        if(id > -1 && id < projects.size()) {
            projects.remove(id);
        }
    }

    @Override
    public void saveProject(Project project) {
        if(project.getId() > -1) {
            projects.remove(project.getId());
        }
        projects.add(project);
    }
}
