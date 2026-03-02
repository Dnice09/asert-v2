package tz.go.mnrt.asert.modules.form.formfieldoption.dtos;

import java.util.UUID;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfieldoption.entity.FormFieldOption;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldOptionResponseDto {
    private Long id;
    private UUID uuid;
    private String label;
    private String value;
    private Integer score;
    private Integer orderIndex;

    /**
     * Constructor that creates a DTO from a FormFieldOption entity
     *
     * @param entity The FormFieldOption entity to convert
     */
    public FormFieldOptionResponseDto(FormFieldOption entity) {
        BeanUtils.copyProperties(entity, this);
    }
}
