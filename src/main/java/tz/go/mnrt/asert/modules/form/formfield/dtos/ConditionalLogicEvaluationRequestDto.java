package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.Map;
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
public class ConditionalLogicEvaluationRequestDto {
    @NotNull(message = "Form UUID is required")
    private UUID formUuid;

    @NotNull(message = "Field values are required")
    private Map<UUID, String> fieldValues;
}
