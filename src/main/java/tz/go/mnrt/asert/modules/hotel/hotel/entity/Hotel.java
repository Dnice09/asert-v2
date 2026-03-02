package tz.go.mnrt.asert.modules.hotel.hotel.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.entity.HotelAssessmentApproval;
import tz.go.mnrt.asert.modules.company.entity.Company;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;
import tz.go.mnrt.asert.modules.user.entity.User;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hotels")
public class Hotel extends BaseModel {
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "website")
    private String website;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "description")
    private String description;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private HotelState status;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    @Column(name = "default_image_id", nullable = false)
    private Long defaultImageId;

    @OneToOne
    @JoinColumn(name = "default_image_id", insertable = false, updatable = false)
    private HotelMedia defaultImage;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private AdminHierarchy AdminHierarchy;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoomType> roomTypes = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<HotelFacility> facilities = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<HotelMedia> media = new HashSet<>();

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "hotel_assessors", joinColumns = @JoinColumn(name = "hotel_id"), inverseJoinColumns = @JoinColumn(name = "assessor_id"))
    @Builder.Default
    private Set<Assessor> assessors = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FormSubmission> formSubmissions = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<HotelAssessmentApproval> assessmentApprovals = new HashSet<>();

    public void setDefaultImage(HotelMedia newDefaultImage) {
        if (newDefaultImage != null && !newDefaultImage.getHotel().equals(this)) {
            throw new IllegalArgumentException("Default image must belong to this hotel");
        }

        // If there was a previous default image, update its flag
        if (this.defaultImage != null && !this.defaultImage.equals(newDefaultImage)) {
            this.defaultImage.setIsDefault(false);
        }

        // Set the new default image and update its flag
        this.defaultImage = newDefaultImage;
        if (newDefaultImage != null) {
            newDefaultImage.setIsDefault(true);
        }
    }

    public void addAssessor(Assessor assessor) {
        this.assessors.add(assessor);
        assessor.getHotels().add(this);
    }

    public void removeAssessor(Assessor assessor) {
        this.assessors.remove(assessor);
        assessor.getHotels().remove(this);
    }

    public void addAssessmentApproval(HotelAssessmentApproval approval) {
        assessmentApprovals.add(approval);
        approval.setHotel(this);
    }

    public void removeAssessmentApproval(HotelAssessmentApproval approval) {
        assessmentApprovals.remove(approval);
        approval.setHotel(null);
    }
}
