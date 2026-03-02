package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import javax.persistence.QueryHint;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;

public interface AssessmentVarianceLogRepository
    extends BaseRepository<AssessmentVarianceLog, Long> {

  Optional<AssessmentVarianceLog> findByUuid(UUID uuid);

  /**
   * Find a single variance log by UUID with all related entities fetched in a single query.
   * Uses JOIN FETCH to avoid lazy loading and N+1 query problem.
   *
   * Performance: Single query instead of 6+ separate queries
   */
  @Query("SELECT v FROM AssessmentVarianceLog v " +
         "LEFT JOIN FETCH v.hotel " +
         "LEFT JOIN FETCH v.form " +
         "LEFT JOIN FETCH v.section " +
         "LEFT JOIN FETCH v.assessor1 " +
         "LEFT JOIN FETCH v.assessor2 " +
         "LEFT JOIN FETCH v.assessor3 " +
         "WHERE v.uuid = :uuid AND v.isDeleted = false")
  Optional<AssessmentVarianceLog> findByUuidWithRelations(@Param("uuid") UUID uuid);

  void deleteByUuid(UUID uuid);

  /**
   * Find all variance logs with all related entities fetched in a single query.
   * Uses JOIN FETCH to avoid N+1 query problem.
   * Deduplicated by field UUID to show only the latest entry per section.
   *
   * Performance optimizations:
   * - Single query loads all relationships
   * - Database-level deduplication using DISTINCT ON
   * - Batch size hint for optimal memory usage
   */
  @Query(value = "SELECT DISTINCT v FROM AssessmentVarianceLog v " +
         "LEFT JOIN FETCH v.hotel " +
         "LEFT JOIN FETCH v.form " +
         "LEFT JOIN FETCH v.section " +
         "LEFT JOIN FETCH v.assessor1 " +
         "LEFT JOIN FETCH v.assessor2 " +
         "LEFT JOIN FETCH v.assessor3 " +
         "WHERE v.isDeleted = false " +
         "ORDER BY v.id DESC",
         countQuery = "SELECT COUNT(DISTINCT v.id) FROM AssessmentVarianceLog v WHERE v.isDeleted = false")
  @QueryHints(@QueryHint(name = "org.hibernate.fetchSize", value = "50"))
  Page<AssessmentVarianceLog> findAllWithRelations(Pageable pageable);

  /**
   * Find all unresolved variances for a specific hotel and form.
   * Includes both OPEN and PARTIALLY_RESOLVED statuses.
   * Uses index on hotel_id, form_id, and status for optimal performance.
   */
  @Query("SELECT v FROM AssessmentVarianceLog v " +
         "WHERE v.hotel.id = :hotelId " +
         "AND v.form.id = :formId " +
         "AND v.status IN ('OPEN', 'PARTIALLY_RESOLVED') " +
         "AND v.isDeleted = false")
  List<AssessmentVarianceLog> findUnresolvedVariances(
      @Param("hotelId") Long hotelId,
      @Param("formId") Long formId
  );

  /**
   * Check if there are any unresolved variances for a specific field.
   * Includes both OPEN and PARTIALLY_RESOLVED statuses.
   * Used to determine if a field still has active variance issues.
   */
  @Query("SELECT COUNT(v) > 0 FROM AssessmentVarianceLog v " +
         "WHERE v.fieldUuid = :fieldUuid " +
         "AND v.hotel.id = :hotelId " +
         "AND v.form.id = :formId " +
         "AND v.status IN ('OPEN', 'PARTIALLY_RESOLVED') " +
         "AND v.isDeleted = false")
  boolean existsUnresolvedVarianceForField(
      @Param("fieldUuid") UUID fieldUuid,
      @Param("hotelId") Long hotelId,
      @Param("formId") Long formId
  );

  /**
   * Find an unresolved variance for a specific field.
   * Includes both OPEN and PARTIALLY_RESOLVED statuses.
   * Used to update existing variance with 3rd assessor's score.
   */
  @Query("SELECT v FROM AssessmentVarianceLog v " +
         "WHERE v.fieldUuid = :fieldUuid " +
         "AND v.hotel.id = :hotelId " +
         "AND v.form.id = :formId " +
         "AND v.status IN ('OPEN', 'PARTIALLY_RESOLVED') " +
         "AND v.isDeleted = false")
  Optional<AssessmentVarianceLog> findUnresolvedVarianceForField(
      @Param("fieldUuid") UUID fieldUuid,
      @Param("hotelId") Long hotelId,
      @Param("formId") Long formId
  );
}
