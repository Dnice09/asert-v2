package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequestCreateDto {

    @NotNull(message = "Hotel UUID is required")
    private UUID hotelUuid;

    @Valid
    @NotNull(message = "Facility info is required")
    private FacilityInfoDto facilityInfo;

    @Size(max = 2000, message = "Additional comments must not exceed 2000 characters")
    private String additionalComments;

    @NotNull(message = "Terms acceptance is required")
    private Boolean termsAccepted;

    @Valid
    @NotNull(message = "Essential items are required")
    @Size(min = 1, message = "At least one essential item is required")
    private List<EssentialItemCreateDto> essentialItems;

    @Valid
    private List<UploadedDocumentDto> uploadedDocuments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime submittedAt;

    @DecimalMin(value = "0.0", message = "Completion percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Completion percentage cannot exceed 100")
    private Double completionPercentage;
}