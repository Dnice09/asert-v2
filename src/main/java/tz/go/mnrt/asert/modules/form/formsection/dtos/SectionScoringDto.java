package tz.go.mnrt.asert.modules.form.formsection.dtos;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SectionScoringDto {
    private UUID sectionUuid;
    private String sectionTitle;
    private Integer maxScore; // fixed max score if set
    private Integer calculatedMaxScore; // max score calculated from options
    private Integer effectiveMaxScore; // the actual score used (maxScore if defined, else calculatedMaxScore)
    private boolean hasScoringOptions; // true if section has fields with scoring options
    private boolean hasDefinedMaxScore; // true if maxScore is explicitly set
    private boolean hasMismatch; // true if maxScore != calculatedMaxScore (when both exist)
    private Integer sectionLevel;
    private UUID parentSectionUuid;
    private int fieldCount; // number of fields in this section (including subsections)
    private int subsectionCount; // number of direct subsections
}