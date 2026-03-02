package tz.go.mnrt.asert.modules.company.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.contactperson.entity.ContactPerson;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
public class Company extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "trading_name")
    private String tradingName;

    @Column(name = "company_type_id", nullable = false)
    private Long companyTypeId;

    @Column(name = "website")
    private String website;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "tin", nullable = false)
    private String tin;

    @Column(name = "vat")
    private String vat;

    @Column(name = "registration_type", nullable = false)
    private String registrationType;

    @Column(name = "certificate_registration_number", nullable = false, unique = true)
    private String certificateRegistrationNumber;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "registration_status", nullable = false)
    private String registrationStatus;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_type_id", insertable = false, updatable = false)
    private CompanyType companyType;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ContactPerson> contactPersons;
}
