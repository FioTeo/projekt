package uhk.projekt.converter;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import uhk.projekt.model.Role;
import uhk.projekt.service.RoleService;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.Annotation;

public class StringToRoleConverter implements Converter<String, Role> {

    private final RoleService roleService;

    @Autowired
    public StringToRoleConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    public Role convert(@NotNull String source) {
        return roleService.getRoleByName(source)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + source));
    }
}
