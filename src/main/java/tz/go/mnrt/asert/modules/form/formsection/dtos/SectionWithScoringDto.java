package tz.go.mnrt.asert.modules.form.formsection.dtos;

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
@NoArgsConstructor
@AllArgsConstructor
public class SectionWithScoringDto {
    private UUID uuid;
    private String title;
    private Double weight;
    private Integer maxScore;
    private Integer level;
    private UUID parentSectionUuid;
    private List<SectionWithScoringDto> subsections;
}
