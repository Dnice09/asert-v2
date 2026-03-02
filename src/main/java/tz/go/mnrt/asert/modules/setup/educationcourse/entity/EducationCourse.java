package tz.go.mnrt.asert.modules.setup.educationcourse.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.setup.educationlevel.entity.EducationLevel;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "education_courses")
public class EducationCourse extends BaseModel {
    @Column(name = "name", nullable = false,unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "education_level_id", nullable = false, insertable = false, updatable = false)
    private EducationLevel educationLevel;

    @Column(name = "education_level_id", nullable = false)
    private Long educationLevelId;
}
