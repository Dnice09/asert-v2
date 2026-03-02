package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentComparisonDto {

    private String hotelName;
    private SelfAssessmentResultDto latestSelfAssessment;
    private OfficialAssessmentDto officialAssessment;
    private Double variance;
    private List<SectionComparisonDto> sectionComparisons;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OfficialAssessmentDto {
        private Double totalScore;
        private Double percentage;
        private String starRating;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SectionComparisonDto {
        private String sectionTitle;
        private Double selfAssessmentScore;
        private Double officialScore;
        private Double variance;
    }
}
