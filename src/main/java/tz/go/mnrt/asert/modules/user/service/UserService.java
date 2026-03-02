package tz.go.mnrt.asert.modules.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.passwordresettoken.dtos.PasswordResetDto;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.user.dtos.*;
import tz.go.mnrt.asert.modules.user.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    UserDto save(UserDto userDto);

    ExternalUserDto registerUser(ExternalUserDto userDto);

    ExternalUserDto registerAssessor(ExternalUserDto userDto);

    ExternalUserDto registerFacilityServiceOwner(ExternalUserDto userDto);

    Page<UserListDto> findAll(Pageable page, Map<String, String> search);

    UserListDto findById(UUID uuid);

    UserListDto approve(UUID uuid);

    UserListDto findByUserId(Long userId);

    void delete(UUID uuid);

    Optional<LoggedInUserDto> loggedIn(Principal principal);

    Optional<LoggedInUserDto> loggedIn();

    CurrentUserDto findUserInfo(String name);

    void changePassword(ChangePasswordDto dto);

    void resetPassword(UUID uuid);

    Optional<User> findByEmail(String email);

    boolean isDmo(Set<Role> roles);

    boolean isClient(Set<Role> roles);

    boolean isBoardMember(Set<Role> roles);

    boolean isRegistrar(Set<Role> roles);

    void forgotPassword(ForgotPasswordDto dto, HttpServletRequest request);

    void resetUserPassword(PasswordResetDto dto);
}
