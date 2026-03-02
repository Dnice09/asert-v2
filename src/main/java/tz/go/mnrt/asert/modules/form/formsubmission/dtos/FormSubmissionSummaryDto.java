package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormSubmissionSummaryDto {
    // Basic submission identification
    private UUID uuid;
    private Long id;

    // Submission metadata
    private String submittedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    // Scores
    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;

    // Form information
    private UUID formUuid;
    private String formName;

    // Hotel information
    private UUID hotelUuid;
    private String hotelName;
    private String propertyTypeName;

    // Status information (if applicable)
    private String status;
}
