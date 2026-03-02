package tz.go.mnrt.asert.modules.assessment.preference.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorPreferenceDto implements Serializable {
    private Long id;
    private UUID uuid;
    private PropertyType preference;

    public AssessorPreferenceDto(AssessorPreference entity) {
        entity.toDao(this);
    }
}
