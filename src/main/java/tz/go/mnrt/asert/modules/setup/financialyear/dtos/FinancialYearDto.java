package tz.go.mnrt.asert.modules.setup.financialyear.dtos;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.beans.BeanUtils;

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
public class FinancialYearDto {
    private Long id;
    private UUID uuid;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Start Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Boolean isCurrent = false;

    public FinancialYearDto(FinancialYear financialYear) {
        BeanUtils.copyProperties(financialYear, this);
    }
}
