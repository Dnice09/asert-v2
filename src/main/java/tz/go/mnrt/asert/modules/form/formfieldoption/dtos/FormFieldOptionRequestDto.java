package tz.go.mnrt.asert.modules.form.formfieldoption.dtos;

import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class FormFieldOptionRequestDto {
    private Long id;
    private UUID uuid;

    @NotBlank(message = "Option label is required")
    private String label;

    private Integer score;

    @NotBlank(message = "Option value is required")
    private String value;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;
}
