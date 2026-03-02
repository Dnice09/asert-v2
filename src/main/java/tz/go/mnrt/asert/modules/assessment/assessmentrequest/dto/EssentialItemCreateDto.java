package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItemCreateDto {

    @NotNull(message = "Item number is required")
    @Min(value = 1, message = "Item number must be greater than 0")
    @Max(value = 100, message = "Item number must not exceed 100")
    private Integer itemNo;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 1000, message = "Compliance requirement must not exceed 1000 characters")
    private String complianceRequirement;

    @Pattern(regexp = "^(compliant|non-compliant|)$", message = "Compliance must be 'compliant', 'non-compliant', or empty")
    private String compliance;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    private Boolean evidenceProvided = false;

    @Pattern(regexp = "^(document|certificate|photo|video|)$", message = "Evidence type must be 'document', 'certificate', 'photo', 'video', or empty")
    private String evidenceType;
}