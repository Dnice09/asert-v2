package tz.go.mnrt.asert.modules.form.formfield.dtos;

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
public class ConditionalActionDto {
    private String actionType;
    private UUID targetFieldUuid;
    private UUID targetSectionUuid;
    private String actionValue;
}
