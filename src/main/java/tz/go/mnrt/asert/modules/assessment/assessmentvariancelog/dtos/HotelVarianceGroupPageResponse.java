package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Custom pagination response for hotel-grouped variance logs.
 * Unlike standard Spring Page, this paginates at the HOTEL level,
 * not the individual variance level.
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelVarianceGroupPageResponse {

    /**
     * The hotel groups for the current page
     */
    private List<HotelVarianceGroupDto> content;

    /**
     * Current page number (0-indexed)
     */
    private int page;

    /**
     * Number of hotels per page
     */
    private int size;

    /**
     * Total number of hotels across all pages
     */
    private long totalElements;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the first page
     */
    private boolean first;

    /**
     * Whether this is the last page
     */
    private boolean last;

    /**
     * Total number of variances across ALL hotels (not just this page)
     */
    private long totalVariances;
}
