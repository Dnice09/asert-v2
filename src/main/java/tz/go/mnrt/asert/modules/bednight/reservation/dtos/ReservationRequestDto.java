package tz.go.mnrt.asert.modules.bednight.reservation.dtos;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "hotel_id is required")
  private Integer hotelId;

  @NotNull(message = "room_type_id is required")
  private Integer roomTypeId;

  private LocalDateTime checkInDate;

  private LocalDateTime checkOutDate;

  private String roomNumber;

  private String reservationCode;

  private Boolean isCheckedOut = false;

  private List<Long> visitorIds;

  public ReservationRequestDto(Reservation entity) {
    entity.toDao(this);
  }
}
