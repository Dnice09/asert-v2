package tz.go.mnrt.asert.modules.form.form.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.form.formsection.dtos.SectionWithScoringDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormWithScoringDto extends FormResponseDto {
    private List<SectionWithScoringDto> scoringSections;
}
