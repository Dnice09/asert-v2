package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorCertificate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessorCertificateDto {

    private Long id;
    private UUID uuid;

    @NotBlank(message = "Certificate name is required")
    private String certificateName;

    @NotBlank(message = "Issuing authority is required")
    private String issuingAuthority;

    private String certificateNumber;

    @NotNull(message = "Issue date is required")
    private LocalDate issueDate;

    private LocalDate expiryDate;
    private String description;

    @NotBlank(message = "Certificate type is required")
    private String certificateType; // "ACADEMIC", "PROFESSIONAL", "TRAINING", "LICENSE"

    private String verificationStatus; // "PENDING", "VERIFIED", "REJECTED"
    private String verificationNotes;
    private Boolean isActive = true;

    private Long assessorId;
    private UUID assessorUuid;

    // File upload fields
    private Long fileUploadId;
    private String fileName;
    private String fileType;
    private Long fileSize;

    // Computed fields
    private Boolean isExpired;
    private Boolean isExpiringSoon;
    private Boolean hasFile;
    private String statusDisplayName;
    private String fileUrl;

    public AssessorCertificateDto(AssessorCertificate certificate) {
        this.id = certificate.getId();
        this.uuid = certificate.getUuid();
        this.certificateName = certificate.getCertificateName();
        this.issuingAuthority = certificate.getIssuingAuthority();
        this.certificateNumber = certificate.getCertificateNumber();
        this.issueDate = certificate.getIssueDate();
        this.expiryDate = certificate.getExpiryDate();
        this.description = certificate.getDescription();
        this.certificateType = certificate.getCertificateType();
        this.verificationStatus = certificate.getVerificationStatus();
        this.verificationNotes = certificate.getVerificationNotes();
        this.isActive = certificate.getIsActive();
        this.fileUrl = "/api/v1/uploads/" + certificate.getUuid() + "/view";

        // File upload fields
        this.fileUploadId = certificate.getFileUploadId();
        if (certificate.getFileUpload() != null) {
            this.fileName = certificate.getFileUpload().getName();
            this.fileType = certificate.getFileUpload().getFileType();
            this.fileSize = certificate.getFileUpload().getFileSize();
        }

        // Assessor fields
        if (certificate.getAssessor() != null) {
            this.assessorId = certificate.getAssessor().getId();
            this.assessorUuid = certificate.getAssessor().getUuid();
        }

        // Computed fields
        this.isExpired = certificate.isExpired();
        this.isExpiringSoon = certificate.isExpiringSoon();
        this.hasFile = certificate.hasFile();
        this.statusDisplayName = certificate.getStatusDisplayName();
    }
}
