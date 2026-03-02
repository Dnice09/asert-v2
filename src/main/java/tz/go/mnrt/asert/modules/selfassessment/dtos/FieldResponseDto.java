package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FieldResponseDto {
    private UUID fieldUuid;
    private String value;
}
