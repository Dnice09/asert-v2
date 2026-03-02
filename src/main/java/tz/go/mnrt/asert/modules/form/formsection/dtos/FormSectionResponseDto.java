package tz.go.mnrt.asert.modules.form.formsection.dtos;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldResponseDto;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormSectionResponseDto {
    private Long id;
    private UUID uuid;
    private String title;
    private Integer orderIndex;
    private Integer sectionLevel;
    private UUID parentSectionUuid;
    private Integer maxScore;
    private Double weight;
    private Set<FormFieldResponseDto> fields;
    private Set<FormSectionResponseDto> subsections;

    public FormSectionResponseDto(FormSection entity) {
        BeanUtils.copyProperties(entity, this, "fields", "subsections");
        this.sectionLevel = entity.getSectionLevel();
        if (entity.getParentSection() != null) {
            this.parentSectionUuid = entity.getParentSection().getUuid();
        }
        if (entity.getFields() != null) {
            this.fields = entity.getFields().stream()
                    .map(FormFieldResponseDto::new)
                    .collect(Collectors.toSet());
        }
        if (entity.getSubsections() != null) {
            this.subsections = entity.getSubsections().stream()
                    .map(FormSectionResponseDto::new)
                    .collect(Collectors.toSet());
        }
    }
}
