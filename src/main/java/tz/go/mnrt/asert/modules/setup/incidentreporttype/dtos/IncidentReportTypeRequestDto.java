package tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos;

import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.entity.IncidentReportType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncidentReportTypeRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "name is required")
  private String name;

  @NotNull(message = "severity is required")
  private Integer severity;

  public IncidentReportTypeRequestDto(IncidentReportType entity) {
    entity.toDao(this);
  }
}
