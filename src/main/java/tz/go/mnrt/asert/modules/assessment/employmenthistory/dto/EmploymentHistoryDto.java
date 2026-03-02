package tz.go.mnrt.asert.modules.assessment.employmenthistory.dto;

import lombok.*;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.entity.EmploymentHistory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EmploymentHistoryDto implements Serializable {
    private Long id;
    private UUID uuid;
    private String positionHeld;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String company;

    public EmploymentHistoryDto(EmploymentHistory entity) {
        entity.toDao(this);
    }
}
