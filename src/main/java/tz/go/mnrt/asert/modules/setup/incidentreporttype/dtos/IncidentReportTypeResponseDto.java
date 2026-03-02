package tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.entity.IncidentReportType;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncidentReportTypeResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private String name;

  private Integer severity;

  private Long value;

  public IncidentReportTypeResponseDto(IncidentReportType entity) {
      BeanUtils.copyProperties(entity, this);
      this.value = entity.getId();
  }
}
