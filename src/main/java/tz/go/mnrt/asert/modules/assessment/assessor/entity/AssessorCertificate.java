package tz.go.mnrt.asert.modules.assessment.assessor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.fileupload.entity.FileUpload;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assessor_certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessorCertificate extends BaseModel {

    @Column(name = "certificate_name", nullable = false)
    private String certificateName;

    @Column(name = "issuing_authority", nullable = false)
    private String issuingAuthority;

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "certificate_type")
    private String certificateType; // "ACADEMIC", "PROFESSIONAL", "TRAINING", "LICENSE"

    @Column(name = "verification_status")
    private String verificationStatus; // "PENDING", "VERIFIED", "REJECTED"

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_assessor_certificate_assessor"))
    @JsonBackReference
    private Assessor assessor;

    @Column(name = "file_upload_id")
    private Long fileUploadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_upload_id", insertable = false, updatable = false)
    private FileUpload fileUpload;

    // Helper methods

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return expiryDate.isBefore(thirtyDaysFromNow) && !isExpired();
    }

    public boolean isVerified() {
        return "VERIFIED".equalsIgnoreCase(verificationStatus);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(verificationStatus) || verificationStatus == null;
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(verificationStatus);
    }

    public String getStatusDisplayName() {
        if (isVerified()) {
            return "Verified";
        } else if (isRejected()) {
            return "Rejected";
        } else {
            return "Pending Verification";
        }
    }

    public boolean hasFile() {
        return fileUploadId != null;
    }

    public String getFileName() {
        return fileUpload != null ? fileUpload.getName() : null;
    }

    public String getFileType() {
        return fileUpload != null ? fileUpload.getFileType() : null;
    }

    public Long getFileSize() {
        return fileUpload != null ? fileUpload.getFileSize() : null;
    }
}