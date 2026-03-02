package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.UUID;

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
public class FormFieldResponseValueDto {
    @NotNull(message = "Field UUID is required")
    private UUID fieldUuid;

    @NotNull(message = "Response value is required")
    private String value;

    private String comments;
}
