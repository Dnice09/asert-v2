package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequestUpdateDto {

    private AssessmentRequestStatus status;

    @Size(max = 2000, message = "Processing notes must not exceed 2000 characters")
    private String processingNotes;
}