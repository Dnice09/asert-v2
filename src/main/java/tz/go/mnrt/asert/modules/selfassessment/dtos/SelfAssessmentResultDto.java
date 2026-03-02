package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoreDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelfAssessmentResultDto {

    private UUID uuid;
    private String hotelName;
    private String formName;
    private String submittedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;

    private Double totalScore;
    private Double maxPossibleScore;
    private Double percentage;
    private String estimatedRating;

    private List<SectionScoreDto> sectionScores;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
