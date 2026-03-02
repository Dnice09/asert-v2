package tz.go.mnrt.asert.modules.selfassessment.dtos;

import java.util.List;
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
public class SelfAssessmentRequestDto {

    @NotNull(message = "Hotel UUID is required")
    private UUID hotelUuid;

    @NotNull(message = "Form UUID is required")
    private UUID formUuid;

    private List<FieldResponseDto> responses;
}
