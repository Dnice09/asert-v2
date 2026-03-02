package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;

@Getter
@Setter
@NoArgsConstructor
public class AssessorSubmissionDto {
    private UUID submissionUuid;
    private Long submissionId;
    private String assessorName;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submittedAt;

    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;
    private List<SectionScoreDto> sectionScores = new ArrayList<>();
}
