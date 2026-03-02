package tz.go.mnrt.asert.modules.form.form.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formsection.dtos.FormSectionResponseDto;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionScoringDto;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormResponseDto {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private Set<PropertyType> propertyTypes;

    @NotNull(message = "Created Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String createdBy;
    private String updatedBy;

    private Set<FormSectionResponseDto> sections;

    // Section scoring information
    private List<SectionScoringDto> sectionScores;
    private Integer totalMaxScore;  // Total effective max score (used in submissions)
    private Integer totalDefinedMaxScore; // Total from sections with defined maxScore
    private Integer totalCalculatedMaxScore; // Total from sections calculated from fields
    private boolean hasCalculatedSections;
    private boolean hasSectionMismatches; // true if any section has mismatch

    public FormResponseDto(Form entity) {
        BeanUtils.copyProperties(entity, this, "sections");
        if (entity.getSections() != null) {
            this.sections = entity.getSections().stream()
                    .map(FormSectionResponseDto::new)
                    .collect(Collectors.toSet());

            // Calculate section scores for all sections (including subsections)
            this.sectionScores = entity.getSections().stream()
                    .filter(s -> !s.isDeleted())
                    .map(this::calculateSectionScoring)
                    .collect(Collectors.toList());

            // Calculate totals only from top-level sections (no parent)
            List<tz.go.mnrt.asert.modules.form.formsection.entity.FormSection> topLevelSections =
                    entity.getSections().stream()
                            .filter(s -> !s.isDeleted() && s.getParentSection() == null)
                            .collect(Collectors.toList());

            int totalDefined = 0;
            int totalCalculated = 0;
            int totalEffective = 0;

            for (tz.go.mnrt.asert.modules.form.formsection.entity.FormSection section : topLevelSections) {
                if (section.getMaxScore() != null) {
                    totalDefined += section.getMaxScore();
                    totalEffective += section.getMaxScore();
                } else {
                    List<FormField> sectionFields = getAllFields(section);
                    Integer calculated = calculateMaxScoreFromOptions(sectionFields);
                    int calcValue = calculated != null ? calculated : 0;
                    totalCalculated += calcValue;
                    totalEffective += calcValue;
                }
            }

            this.totalDefinedMaxScore = totalDefined;
            this.totalCalculatedMaxScore = totalCalculated;
            this.totalMaxScore = totalEffective;

            this.hasCalculatedSections = this.sectionScores.stream()
                    .anyMatch(dto -> dto.getMaxScore() == null && dto.isHasScoringOptions());

            this.hasSectionMismatches = this.sectionScores.stream()
                    .anyMatch(SectionScoringDto::isHasMismatch);
        }
    }

    private SectionScoringDto calculateSectionScoring(tz.go.mnrt.asert.modules.form.formsection.entity.FormSection section) {
        SectionScoringDto dto = new SectionScoringDto();
        dto.setSectionUuid(section.getUuid());
        dto.setSectionTitle(section.getTitle());
        dto.setMaxScore(section.getMaxScore());
        dto.setSectionLevel(section.getSectionLevel());
        if (section.getParentSection() != null) {
            dto.setParentSectionUuid(section.getParentSection().getUuid());
        }

        // Get all fields recursively including subsections
        List<FormField> allFields = getAllFields(section);

        // Calculate max score from options
        Integer calculatedMaxScore = calculateMaxScoreFromOptions(allFields);
        boolean hasScoringOptions = calculatedMaxScore != null && calculatedMaxScore > 0;

        dto.setCalculatedMaxScore(calculatedMaxScore);
        dto.setHasScoringOptions(hasScoringOptions);
        dto.setHasDefinedMaxScore(section.getMaxScore() != null);

        // Set effective max score (defined takes precedence over calculated)
        Integer effectiveMaxScore = section.getMaxScore() != null ? section.getMaxScore() : calculatedMaxScore;
        dto.setEffectiveMaxScore(effectiveMaxScore);

        // Check for mismatch between defined and calculated
        boolean hasMismatch = section.getMaxScore() != null &&
                              calculatedMaxScore != null &&
                              !section.getMaxScore().equals(calculatedMaxScore);
        dto.setHasMismatch(hasMismatch);

        // Set counts
        dto.setFieldCount(allFields.size());
        dto.setSubsectionCount(section.getSubsections() != null ?
                               (int) section.getSubsections().stream().filter(s -> !s.isDeleted()).count() : 0);

        return dto;
    }

    private List<FormField> getAllFields(tz.go.mnrt.asert.modules.form.formsection.entity.FormSection section) {
        List<FormField> fields = new java.util.ArrayList<>();
        if (section.getFields() != null) {
            fields.addAll(section.getFields().stream()
                    .filter(f -> !f.isDeleted())
                    .collect(Collectors.toList()));
        }
        if (section.getSubsections() != null) {
            for (tz.go.mnrt.asert.modules.form.formsection.entity.FormSection subsection : section.getSubsections()) {
                if (!subsection.isDeleted()) {
                    fields.addAll(getAllFields(subsection));
                }
            }
        }
        return fields;
    }

    private Integer calculateMaxScoreFromOptions(List<FormField> fields) {
        int totalScore = 0;

        for (FormField field : fields) {
            String fieldType = field.getFieldType();

            // Handle rating fields - each rating field contributes 5 points maximum
            if ("rating".equals(fieldType)) {
                totalScore += 5;
                continue;
            }

            // Handle checkbox fields - sum all option scores (multiple can be selected)
            if ("checkbox".equals(fieldType) && field.getOptions() != null) {
                int checkboxTotal = field.getOptions().stream()
                        .filter(option -> option.getScore() != null)
                        .mapToInt(option -> option.getScore())
                        .sum();
                totalScore += checkboxTotal;
                continue;
            }

            // Handle select/radio fields - find maximum score (only one can be selected)
            if (("select".equals(fieldType) || "radio".equals(fieldType)) && field.getOptions() != null) {
                int fieldMaxScore = field.getOptions().stream()
                        .filter(option -> option.getScore() != null)
                        .mapToInt(option -> option.getScore())
                        .max()
                        .orElse(0);
                totalScore += fieldMaxScore;
            }
        }

        return totalScore > 0 ? totalScore : null;
    }

    public FormResponseDto(UUID uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
    }
}

