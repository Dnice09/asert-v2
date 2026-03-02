package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.user.entity.User;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorUserDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;

    public AssessorUserDto(User entity) {
        entity.toDao(this);
    }
}
