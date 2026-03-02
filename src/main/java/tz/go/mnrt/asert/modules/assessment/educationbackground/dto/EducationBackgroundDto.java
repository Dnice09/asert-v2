package tz.go.mnrt.asert.modules.assessment.educationbackground.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.assessment.educationbackground.entity.EducationBackground;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EducationBackgroundDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String institution;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Boolean graduated;
    private String course;
    private Long educationLevelId;
    private String educationLevelName;

    public EducationBackgroundDto(EducationBackground entity) {
        entity.toDao(this);
        this.educationLevelId = entity.getEducationLevelId();
        this.educationLevelName = entity.getEducationLevel().getName();
    }
}
