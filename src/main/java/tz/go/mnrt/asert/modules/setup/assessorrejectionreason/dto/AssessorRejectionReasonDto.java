package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity.AssessorRejectionReason;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorRejectionReasonDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String code;
    private String reason;
    private String name;

    public AssessorRejectionReasonDto(AssessorRejectionReason entity) {
        entity.toDao(this);
        this.name = entity.getReason();
    }
}
