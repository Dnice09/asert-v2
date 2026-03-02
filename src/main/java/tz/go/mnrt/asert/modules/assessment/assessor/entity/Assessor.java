package tz.go.mnrt.asert.modules.assessment.assessor.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;
import tz.go.mnrt.asert.modules.assessment.document.entity.AssessorDocument;
import tz.go.mnrt.asert.modules.assessment.educationbackground.entity.EducationBackground;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.entity.EmploymentHistory;
import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;
import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity.AssessorRejectionReason;
import tz.go.mnrt.asert.modules.user.entity.User;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "assessors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assessor extends BaseModel {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @Column(name = "phone_two", nullable = true, unique = true)
    private String phoneTwo;

    @Column(name = "photo")
    private String photo;

    @Column(name = "description")
    private String description;

    @Column(name = "profile_photo_id")
    private Long profilePhotoId;

    @OneToOne
    @JoinColumn(name = "profile_photo_id", insertable = false, updatable = false)
    private FileUpload profilePhoto;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "identification_id", nullable = false)
    private String identificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false)
    private IdentificationType identificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessorStatus status = AssessorStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Sex sex;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_applied")
    private LocalDateTime dateApplied;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dob", nullable = false)
    private LocalDate dob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", insertable = false, updatable = false)
    @JsonIgnore
    private AdminHierarchy location;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Lob
    @Column(columnDefinition = "TEXT", name = "verification_notes")
    private String verificationNotes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_verified")
    private LocalDateTime dateVerified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date_rejected")
    private LocalDateTime dateRejected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejection_reason_id", insertable = false, updatable = false)
    private AssessorRejectionReason reason;

    @Column(name = "rejection_reason_id")
    private Long reasonId;

    @Lob
    @Column(columnDefinition = "TEXT", name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToMany(mappedBy = "assessors")
    @JsonIgnore
    private Set<Hotel> hotels = new HashSet<>();

    @OneToMany(mappedBy = "assessor", fetch = FetchType.LAZY)
    private List<EducationBackground> educationBackgrounds;

    @OneToMany(mappedBy = "assessor", fetch = FetchType.LAZY)
    private List<AssessorCertification> certifications;

    @OneToMany(mappedBy = "assessor", fetch = FetchType.LAZY)
    private List<EmploymentHistory> employmentHistories;

    @OneToMany(mappedBy = "assessor", fetch = FetchType.LAZY)
    private List<AssessorDocument> documents;

    @OneToMany(mappedBy = "assessor", fetch = FetchType.LAZY)
    private List<AssessorReference> references;

    @OneToMany(mappedBy = "assessor")
    @JsonManagedReference
    private List<AssessorPreference> preferences;

    @OneToMany(mappedBy = "assessor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<AssessorCertificate> certificates;

    public void setProfilePhoto(FileUpload newProfilePhoto) {
        this.profilePhoto = newProfilePhoto;
        this.profilePhotoId = newProfilePhoto != null ? newProfilePhoto.getId() : null;
    }

    // Helper methods for certificates
    public void addCertificate(AssessorCertificate certificate) {
        if (certificates == null) {
            certificates = new java.util.ArrayList<>();
        }
        certificates.add(certificate);
        certificate.setAssessor(this);
    }

    public void removeCertificate(AssessorCertificate certificate) {
        if (certificates != null) {
            certificates.remove(certificate);
            certificate.setAssessor(null);
        }
    }

    public int getCertificateCount() {
        return certificates != null ? certificates.size() : 0;
    }

    public int getVerifiedCertificateCount() {
        if (certificates == null) {
            return 0;
        }
        return (int) certificates.stream()
                .filter(AssessorCertificate::isVerified)
                .count();
    }
}
