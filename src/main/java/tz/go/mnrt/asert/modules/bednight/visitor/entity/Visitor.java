package tz.go.mnrt.asert.modules.bednight.visitor.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.ReservationVisitor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.country.entity.Country;
import tz.go.mnrt.asert.modules.setup.identificationtype.entity.IdentificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "visitors")
public class Visitor extends BaseModel {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "country_id")
    private Integer countryId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 6)
    private String gender;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "permanent_physical_address")
    private String permanentPhysicalAddress;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "identification_type_id")
    private Integer identificationTypeId;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "passport_border")
    private String passportBorder;

    @Column(name = "doc_type")
    private String docType;

    @Column(name = "doc_entry_point")
    private String docEntryPoint;

    @Column(name = "doc_entry_date")
    private LocalDateTime docEntryDate;

    @Column(name = "doc_exit_date")
    private LocalDateTime docExitDate;

    @Column(name = "doc_residence_place")
    private String docResidencePlace;

    @Column(name = "visa_type")
    private String visaType;

    @Column(name = "visa_number")
    private String visaNumber;

    @Column(name = "visa_visit_purpose")
    private String visaVisitPurpose;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "identification_type_id", insertable = false, updatable = false)
    private IdentificationType   identificationType;

    @OneToMany(mappedBy = "visitor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationVisitor> reservationVisitors = new HashSet<>();
}
