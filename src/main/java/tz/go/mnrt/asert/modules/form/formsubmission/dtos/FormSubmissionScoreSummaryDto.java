package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionScoreSummaryDto {
    private UUID submissionUuid;
    private String formName;
    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;
    private List<CategoryScoreDto> categoryScores;
}
