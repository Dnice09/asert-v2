package tz.go.mnrt.asert.modules.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.company.dtos.CompanyResponseDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.user.entity.User;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
@Valid
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "roles" }, allowGetters = true)
public class UserListDto implements Serializable {

    private Long id;

    private UUID uuid;

    private String email;

    private String firstName;

    private String middleName;

    private String lastName;

    private String phoneNumber;

    private Boolean isActive = true;

    private Boolean isApproved;

    private Long adminHierarchyId;

    private String adminHierarchyName;

    private CompanyResponseDto company;

    @JsonIgnoreProperties({ "authorities", "uuid" })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RoleRequestDto> roles = new HashSet<>();

    @JsonProperty("fullName")
    public String fullName() {
        return String.format("%s %s %s", this.firstName, this.middleName, this.lastName);
    }

    public UserListDto(User user) {
        BeanUtils.copyProperties(user, this);
        if (user.getCompany() != null) {
            CompanyResponseDto responseDto = new CompanyResponseDto();
            responseDto.setId(user.getCompanyId());
            responseDto.setName(user.getCompany().getName());
            responseDto.setWebsite(user.getCompany().getWebsite());
            responseDto.setEmail(user.getCompany().getEmail());
            responseDto.setUuid(user.getCompany().getUuid());
            responseDto.setCompanyTypeId(user.getCompany().getCompanyTypeId());
            company = responseDto;
        }
        adminHierarchyName = user.getAdminHierarchy() != null ? user.getAdminHierarchy().getName() : null;
        roles = null;
    }

    public UserListDto withRoles(Set<Role> roles) {
        setRoles(roles.stream().map(RoleRequestDto::new).collect(Collectors.toSet()));
        return this;
    }

    public UserListDto userWithRoles(User user) {
        BeanUtils.copyProperties(user, this);

        this.setRoles(user.getRoles().stream().map(RoleRequestDto::new).collect(Collectors.toSet()));
        adminHierarchyName = user.getAdminHierarchy() != null ? user.getAdminHierarchy().getName() : null;
        return this;
    }
}
