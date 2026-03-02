package tz.go.mnrt.asert.modules.form.formsection.dtos;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SectionScoreDto {
    private Long sectionId;
    private UUID sectionUuid;
    private String sectionTitle;
    private Double score;
    private Double maxPossible;
    private Double percentage;
    private List<SectionScoreDto> subsectionScores;
}
