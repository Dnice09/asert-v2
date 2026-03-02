package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for returning submission data in draft-compatible format for variance resolution.
 * Matches the structure of FormDraftResponseDto so the frontend can load it the same way.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionAsDraftDto {

    private Long id;
    private UUID uuid;

    private UUID formUuid;
    private String formName;
    private UUID hotelUuid;
    private String hotelName;
    private String submittedBy;
    private Integer currentSectionIndex;

    // JSON string containing formValues and sectionScores (same as FormDraftResponseDto)
    private String formData;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastSavedAt;

    private Double completionPercentage;
    private Integer totalSections;
}
