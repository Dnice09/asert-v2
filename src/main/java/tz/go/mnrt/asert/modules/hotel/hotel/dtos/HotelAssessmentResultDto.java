package tz.go.mnrt.asert.modules.hotel.hotel.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AggregatedSectionScoreDto;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorSubmissionDto;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@NoArgsConstructor
public class HotelAssessmentResultDto {
    private UUID hotelUuid;
    private String hotelName;
    private PropertyType hotelType;

    // Form information
    private UUID formUuid;
    private String formName;

    // Aggregate assessment details
    private Integer totalAssessments;
    private Integer uniqueAssessors;
    private Double aggregateScore;
    private Double aggregatePercentage;
    private String starRating;

    // Latest assessment timestamp

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAssessedAt;

    // Individual assessor submissions
    private List<AssessorSubmissionDto> assessorSubmissions = new ArrayList<>();

    // Section-based aggregate scores
    private List<AggregatedSectionScoreDto> sectionScores = new ArrayList<>();
}
