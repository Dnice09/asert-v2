package tz.go.mnrt.asert.modules.bednight.incidentreport.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.bednight.incidentreport.entity.IncidentReport;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncidentReportRequestDto {
    private Long id;
    private UUID uuid;

  @NotNull(message = "incident_type_id is required")
  private Integer incidentTypeId;

  @NotNull(message = "visitor_id is required")
  private Integer visitorId;

  @NotNull(message = "hotel_id is required")
  private Integer hotelId;

  private Integer userId;

  @NotNull(message = "reservation_id is required")
  private Integer reservationId;

  private LocalDateTime incidentDate;

    private String comment;

    public IncidentReportRequestDto(IncidentReport entity) {
        entity.toDao(this);
    }
}
