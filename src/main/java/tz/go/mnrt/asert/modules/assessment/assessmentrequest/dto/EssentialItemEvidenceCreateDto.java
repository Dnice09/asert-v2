package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItemEvidenceCreateDto {

    @Size(max = 1000, message = "Evidence description must not exceed 1000 characters")
    private String evidenceDescription;

    @Pattern(regexp = "^(document|certificate|photo|video|)$", message = "Evidence type must be 'document', 'certificate', 'photo', 'video', or empty")
    private String evidenceType;

    private UUID fileUploadId;
}