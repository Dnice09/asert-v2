package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;

/**
 * DTO representing a hotel group with all its associated variance logs.
 * Used for hotel-level pagination where each "page" contains N hotels,
 * and each hotel contains ALL its variances (maintaining the hierarchy).
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelVarianceGroupDto {

    /**
     * The hotel information
     */
    private HotelResponseDto hotel;

    /**
     * All variance logs for this hotel (complete list, not paginated)
     */
    private List<AssessmentVarianceLogResponseDto> variances;

    /**
     * Total number of variances for this hotel (convenience field)
     */
    private int varianceCount;

    /**
     * Number of unresolved variances (OPEN or PARTIALLY_RESOLVED)
     */
    private int unresolvedCount;
}
