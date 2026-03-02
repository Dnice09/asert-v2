package tz.go.mnrt.asert.modules.bednight.reservation.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation extends BaseModel {

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "room_type_id", nullable = false)
    private Integer roomTypeId;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date")
    private LocalDateTime checkOutDate;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "reservation_code")
    private String reservationCode;

    @Column(name = "is_checked_out", nullable = false)
    private Boolean isCheckedOut = false;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_type_id", insertable = false, updatable = false)
    private RoomType   roomType;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationVisitor> reservationVisitors = new HashSet<>();

    public void addVisitor(Visitor visitor) {
        ReservationVisitor rv = new ReservationVisitor();
        rv.setUuid(UUID.randomUUID());
        rv.setReservation(this);
        rv.setVisitor(visitor);
        this.reservationVisitors.add(rv);
        visitor.getReservationVisitors().add(rv);
    }

    public void removeVisitor(Visitor visitor) {
        reservationVisitors.removeIf(rv -> rv.getVisitor().equals(visitor));
        visitor.getReservationVisitors().removeIf(rv -> rv.getReservation().equals(this));
    }

}
