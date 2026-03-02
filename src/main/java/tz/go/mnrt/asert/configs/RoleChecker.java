package tz.go.mnrt.asert.configs;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.role.services.RoleService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;
import tz.go.mnrt.asert.modules.user.service.UserService;

/**
 * The RoleChecker class is a Spring component responsible for checking user
 * roles.
 * It checks both authentication-based roles and approval roles based on the
 * state of a facility.
 */
@Component
public class RoleChecker {

    @Autowired
    @Lazy
    private RoleService roleService;

    @Lazy
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Checks if the user has any authentication-based role.
     *
     * @param authentication the authentication object containing user details
     * @return true if the user has an authentication-based role, false otherwise
     */
    public boolean hasAuthBasedRole(Authentication authentication) {
        List<String> authBasedRoles = roleService.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authBasedRoles.contains(grantedAuthority.getAuthority()));
    }

    /**
     * Checks if the user has an approval role based on the state of the facility.
     *
     * @param authentication the authentication object containing user details
     * @param hotel          the facility entity
     * @return true if the user has an approval role for the facility's current
     *         state, false otherwise
     */
    @Transactional
    public boolean hasApprovalRoleBasedOnState(Authentication authentication, Hotel hotel) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);

        User user = userRepository.findByUuid(logged.getUuid())
                .orElseThrow(() -> new ValidationException("User with uuid {" + logged.getUuid() + "} not found"));

        List<HotelState> userRoleStates = user.getRoles().stream()
                .flatMap(role -> role.getStates().stream())
                .distinct()
                .collect(Collectors.toList());

        if (userRoleStates == null) {
            return false;
        }

        return userRoleStates.contains(hotel.getStatus());
    }
}
