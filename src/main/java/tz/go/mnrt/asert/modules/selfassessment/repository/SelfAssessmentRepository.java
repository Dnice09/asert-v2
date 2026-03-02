package tz.go.mnrt.asert.modules.selfassessment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessment;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentStatus;

@Repository
public interface SelfAssessmentRepository extends JpaRepository<SelfAssessment, Long> {

    Optional<SelfAssessment> findByUuidAndIsDeletedFalse(UUID uuid);

    // Optimized query 1: Fetch self-assessment with hotel and form only
    @Query("SELECT DISTINCT sa FROM SelfAssessment sa " +
           "LEFT JOIN FETCH sa.hotel " +
           "LEFT JOIN FETCH sa.form " +
           "WHERE sa.uuid = :uuid AND sa.isDeleted = false")
    Optional<SelfAssessment> findByUuidWithHotelAndForm(@Param("uuid") UUID uuid);

    // Optimized query 2: Fetch section scores separately
    @Query("SELECT DISTINCT sa FROM SelfAssessment sa " +
           "LEFT JOIN FETCH sa.sectionScores " +
           "WHERE sa.uuid = :uuid AND sa.isDeleted = false")
    Optional<SelfAssessment> findByUuidWithSectionScores(@Param("uuid") UUID uuid);

    // Optimized query 3: Fetch field responses separately
    @Query("SELECT DISTINCT sa FROM SelfAssessment sa " +
           "LEFT JOIN FETCH sa.fieldResponses " +
           "WHERE sa.uuid = :uuid AND sa.isDeleted = false")
    Optional<SelfAssessment> findByUuidWithFieldResponses(@Param("uuid") UUID uuid);

    List<SelfAssessment> findByHotelIdAndIsDeletedFalseOrderByCreatedAtDesc(Long hotelId);

    List<SelfAssessment> findByHotelIdAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(
            Long hotelId,
            SelfAssessmentStatus status);

    @Query("SELECT sa FROM SelfAssessment sa " +
           "WHERE sa.hotel.uuid = :hotelUuid " +
           "AND sa.status = :status " +
           "AND sa.isDeleted = false " +
           "ORDER BY sa.createdAt DESC")
    List<SelfAssessment> findByHotelUuidAndStatus(
            @Param("hotelUuid") UUID hotelUuid,
            @Param("status") SelfAssessmentStatus status);

    @Query("SELECT sa FROM SelfAssessment sa " +
           "WHERE sa.hotel.uuid = :hotelUuid " +
           "AND sa.isDeleted = false " +
           "ORDER BY sa.submittedAt DESC")
    List<SelfAssessment> findByHotelUuidOrderBySubmittedAtDesc(@Param("hotelUuid") UUID hotelUuid);

    @Query("SELECT sa FROM SelfAssessment sa " +
           "WHERE sa.hotel.uuid = :hotelUuid " +
           "AND sa.status = 'COMPLETED' " +
           "AND sa.isDeleted = false " +
           "ORDER BY sa.submittedAt DESC")
    Optional<SelfAssessment> findLatestCompletedByHotelUuid(@Param("hotelUuid") UUID hotelUuid);

    long countByHotelIdAndIsDeletedFalse(Long hotelId);

    long countByHotelIdAndStatusAndIsDeletedFalse(Long hotelId, SelfAssessmentStatus status);
}
