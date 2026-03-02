package tz.go.mnrt.asert.modules.selfassessment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentDraftRequestDto {

    private Long id;
    private UUID uuid;

    @NotNull(message = "Form UUID is required")
    private UUID formUuid;

    private UUID hotelUuid;

    // This field will be automatically set from the logged-in user
    private String submittedBy;

    @NotNull(message = "Current section index is required")
    private Integer currentSectionIndex;

    private String formData;

    private Double completionPercentage;

    private Integer totalSections;
}
