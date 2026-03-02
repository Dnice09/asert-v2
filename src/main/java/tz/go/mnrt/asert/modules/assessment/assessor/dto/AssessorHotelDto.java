package tz.go.mnrt.asert.modules.assessment.assessor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessorHotelDto {

    private List<Long> hotels = new ArrayList<>();
    private LocalDate dateAssigned;
    private LocalDate deadline;
    private Boolean selfAssessmentRequest;
}
