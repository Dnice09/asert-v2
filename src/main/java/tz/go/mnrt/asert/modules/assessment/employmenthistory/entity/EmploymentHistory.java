package tz.go.mnrt.asert.modules.assessment.employmenthistory.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_employment_history")
public class EmploymentHistory extends BaseModel {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "position_held", nullable = false)
    private String positionHeld;

    @Column(name = "company", nullable = false)
    private String company;

    @ManyToOne
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    private Assessor assessor;

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;
}
