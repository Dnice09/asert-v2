package tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.EssentialItem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EssentialItemResponseDto {

    private Long id;
    private UUID uuid;
    private Integer itemNo;
    private String description;
    private String complianceRequirement;
    private String compliance;
    private String notes;
    private Boolean evidenceProvided;
    private String evidenceType;
    private String statusDisplayName;
    private List<EssentialItemEvidenceResponseDto> evidence;

    public EssentialItemResponseDto(EssentialItem entity) {
        BeanUtils.copyProperties(entity, this);
        
        this.statusDisplayName = entity.getStatusDisplayName();
        
        if (entity.getEvidence() != null) {
            this.evidence = entity.getEvidence().stream()
                    .map(EssentialItemEvidenceResponseDto::new)
                    .collect(Collectors.toList());
        }
    }
}