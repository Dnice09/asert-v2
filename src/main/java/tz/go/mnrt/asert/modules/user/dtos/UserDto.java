package tz.go.mnrt.asert.modules.user.dtos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.user.entity.User;

@Setter
@Getter
@ToString
@Valid
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = { "roles" }, allowGetters = true)
public class UserDto implements Serializable {

    private Long id;

    private UUID uuid;

    @Email
    @NotNull(message = "Email is required")
    private String email;

    @NotNull
    private String firstName;

    private String middleName;

    @NotNull(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    private Boolean isActive = true;

    private Long adminHierarchyId;

    @JsonIgnoreProperties({ "authorities", "uuid" })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RoleRequestDto> roles = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Long> roleIds = new HashSet<>();

    @JsonProperty("fullName")
    public String fullName() {
        return String.format("%s %s %s", this.firstName, this.middleName, this.lastName);
    }

    public UserDto(User user) {
        user.toDao(this);
        setRoles(user.getRoles().stream().map(RoleRequestDto::new).collect(Collectors.toSet()));
    }
}
