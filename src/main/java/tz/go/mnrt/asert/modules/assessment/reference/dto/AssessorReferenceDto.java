package tz.go.mnrt.asert.modules.assessment.reference.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorReferenceDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String name;
    private String phone;
    private String email;
    private String title;
    private String relationship;

    public AssessorReferenceDto(AssessorReference entity) {
        entity.toDao(this);
    }
}
