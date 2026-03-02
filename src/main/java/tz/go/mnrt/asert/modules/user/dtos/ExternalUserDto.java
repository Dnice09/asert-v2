package tz.go.mnrt.asert.modules.user.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.user.entity.User;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
@JsonIgnoreProperties(value = {"roles"}, allowGetters = true)
public class ExternalUserDto implements Serializable {
    private Long id;

    private UUID uuid;

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be in a valid format")
    private String phoneNumber;

    private Boolean isActive = true;

    private String registrationType;

    private Long adminHierarchyId;

    @JsonIgnoreProperties({"authorities", "uuid"})
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<RoleRequestDto> roles = new HashSet<>();

    @JsonProperty("fullName")
    public String fullName() {
        return String.format("%s %s %s", this.firstName, this.middleName, this.lastName);
    }

    public ExternalUserDto(User user) {
        user.toDao(this);
        setRoles(user.getRoles().stream().map(RoleRequestDto::new).collect(Collectors.toSet()));
    }
}
