package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfield.entity.FormField;
import tz.go.mnrt.asert.modules.form.formfieldoption.dtos.FormFieldOptionResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldResponseDto {
    private Long id;
    private UUID uuid;
    private String label;
    private String fieldType;
    private boolean required;
    private String placeholder;
    private String helpText;
    private Integer orderIndex;
    private String validationRules;
    private String conditionalLogic;
    private Set<FormFieldOptionResponseDto> options;

    /**
     * Constructor that creates a DTO from a FormField entity
     *
     * @param entity The FormField entity to convert
     */
    public FormFieldResponseDto(FormField entity) {
        // Copy all matching properties from entity to this DTO
        BeanUtils.copyProperties(entity, this, "options");

        if (entity.getOptions() != null) {
            this.options = entity.getOptions().stream()
                    .map(FormFieldOptionResponseDto::new)
                    .collect(Collectors.toSet());
        }
    }
}
