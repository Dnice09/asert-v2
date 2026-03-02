package tz.go.mnrt.asert.modules.form.formfield.dtos;

import java.util.List;
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
public class ConditionalLogicDto {
    private UUID triggerFieldUuid;
    private String operator;
    private String value;
    private List<ConditionalActionDto> actions;
}
