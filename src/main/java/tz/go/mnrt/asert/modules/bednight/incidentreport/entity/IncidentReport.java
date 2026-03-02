package tz.go.mnrt.asert.modules.bednight.incidentreport.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.setup.incidentreporttype.entity.IncidentReportType;
import tz.go.mnrt.asert.modules.user.entity.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incident_reports")
public class IncidentReport extends BaseModel {

    @Column(name = "incident_type_id", nullable = false)
    private Integer incidentTypeId;

    @Column(name = "visitor_id", nullable = false)
    private Integer visitorId;

    @Column(name = "hotel_id", nullable = false)
    private Integer hotelId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "reservation_id", nullable = false)
    private Integer reservationId;

    @Column(name = "incident_date")
    private LocalDateTime incidentDate;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hotel_id", insertable = false, updatable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "incident_type_id", insertable = false, updatable = false)
    private IncidentReportType   incidentType;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "visitor_id", insertable = false, updatable = false)
    private Visitor visitor;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "reservation_id", insertable = false, updatable = false)
    private Reservation   reservation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User   user;
}
