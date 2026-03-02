package tz.go.mnrt.asert.modules.role.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.authority.dtos.AuthorityDto;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.role.entity.Role;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Valid
@JsonIgnoreProperties(value = { "authorities" }, allowGetters = true)
public class RoleResponseDto implements Serializable {
    private Long id;
    private UUID uuid;

    private String code;

    @NotNull
    private String name;

    private Long levelId;

    private String levelName;

    private Boolean isClient;

    private boolean hasApprovalStages;

    private Set<HotelState> states;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> authorityIds = new ArrayList<>();

    @JsonIgnoreProperties({ "resource", "action", "uuid" })
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AuthorityDto> authorities = new ArrayList<>();

    public RoleResponseDto(Role role) {
        BeanUtils.copyProperties(role, this);
        if (role.getLevel() != null) {
            levelId = role.getLevel().getId();
            levelName = role.getLevel().getName();
        }
        authorities = null;
        authorityIds = null;
    }

    public RoleResponseDto withStates(Role role) {
        BeanUtils.copyProperties(role, this);
        if (role.getLevel() != null) {
            levelId = role.getLevel().getId();
            levelName = role.getLevel().getName();
        }
        authorities = null;
        authorityIds = null;

        hasApprovalStages = role.getStates().size() > 0;

        setStates(role.getStates());

        return this;
    }

    public RoleResponseDto withAuthorities(Set<Authority> authorities) {
        setAuthorities(authorities.stream().map(AuthorityDto::new).collect(Collectors.toList()));
        return this;
    }
}
