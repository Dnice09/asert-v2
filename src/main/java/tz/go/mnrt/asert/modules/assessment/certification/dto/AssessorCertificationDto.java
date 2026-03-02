package tz.go.mnrt.asert.modules.assessment.certification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorCertificationDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String title;
    private String issuer;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String description;

    public AssessorCertificationDto(AssessorCertification entity) {
        entity.toDao(this);
    }
}
