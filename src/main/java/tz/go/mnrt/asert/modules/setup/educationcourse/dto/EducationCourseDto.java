package tz.go.mnrt.asert.modules.setup.educationcourse.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.setup.educationcourse.entity.EducationCourse;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EducationCourseDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;
    private Long educationLevelId;
    private String educationLevelName;

    public EducationCourseDto(EducationCourse entity) {
        entity.toDao(this);
        this.educationLevelId = entity.getEducationLevel().getId();
        this.educationLevelName = entity.getEducationLevel().getName();
    }
}
