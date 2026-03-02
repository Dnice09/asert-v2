package tz.go.mnrt.asert.modules.assessment.educationbackground.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.educationlevel.entity.EducationLevel;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assessor_education_history")
public class EducationBackground extends BaseModel {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "institution", nullable = false)
    private String institution;

    @Column(name = "course", nullable = false)
    private String course;

    @Column(name = "graduated", nullable = false)
    private Boolean graduated;

    @ManyToOne
    @JoinColumn(name = "education_level_id", nullable = false, insertable = false, updatable = false)
    private EducationLevel educationLevel;

    @Column(name = "education_level_id", nullable = false)
    private Long educationLevelId;

    @ManyToOne
    @JoinColumn(name = "assessor_id", nullable = false, insertable = false, updatable = false)
    private Assessor assessor;

    @Column(name = "assessor_id", nullable = false)
    private Long assessorId;
}
