package tz.go.mnrt.asert.modules.role.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.annotations.IsTrackable;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.user.entity.User;

@Getter
@Setter
@Entity
@Builder
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseModel {

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "level_id")
    private Long levelId;

    @Column(name = "is_client")
    private Boolean isClient;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "role_id")
    @Column(name = "state", nullable = false)
    @Builder.Default
    private Set<HotelState> states = new HashSet<>();

    @ManyToOne()
    @JoinColumn(name = "level_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({ "roles", "adminHierarchies" })
    private AdminHierarchyLevel level;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    @JsonIgnoreProperties({ "roles", "adminHierarchy" })
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(name = "role_authorities", joinColumns = { @JoinColumn(name = "role_id") }, inverseJoinColumns = {
            @JoinColumn(name = "authority_id") })
    @JsonIgnoreProperties({ "menuItems", "roles" })
    @IsTrackable
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    public void addState(HotelState state) {
        states.add(state);
    }

    public void removeState(HotelState state) {
        states.remove(state);
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
        authority.getRoles().add(this);
    }

    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.getRoles().remove(this);
    }
}
