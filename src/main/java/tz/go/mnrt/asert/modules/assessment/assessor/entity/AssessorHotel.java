package tz.go.mnrt.asert.modules.assessment.assessor.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "assessor_hotels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssessorHotel extends BaseModel {

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    private Assessor assessor;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "data_collected", nullable = false)
    private Boolean dataCollected;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false, insertable = false, updatable = false)
    private Hotel hotel;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_assigned", nullable = false)
    private LocalDate dateAssigned;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AssessmentStatus status;

    @Column(name = "self_assessment_request", nullable = true)
    private Boolean selfAssessmentRequest;
}
