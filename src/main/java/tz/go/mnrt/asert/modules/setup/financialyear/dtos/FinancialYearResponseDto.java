package tz.go.mnrt.asert.modules.setup.financialyear.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.*;
import tz.go.mnrt.asert.modules.setup.financialyear.entity.FinancialYear;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FinancialYearResponseDto {
  private Long id;
  private UUID uuid;


  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  public FinancialYearResponseDto(FinancialYear entity) {
    entity.toDao(this);
  }
}
