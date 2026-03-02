package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AssessorApprovalDto implements Serializable {
    private Long id;
    private String verificationNotes;
}
