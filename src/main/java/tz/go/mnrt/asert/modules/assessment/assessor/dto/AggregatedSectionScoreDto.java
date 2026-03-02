package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AggregatedSectionScoreDto {
    private UUID sectionUuid;
    private String sectionTitle;
    private Double averageScore;
    private Double maxPossibleScore;
    private Double averagePercentage;
    private List<AggregatedSectionScoreDto> subsectionScores = new ArrayList<>();
}
