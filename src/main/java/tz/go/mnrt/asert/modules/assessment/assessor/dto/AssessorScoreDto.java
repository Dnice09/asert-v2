package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssessorScoreDto implements Serializable {
    private Long id;
    private Double score;
    private String title;
    private String firstName;
    private String lastName;
    private String email;
}
