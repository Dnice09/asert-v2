package tz.go.mnrt.asert.configs;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import lombok.RequiredArgsConstructor;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.adminhierarchy.service.AdminHierarchyService;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.adminhierarchylevel.repository.AdminHierarchyLevelRepository;
import tz.go.mnrt.asert.modules.adminhierarchylevel.services.AdminHierarchyLevelService;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.authority.repository.AuthorityRepository;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.role.repository.RoleRepository;
import tz.go.mnrt.asert.modules.role.services.AdminPermissionService;
import tz.go.mnrt.asert.modules.role.services.RoleService;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.user.repository.UserRepository;
import tz.go.mnrt.asert.utils.Util;

@Component
@RequiredArgsConstructor
public class Initializer implements ApplicationRunner {
    private final AdminPermissionService adminPermissionService;

    @Value("${asert.default-password}")
    private String defaultPassword;
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final EntityManager em;
    private final UserRepository userRepository;
    private final AdminHierarchyService adminHierarchyService;
    private final AdminHierarchyLevelRepository adminHierarchyLevelRepository;
    private final AdminHierarchyRepository adminHierarchyRepository;
    private final PasswordEncoder bcryptEncoder;
    private final RequestMappingInfoHandlerMapping requestMappingHandlerMapping;
    private final AdminHierarchyLevelService adminHierarchyLevelService;
    private final RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void initialize() {
        adminPermissionService.setAdminPermissions();
    }

    @Value("${environment}")
    private String environment;

    private void initializeAuthorities() {
        try {
            requestMappingHandlerMapping.getHandlerMethods()
                    .forEach(new BiConsumer<RequestMappingInfo, HandlerMethod>() {
                        @Override
                        public void accept(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
                            NoAuthorization noAuthorization = handlerMethod.getMethodAnnotation(NoAuthorization.class);

                            if (requestMappingInfo.getPatternValues().size() > 0) {
                                List<String> urls = new ArrayList<>(requestMappingInfo.getPatternValues());

                                if (requestMappingInfo.getMethodsCondition().getMethods().size() != 0
                                        && urls.size() == 1 && urls.get(0).contains("api") && noAuthorization == null) {
                                    String resourceName = Util
                                            .parseResource(handlerMethod.getBeanType().getSimpleName());
                                    String actionName = handlerMethod.getMethod().getName();
                                    String actionDisplayName = splitCamelCase(actionName);
                                    actionDisplayName = actionDisplayName.substring(0, 1).toUpperCase()
                                            + actionDisplayName.substring(1);
                                    if (authorityRepository.findByResourceAndAction(resourceName, actionName) == null) {
                                        Authority authority = new Authority();
                                        authority.setAction(actionName);
                                        authority.setResource(resourceName);
                                        authority.setName(actionDisplayName);
                                        authorityRepository.save(authority);
                                    }
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error("############# Error on initializing authorities #####################", e);
            System.exit(1);
        }
    }

    private String splitCamelCase(String s) {
        return s.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
    }

    private void initializeAdminHierarchyLevel(Role adminRole, Role clientRole, Role dmoRole) {
        String name = "COUNTRY";
        String code = "001";
        int position = 1;
        boolean exists = this.adminHierarchyLevelService.exists(code, position);
        if (!exists) {
            AdminHierarchyLevel level = new AdminHierarchyLevel();
            level.setName(name);
            level.setCode(code);
            level.setPosition(position);
            level.setRoles(new HashSet<>(roleService.findAll()));
            AdminHierarchyLevel created = adminHierarchyLevelRepository.save(level);
            initializeAdminHierarchy(created, adminRole, clientRole, dmoRole);
        } else {
            Optional<AdminHierarchyLevel> row = adminHierarchyLevelService.findByCode(code);
            row.ifPresent(adminHierarchyLevel -> initializeAdminHierarchy(adminHierarchyLevel, adminRole, clientRole,
                    dmoRole));
        }
    }

    private void initializeAdminHierarchy(AdminHierarchyLevel adminLevel, Role adminRole, Role clientRole,
            Role dmoRole) {
        String name = "ZANZIBAR";
        String code = "ZNZ001";
        boolean exists = this.adminHierarchyService.exists(code);
        if (!exists) {
            AdminHierarchy level = new AdminHierarchy();
            level.setName(name);
            level.setCode(code);
            level.setAdminHierarchyLevel(em.getReference(AdminHierarchyLevel.class, adminLevel.getId()));
            AdminHierarchy adminHierarchy = adminHierarchyRepository.save(level);
            initializeAdmin(adminHierarchy, adminRole);
            initializeClient(adminHierarchy, clientRole);
            initializeDmo(adminHierarchy, dmoRole);
        } else {
            AdminHierarchy adminHierarchy = adminHierarchyRepository.findFirstByOrderByIdAsc().get();
            initializeAdmin(adminHierarchy, adminRole);
            initializeClient(adminHierarchy, clientRole);
            initializeDmo(adminHierarchy, dmoRole);
        }
    }

    private void initializeAdmin(AdminHierarchy adminHierarchy, Role role) {
        try {
            String email = "admin@mnrt.go.tz";
            Optional<User> optionalUser = userRepository.findUserByEmailIgnoreCase(email);
            if (optionalUser.isEmpty()) {
                User user = new User();
                user.setFirstName("Super Admin");
                user.setEmail(email);
                user.setPhoneNumber("0700000998");
                user.setAdminHierarchyId(adminHierarchy.getId());
                user.setPassword(bcryptEncoder.encode(defaultPassword));
                user.setIsActive(true);
                user.getRoles().add(role);
                userRepository.save(user);
                logger.info("The user with name username Super admin  has been created successfully");
            }
        } catch (Exception e) {
            logger.error("############# Error on initializing users #####################", e);
            System.exit(1);
        }
    }

    private void initializeClient(AdminHierarchy adminHierarchy, Role role) {
        try {
            String client = "client@mnrt.go.tz";
            Optional<User> optionalClient = userRepository.findUserByEmailIgnoreCase(client);
            if (optionalClient.isEmpty()) {
                User user = new User();
                user.setFirstName("Client");
                user.setLastName("User");
                user.setAdminHierarchyId(adminHierarchy.getId());
                user.setEmail(client);
                user.setPhoneNumber("0700000999");
                user.setPassword(bcryptEncoder.encode(defaultPassword));
                user.setIsActive(true);
                user.getRoles().add(role);
                userRepository.save(user);
                logger.info("The user with name username client  has been created successfully");
            }
        } catch (Exception e) {
            logger.error("############# Error on initializing client user #####################", e);
            System.exit(1);
        }
    }

    private void initializeDmo(AdminHierarchy adminHierarchy, Role role) {
        try {
            String client = "dmo@mnrt.go.tz";
            Optional<User> optionalClient = userRepository.findUserByEmailIgnoreCase(client);
            if (optionalClient.isEmpty()) {
                User user = new User();
                user.setFirstName("DMO");
                user.setLastName("DMO");
                user.setAdminHierarchyId(adminHierarchy.getId());
                user.setEmail(client);
                user.setPhoneNumber("0700000888");
                user.setPassword(bcryptEncoder.encode(defaultPassword));
                user.setIsActive(false);
                user.getRoles().add(role);
                userRepository.save(user);
                logger.info("The user with name username dmo  has been created successfully");
            }
        } catch (Exception e) {
            logger.error("############# Error on initializing client user #####################", e);
            System.exit(1);
        }
    }

    private Role initializeAdminRole() {
        Optional<Role> roleOptional = roleRepository.findRoleByName("SUPER ADMINISTRATOR");
        if (roleOptional.isEmpty()) {
            Set<Authority> authorities = new HashSet<>(authorityRepository.findAll());
            Role role = new Role();
            role.setName("SUPER ADMINISTRATOR");
            role.setIsClient(false);
            role.setCode(String.join("_", role.getName().split(" ")));
            role.setAuthorities(authorities);
            logger.info("The role with name SUPER ADMINISTRATOR has been created successfully");
            return roleRepository.save(role);
        }
        return roleOptional.get();
    }

    private Role initializeClientRole() {
        String roleName = "CLIENT USER";
        Optional<Role> roleOptional = roleRepository.findRoleByName(roleName);
        if (roleOptional.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setIsClient(true);
            role.setCode(roleName);
            role.setAuthorities(new HashSet<>());
            logger.info("The role with name {} has been created successfully", roleName);
            return roleRepository.save(role);
        }
        return roleOptional.get();
    }

    private Role initializeDmoRole() {
        String roleName = "DMO";
        Optional<Role> roleOptional = roleRepository.findRoleByName(roleName);
        if (roleOptional.isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setIsClient(true);
            role.setCode(roleName);
            role.setAuthorities(new HashSet<>());
            logger.info("The role with name {} has been created successfully", roleName);
            return roleRepository.save(role);
        }
        return roleOptional.get();
    }

    private void assignAuthoritiesToSuperAdminRole() {
        List<Authority> authorities = authorityRepository.findAll();
        Set<Authority> authoritySet = new HashSet<>(authorities);

        // Get the super administrator role
        Optional<Role> roleOptional = roleRepository.findRoleByName("SUPER ADMINISTRATOR");
        Role role = null;
        if (roleOptional.isPresent()) {
            role = roleOptional.get();
            authoritySet = role.getAuthorities();
        }
        assert role != null;
        role.setAuthorities(authoritySet);
        roleRepository.save(role);
        logger.info("Authorities assigned to super administrator's role");
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        logger.info("****************************");
        System.out.println(Base64.getEncoder().encodeToString(keyGenerator.generateKey().getEncoded()));
        initializeAuthorities();
        Role adminRole = initializeAdminRole();
        Role clientRole = initializeClientRole();
        Role dmoRole = initializeDmoRole();
        initializeAdminHierarchyLevel(adminRole, clientRole, dmoRole);
        assignAuthoritiesToSuperAdminRole();
        initializeSetupTables();
    }

    private void initializeSetupTables() {
    }

}
