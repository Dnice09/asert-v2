package tz.go.mnrt.asert.modules.form.formsubmission.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryScoreDto {
    private String categoryName;
    private Double score;
    private Double maxPossible;
    private Double percentage;
}
