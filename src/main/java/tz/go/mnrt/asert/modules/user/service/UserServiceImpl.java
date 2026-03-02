package tz.go.mnrt.asert.modules.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tz.go.mnrt.asert.helpers.Utils;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.core.rest.errors.DeleteException;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;
import tz.go.mnrt.asert.modules.menuitem.service.MenuItemService;
import tz.go.mnrt.asert.modules.passwordresettoken.dtos.PasswordResetDto;
import tz.go.mnrt.asert.modules.passwordresettoken.entity.PasswordResetToken;
import tz.go.mnrt.asert.modules.passwordresettoken.repository.PasswordResetTokenRepository;
import tz.go.mnrt.asert.modules.passwordresettoken.services.PasswordResetTokenService;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.role.repository.RoleRepository;
import tz.go.mnrt.asert.modules.user.dtos.*;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends SimpleSearchService<User> implements UserService {
    @Autowired
    private RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EntityManager em;
    private final MenuItemService menuItemService;
    private final AdminHierarchyRepository adminHierarchyRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final AssessorRepository assessorRepository;

    private final Validator validator;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${asert.default-password:password}")
    private String defaultPassword;

    public UserServiceImpl(UserRepository userRepository, EntityManager em, MenuItemService menuItemService,
            AdminHierarchyRepository adminHierarchyRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            AssessorRepository assessorRepository,
            PasswordResetTokenService passwordResetTokenService) {
        this.userRepository = userRepository;
        this.em = em;
        this.menuItemService = menuItemService;
        this.adminHierarchyRepository = adminHierarchyRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.assessorRepository = assessorRepository;
        this.passwordResetTokenService = passwordResetTokenService;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public UserDto save(UserDto userDto) {
        log.info("saving user {} ", userDto);
        User user = new User();

        if (userDto.getUuid() != null) {
            user = userRepository
                    .findByUuid(userDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "User with uuid {" + userDto.getUuid() + "} does not exist"));
            user.setRoles(new HashSet<>());
        } else {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }

        BeanUtils.copyProperties(userDto, user, "uuid");
        assert (user.getUuid() != null);

        if (userDto.getAdminHierarchyId() != null) {
            user.setAdminHierarchyId(userDto.getAdminHierarchyId());
        } else {
            user.setAdminHierarchyId(null);
        }

        for (Long roleId : userDto.getRoleIds()) {
            user.addRole(em.getReference(Role.class, roleId));
        }

        // Set the full name before saving
        user.updateFullName();

        user = userRepository.save(user);

        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public ExternalUserDto registerUser(ExternalUserDto userDto) {
        User user = new User();

        Set<ConstraintViolation<ExternalUserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<ExternalUserDto> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new ValidationException(sb.toString());
        }

        if (userDto.getUuid() != null) {
            user = userRepository
                    .findByUuid(userDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "User with uuid {" + userDto.getUuid() + "} does not exist"));
            user.setRoles(new HashSet<>());
        } else {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }

        BeanUtils.copyProperties(userDto, user, "uuid");
        assert (user.getUuid() != null);

        List<Role> roles = roleRepository.findByNames(List.of("ASSESSOR", "FACILITY_OWNER"));
        if (!roles.isEmpty()) {
            for (Role role : roles) {
                Long roleId = role.getId();
                user.addRole(em.getReference(Role.class, roleId));
            }
        }
        user.updateFullName();

        user.setIsApproved(false);
        user = userRepository.save(user);

        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public ExternalUserDto registerAssessor(ExternalUserDto userDto) {
        User user = new User();

        Set<ConstraintViolation<ExternalUserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<ExternalUserDto> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new ValidationException(sb.toString());
        }

        if (userDto.getUuid() != null) {
            user = userRepository
                    .findByUuid(userDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "User with uuid {" + userDto.getUuid() + "} does not exist"));
            user.setRoles(new HashSet<>());
        } else {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }

        BeanUtils.copyProperties(userDto, user, "uuid");
        assert (user.getUuid() != null);

        List<Role> roles = roleRepository.findByNames(List.of("ASSESSOR"));
        if (!roles.isEmpty()) {
            for (Role role : roles) {
                Long roleId = role.getId();
                user.addRole(em.getReference(Role.class, roleId));
            }
        }
        user.updateFullName();
        user.setIsApproved(false);

        user = userRepository.save(user);

        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public ExternalUserDto registerFacilityServiceOwner(ExternalUserDto userDto) {
        User user = new User();

        Set<ConstraintViolation<ExternalUserDto>> violations = validator.validate(userDto);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<ExternalUserDto> violation : violations) {
                sb.append(violation.getMessage()).append("\n");
            }
            throw new ValidationException(sb.toString());
        }

        if (userDto.getUuid() != null) {
            user = userRepository
                    .findByUuid(userDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "User with uuid {" + userDto.getUuid() + "} does not exist"));
            user.setRoles(new HashSet<>());
        } else {
            user.setPassword(passwordEncoder.encode(defaultPassword));
        }

        BeanUtils.copyProperties(userDto, user, "uuid");
        assert (user.getUuid() != null);

        List<Role> roles = roleRepository.findByNames(List.of("FACILITY_OWNER"));
        if (!roles.isEmpty()) {
            for (Role role : roles) {
                Long roleId = role.getId();
                user.addRole(em.getReference(Role.class, roleId));
            }
        }
        user.updateFullName();
        user.setIsApproved(false);

        user = userRepository.save(user);

        userDto.setId(user.getId());
        return userDto;
    }

    @Override
    public Page<UserListDto> findAll(Pageable page, Map<String, String> search) {
        log.info("finding paginated users with page {}  with search term {} ", page, search);
        LoggedInUserDto logged = loggedIn().orElse(null);
        Specification<User> specs = createSpecification(User.class, search);

        if (search != null && search.containsKey("isApproved")) {
            String isApproved = search.get("isApproved");
            log.info("finding paginated users with isApproved value {}  ", isApproved);
            if (isApproved != null) {
                Boolean boolValue = Boolean.parseBoolean(isApproved);
                log.info("Converted isApproved string '{}' to boolean: {}", isApproved, boolValue);
                specs = specs
                        .and((root, query, cb) -> cb.equal(root.get("isApproved"), boolValue));
            }
        }

        // Temporarily disable admin hierarchy filtering for debugging
        if (false && logged != null && logged.getAdminHierarchyId() != null) {
            List<Long> adminIds = adminHierarchyRepository.getUserAdminAreas(logged.getAdminHierarchyId());
            log.info("Applying admin hierarchy filter. Logged user admin hierarchy: {}, allowed admin IDs: {}",
                    logged.getAdminHierarchyId(), adminIds);
            specs = specs.and(((root, query, cb) -> root.get("adminHierarchyId").in(adminIds)));
        } else {
            log.info("No admin hierarchy filtering applied. Logged user: {}",
                    logged != null ? logged.getEmail() : "null");
        }

        return userRepository.findAll(specs, page).map(user -> new UserListDto().userWithRoles(user));
    }

    @Override
    public UserListDto findById(UUID uuid) {
        log.info("finding user with uuid {} ", uuid);
        return userRepository
                .findByUuid(uuid)
                .map(u -> new UserListDto(u).withRoles(u.getRoles()))
                .orElseThrow(() -> new ValidationException("User with uuid {" + uuid + "} not found"));
    }

    @Override
    public UserListDto findByUserId(Long userId) {
        log.info("finding user with id {} ", userId);
        return userRepository
                .findById(userId)
                .map(u -> new UserListDto(u).withRoles(u.getRoles()))
                .orElseThrow(() -> new ValidationException("User with id {" + userId + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting user with uuid {} ", uuid);
        try {
            User user = userRepository
                    .findByUuid(uuid)
                    .orElseThrow(
                            () -> new ValidationException(
                                    String.format("User with uuid {%s} not found", uuid)));
            user.setRoles(new HashSet<>());
            userRepository.delete(user);
        } catch (DataIntegrityViolationException e) {
            throw new DeleteException(User.class.getName());
        }
    }

    @Override
    public CurrentUserDto findUserInfo(String email) {
        log.info("Find  user by {} ", email);

        User user = userRepository
                .findUserByEmailWithAuthorities(email)
                .orElseThrow(() -> new ValidationException("No user with email " + email));

        log.info("Found  user is {} ", user.getFirstName());

        List<Long> authIds = new ArrayList<>();
        authIds.add(0L);
        user.getRoles()
                .forEach(
                        r -> r.getAuthorities()
                                .forEach(
                                        a -> {
                                            authIds.add(a.getId());
                                        }));
        Set<MenuItem> menuItems = menuItemService.getByAuthorities(authIds);
        Set<MenuItem> menuItems2 = menuItemService.getWithNoGroupByAuthorities(authIds);
        Map<String, List<Long>> groupItemIds = Utils.getMenuGroup(menuItems);

        List<MenuGroupDto> itemAsGroup = menuItems2.stream()
                .map(
                        i -> new MenuGroupDto(
                                i.getId(),
                                i.getName(),
                                i.getIcon(),
                                i.getState(),
                                i.getSortOrder(),
                                i.getTranslationLabel()))
                .collect(Collectors.toList());
        log.info("get items and group filtered by corresponding ids");
        itemAsGroup.addAll(menuItemService.getWithItems(groupItemIds));
        log.info("Sorting groups");

        Collections.sort(
                itemAsGroup,
                (o1, o2) -> (o1.getSortOrder() != null ? o1.getSortOrder() : 0)
                        - (o2.getSortOrder() != null ? o2.getSortOrder() : 0));

        // Fetch notifications for the current user - Kz
        log.info("Loading notifications for user {}.", user.getFirstName());
        CurrentUserDto currentUserDto = new CurrentUserDto(user, itemAsGroup);
        // currentUserDto.setNotifications(notifications);

        return currentUserDto;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordDto dto) {
        LoggedInUserDto logged = loggedIn().orElse(null);
        assert logged != null;
        userRepository.changePassword(logged.getUuid(), passwordEncoder.encode(dto.getNewPassword()));
    }

    @Override
    @Transactional
    public void resetPassword(UUID uuid) {
        String password = passwordEncoder.encode(defaultPassword);
        userRepository.resetPassword(uuid, password);
    }

    @Override
    public Optional<LoggedInUserDto> loggedIn(Principal principal) {
        return userRepository.findLoggedIn(principal.getName());
    }

    @SuppressWarnings("unused")
    @Override
    public Optional<LoggedInUserDto> loggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(auth.getName());
        if (auth == null) {
            return Optional.empty();
        } else {
            return userRepository.findLoggedIn(auth.getName());
        }
    }

    @Override
    public boolean isClient(Set<Role> roles) {
        return roles.stream().anyMatch(r -> r.getCode().equals("CLIENT_USER"));
    }

    @Override
    public boolean isDmo(Set<Role> roles) {
        return roles.stream().anyMatch(r -> r.getCode().equals("DMO"));
    }

    @Override
    public boolean isBoardMember(Set<Role> roles) {
        return roles.stream().anyMatch(r -> r.getCode().equals("BOARD_MEMBER"));
    }

    @Override
    public boolean isRegistrar(Set<Role> roles) {
        return roles.stream().anyMatch(r -> r.getCode().equals("FACILITY_REGISTRAR"));
    }

    @Override
    public void forgotPassword(ForgotPasswordDto dto, HttpServletRequest request) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ValidationException("User with email " + dto.getEmail() + " not found")));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String token = UUID.randomUUID().toString();

            // save token
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUserId(user.getId());
            passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
            passwordResetTokenRepository.save(passwordResetToken);

            // send reset link
            String baseUrl = UriComponentsBuilder.newInstance()
                    .scheme(request.getScheme())
                    .host(request.getServerName())
                    .build()
                    .toString();
            String resetLink = baseUrl + "/reset-password/" + token;
            passwordResetTokenService.sendResetLink(user.getFullName(), user.getEmail(), resetLink);
        }
    }

    @Override
    public void resetUserPassword(PasswordResetDto dto) {
        Optional<PasswordResetToken> optionalPasswordResetToken = passwordResetTokenRepository
                .findByToken(dto.getToken());
        if (optionalPasswordResetToken.isEmpty()) {
            throw new ValidationException("Token not provided");
        }

        PasswordResetToken resetToken = optionalPasswordResetToken.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Token expired");
        }

        Optional<User> optionalUser = userRepository.findById(resetToken.getUserId());
        if (optionalUser.isEmpty()) {
            throw new ValidationException("User not found");
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserListDto approve(UUID uuid) {
        log.info("finding user with uuid {} ", uuid);
        User user = userRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("User with uuid {" + uuid + "} not found"));

        user.setIsApproved(true);
        user = userRepository.save(user);
        // check if user has an assessor role then save him/her as an assessor
        boolean isAssessor = user.getRoles().stream()
                .anyMatch(r -> r.getCode().equals("ASSESSOR") || r.getCode().equals("ASSESSOR"));

        // lets log the isAssessor value
        log.info("Is user with uuid {} an assessor? {}", uuid, isAssessor);

        // generate the assessor creation object
        Assessor assessor = new Assessor();

        if (isAssessor) {
            BeanUtils.copyProperties(user, assessor, "id", "uuid");
            assessor.setUser(user);
            assessor.setUserId(user.getId());
            assessor.setPhone(user.getPhoneNumber());

            assessor = assessorRepository.save(assessor);

            log.info("Assessor with id {} created for user {}", assessor.getId(), user.getEmail());
        }

        log.info("User with uuid {} approved successfully", uuid);
        return new UserListDto(user).withRoles(user.getRoles());
    }
}
