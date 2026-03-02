package tz.go.mnrt.asert.modules.bednight.reservation.dtos;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ReservationResponseDto {
  private Long id;
  private UUID uuid;

  @NotNull(message = "Created Date is required")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDateTime createdDate;

  private Integer hotelId;

  private Integer roomTypeId;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime checkInDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime checkOutDate;

  private String roomNumber;

  private String reservationCode;

  private Boolean isCheckedOut;

  private HotelResponseDto hotel = new HotelResponseDto();

  private RoomTypeResponseDto roomType = new RoomTypeResponseDto();

  private String hotelName;

  private String roomTypeName;

  private Set<ReservationVisitorDto> visitors = new HashSet<>();

  public ReservationResponseDto(Reservation reservation) {
    BeanUtils.copyProperties(reservation, this);
    if(reservation.getHotel() != null){
        BeanUtils.copyProperties(reservation.getHotel(), this.hotel);
        this.hotelName = reservation.getHotel().getName();
    }

    if(reservation.getRoomType() != null){
      BeanUtils.copyProperties(reservation.getRoomType(), this.roomType);
      this.roomTypeName = reservation.getRoomType().getName();
    }

    if(reservation.getReservationVisitors() != null && !reservation.getReservationVisitors().isEmpty()){
        setVisitors(reservation.getReservationVisitors().stream().map(ReservationVisitorDto::new).collect(Collectors.toSet()));
    }
  }
}
