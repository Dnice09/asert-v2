package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfieldoption.dtos.FormFieldOptionRequestDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormFieldRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Field label is required")
    private String label;

    @NotBlank(message = "Field type is required")
    private String fieldType;

    private boolean required;
    private String placeholder;
    private String helpText;

    private Integer score;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private String validationRules;

    private List<FormFieldOptionRequestDto> options;
    
    private List<ConditionalLogicDto> conditionalLogic;
}
