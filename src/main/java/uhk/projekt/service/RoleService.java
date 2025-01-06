package uhk.projekt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uhk.projekt.model.Role;
import uhk.projekt.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Získá všechny dostupné role.
     *
     * @return seznam všech rolí
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Najde roli podle názvu.
     *
     * @param name název role
     * @return Optional s rolí, pokud existuje
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Vytvoří novou roli.
     *
     * @param role role k vytvoření
     * @return vytvořená role
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Najde role podle názvů.
     *
     * @param roleNames seznam názvů rolí
     * @return sada rolí
     */
    public Set<Role> getRolesByNames(Set<String> roleNames) {
        List<Role> roles = roleRepository.findAllByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new RuntimeException("Některé role nebyly nalezeny.");
        }
        return new HashSet<>(roles);
    }
}
