package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.Map;
import java.util.UUID;

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
public class ConditionalLogicEvaluationResponseDto {
    private Map<UUID, Boolean> fieldVisibility;
    private Map<UUID, Boolean> fieldRequiredStatus;
    private Map<UUID, Boolean> sectionVisibility;
}
