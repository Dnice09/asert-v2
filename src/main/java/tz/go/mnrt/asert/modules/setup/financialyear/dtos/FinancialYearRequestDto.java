package tz.go.mnrt.asert.modules.setup.financialyear.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.setup.financialyear.entity.FinancialYear;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FinancialYearRequestDto {
    private Long id;
    private UUID uuid;

    public FinancialYearRequestDto(FinancialYear entity) {
        entity.toDao(this);
    }
}
