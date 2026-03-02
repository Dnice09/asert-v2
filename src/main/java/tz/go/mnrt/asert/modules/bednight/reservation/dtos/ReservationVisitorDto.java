package tz.go.mnrt.asert.modules.bednight.reservation.dtos;

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
import tz.go.mnrt.asert.modules.bednight.reservation.entity.ReservationVisitor;
import tz.go.mnrt.asert.modules.bednight.visitor.dtos.VisitorResponseDto;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ReservationVisitorDto {
    private Long id;
    private UUID uuid;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    private Integer reservationId;
    private Integer VisitorId;
    private ReservationResponseDto reservation = new ReservationResponseDto();
    private VisitorResponseDto visitor = new VisitorResponseDto();
    private String visitorName;
    private String visitorCountry;
    private String visitorGender;

    public ReservationVisitorDto(ReservationVisitor reservationVisitor) {
        BeanUtils.copyProperties(reservationVisitor, this);
        if(reservationVisitor.getReservation() != null){
            BeanUtils.copyProperties(reservationVisitor.getReservation(), this.reservation);
        }

        if(reservationVisitor.getVisitor() != null){
        BeanUtils.copyProperties(reservationVisitor.getVisitor(), this.visitor);
        this.visitorName = reservationVisitor.getVisitor().getFullName();
        this.visitorCountry = reservationVisitor.getVisitor().getCountry().getName();
        this.visitorGender = reservationVisitor.getVisitor().getGender();
        }
    }
}
