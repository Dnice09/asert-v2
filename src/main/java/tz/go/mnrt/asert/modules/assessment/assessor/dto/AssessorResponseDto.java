package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Sex;

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
public class AssessorResponseDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    private String description;
    private String photo;
    private String title;
    private String identificationId;
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

    public AssessorResponseDto(Assessor entity) {
        entity.toDao(this);
    }
}
