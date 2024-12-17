package uhk.projekt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uhk.projekt.model.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
}
