package tz.go.mnrt.asert.modules.user.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.authority.dtos.AuthorityDto;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.user.entity.User;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserDto {

    private Long id;

    private UUID uuid;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String firstName;

    private String middleName;

    private String lastName;

    private Long adminHierarchyId;

    private String adminHierarchyName;

    private String levelName;

    private String levelCode;

    private Integer levelPosition;

    private Boolean passwordChanged;

    private Long companyId;

    private Boolean isClient;

    private String fullName;

    private List<AuthorityDto> authorities = new ArrayList<>();

    private List<RoleRequestDto> roles = new ArrayList<>();

    private List<MenuGroupDto> menus = new ArrayList<>();

    public CurrentUserDto(User user, List<MenuGroupDto> itemAsGroup) {
        BeanUtils.copyProperties(user, this);
        adminHierarchyId = user.getAdminHierarchyId();

        if (user.getAdminHierarchy() != null) {
            adminHierarchyName = user.getAdminHierarchy().getName();
            levelName = user.getAdminHierarchy().getAdminHierarchyLevel().getName();
            levelCode = user.getAdminHierarchy().getAdminHierarchyLevel().getCode();
            levelPosition = user.getAdminHierarchy().getAdminHierarchyLevel().getPosition();
        }

        user.getRoles().forEach(r -> {
            RoleRequestDto roleDto = new RoleRequestDto(r).withStates(r);
            roles.add(roleDto);
            r.getAuthorities().forEach(a -> authorities.add(new AuthorityDto(a)));
        });

        // if any or user's roles has isClient = true then set isClient to true
        setIsClient(user.getRoles().stream().anyMatch(r -> r.getIsClient() != null && r.getIsClient()));

        menus = itemAsGroup;
    }
}
