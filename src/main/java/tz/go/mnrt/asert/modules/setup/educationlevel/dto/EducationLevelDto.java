package tz.go.mnrt.asert.modules.setup.educationlevel.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EducationLevelDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;

    public EducationLevelDto(String name) {
        this.name = name;
    }
}
