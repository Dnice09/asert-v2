package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldResponseValueDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionRequestDto {
    private Long id;
    private UUID uuid;
    private UUID hotelUuid;

    @NotNull(message = "Form UUID is required")
    private UUID formUuid;

    private String submittedBy;

    private List<FormFieldResponseValueDto> responses;

    private List<SectionScoreDto> sectionScores;

    private Double totalScore;

    private Double maxPossibleScore;

    private Double percentage;

    // Flag to allow re-submission when resolving variances
    @JsonProperty("isVarianceResolution")
    private Boolean isVarianceResolution = false;

    // Historical variance status - whether there were unresolved variances at submission time
    private Boolean hasUnresolvedVariances;
}
