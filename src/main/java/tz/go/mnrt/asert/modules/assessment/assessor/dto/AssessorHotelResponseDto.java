package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessmentStatus;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorHotelResponseDto implements Serializable {
    private Long id;
    private UUID uuid;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateAssigned;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private AssessmentStatus status;
    private Boolean dataCollected;
    private Boolean selfAssessmentRequest;

    private Long hotelId;
    private UUID hotelUuid;
    private String hotelName;
    private String hotelEmail;
    private String hotelMobile;
    private String hotelCompany;
    private String assessorName;
    private String assessorPhone;
    private String assessorEmail;

    public AssessorHotelResponseDto(AssessorHotel entity) {
        entity.toDao(this);
        this.selfAssessmentRequest = entity.getSelfAssessmentRequest() != null ? entity.getSelfAssessmentRequest() : false;
        if (entity.getHotelId() != null) {
            this.hotelUuid = entity.getHotel().getUuid();
            this.hotelId = entity.getHotelId();
            this.hotelName = entity.getHotel().getName();
            this.hotelEmail = entity.getHotel().getEmail();
            this.hotelMobile = entity.getHotel().getPhone();
            this.hotelCompany = entity.getHotel().getCompany().getName();
        }
        if (entity.getAssessorId() != null) {
            this.assessorName = entity.getAssessor().getFirstName() + " " + entity.getAssessor().getLastName();
            this.assessorPhone = entity.getAssessor().getPhone();
            this.assessorEmail = entity.getAssessor().getEmail();
        }
    }
}
