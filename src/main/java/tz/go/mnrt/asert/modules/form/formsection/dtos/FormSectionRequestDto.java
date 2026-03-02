package tz.go.mnrt.asert.modules.form.formsection.dtos;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.form.formfield.dtos.FormFieldRequestDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FormSectionRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Section title is required")
    private String title;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private List<FormFieldRequestDto> fields;

    private Double weight;
    private Integer maxScore;

    // For subsections
    private UUID parentSectionUuid;
    private Integer sectionLevel;
    private List<FormSectionRequestDto> subsections;
}
