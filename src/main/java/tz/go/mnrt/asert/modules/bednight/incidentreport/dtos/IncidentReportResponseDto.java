package tz.go.mnrt.asert.modules.bednight.incidentreport.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.bednight.incidentreport.entity.IncidentReport;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.dtos.IncidentReportTypeResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncidentReportResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private Integer incidentTypeId;

  private Integer visitorId;

  private Integer hotelId;

  private Integer userId;

  private Integer reservationId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime incidentDate;

  private String comment;

  private IncidentReportTypeResponseDto incidentType = new IncidentReportTypeResponseDto();

  private String incidentTypeName;

  public IncidentReportResponseDto(IncidentReport entity) {
   BeanUtils.copyProperties(entity, this);
   if(entity.getIncidentType() != null){
    BeanUtils.copyProperties(entity.getIncidentType(), this.getIncidentType());
    this.incidentTypeName = entity.getIncidentType().getName();
   }
  }
}
