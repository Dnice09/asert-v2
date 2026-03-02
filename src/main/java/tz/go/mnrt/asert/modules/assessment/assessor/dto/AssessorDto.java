package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.IdentificationType;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Sex;
import tz.go.mnrt.asert.modules.setup.fileupload.dtos.FileUploadListDto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String description;
    private String phoneTwo;
    private String photo;
    private FileUploadListDto profilePhoto;
    private String title;
    private String identificationId;
    private IdentificationType identificationType;
    private AssessorStatus status;
    private Sex sex;
    private Long locationId;
    private String locationName;
    private Long verifiedBy;
    private String verificationNotes;
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateVerified;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateRejected;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateApplied;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private Long reasonId;
    private String rejectionDescription;
    private String rejectionReason;
    private AssessorUserDto user;
    private Integer completionPercentage;

    public AssessorDto(Assessor entity, String uploadedFilesUrl) {
        entity.toDao(this);
        this.user = new AssessorUserDto(entity.getUser());
        this.name = entity.getFirstName() + " " + entity.getMiddleName() + " " + entity.getLastName();

        if (entity.getLocation() != null) {
            this.locationId = entity.getLocation().getId();
            this.locationName = entity.getLocation().getName();
        }

        if (entity.getReason() != null) {
            this.rejectionDescription = entity.getRejectionReason();
            this.rejectionReason = entity.getReason().getReason();
        }

        if (entity.getPhoto() != null && entity.getUuid() != null) {
            // Return photo download URL instead of base64 data for performance
            this.photo = "/api/v1/uploads/" + entity.getUuid() + "/view";
        }

        if (entity.getProfilePhoto() != null) {
            this.profilePhoto = new FileUploadListDto(entity.getProfilePhoto());
        }
    }
}
