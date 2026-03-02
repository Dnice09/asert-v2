package tz.go.mnrt.asert.modules.form.formsubmission.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.form.form.entity.Form;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmission.enums.SubmissionStatus;

public interface FormSubmissionRepository
        extends BaseRepository<FormSubmission, Long> {

    Optional<FormSubmission> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("SELECT DISTINCT fs.submittedBy FROM FormSubmission fs WHERE fs.hotel.id = :hotelId AND fs.isDeleted = false")
    List<String> findDistinctAssessorsByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT fs FROM FormSubmission fs WHERE fs.hotel.id = :hotelId AND " +
            "(:assessor IS NULL OR fs.submittedBy = :assessor) AND fs.isDeleted = false " +
            "ORDER BY fs.submittedAt DESC")
    Page<FormSubmission> findByHotelIdAndAssessor(
            @Param("hotelId") Long hotelId,
            @Param("assessor") String assessor,
            Pageable pageable);

    List<FormSubmission> findByHotelIdOrderBySubmittedAtDesc(Long hotelId);

    @Query("SELECT fs FROM FormSubmission fs WHERE fs.hotel.id = :hotelId AND fs.isDeleted = false " +
            "ORDER BY fs.submittedAt DESC")
    Page<FormSubmission> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN true ELSE false END FROM FormSubmission fs " +
            "WHERE fs.form.uuid = :formUuid AND fs.hotel.uuid = :hotelUuid AND fs.submittedBy = :submittedBy AND fs.isDeleted = false")
    boolean existsByFormUuidAndHotelUuidAndSubmittedBy(
            @Param("formUuid") UUID formUuid,
            @Param("hotelUuid") UUID hotelUuid,
            @Param("submittedBy") String submittedBy);

    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN true ELSE false END FROM FormSubmission fs " +
            "WHERE fs.form.uuid = :formUuid AND fs.hotel.uuid = :hotelUuid AND fs.assessor.id = :assessorId AND fs.isDeleted = false")
    boolean existsByFormUuidAndHotelUuidAndAssessorId(
            @Param("formUuid") UUID formUuid,
            @Param("hotelUuid") UUID hotelUuid,
            @Param("assessorId") Long assessorId);

    @Query("SELECT fs.assessor FROM FormSubmission fs WHERE fs.id = :id AND fs.isDeleted = false")
    Optional<Assessor> findAssessorById(@Param("id") Long id);

    /**
     * Fetch all APPROVED submissions for a hotel and form, eagerly loading section scores.
     * Optimized with JOIN FETCH to avoid N+1 queries.
     * Used for variance detection to get all APPROVED assessor submissions in one query.
     *
     * Only APPROVED submissions are used for variance detection.
     * DRAFT, SUBMITTED, and REJECTED submissions are not included in variance calculations.
     *
     * NOTE: We don't filter soft-deleted section scores in the query because
     * adding WHERE conditions on the fetched collection causes Hibernate to
     * return null collections instead of filtered collections. Instead, we
     * filter soft-deleted scores in the service layer after loading.
     *
     * NOTE: Using DISTINCT with multiple LEFT JOIN FETCH can cause Hibernate
     * to improperly initialize collections. We use DISTINCT in the result set
     * transformation instead.
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "LEFT JOIN FETCH fs.sectionScores " +
           "LEFT JOIN FETCH fs.assessor " +
           "WHERE fs.hotel.id = :hotelId " +
           "AND fs.form.id = :formId " +
           "AND fs.isDeleted = false")
    List<FormSubmission> findByHotelIdAndFormIdWithScores(
        @Param("hotelId") Long hotelId,
        @Param("formId") Long formId
    );

    // ========== NEW STATUS-BASED METHODS ==========

    /**
     * Check if an APPROVED submission exists for a specific assessor, hotel, and form.
     * Used for duplicate submission validation - only APPROVED submissions block new submissions.
     * DRAFT submissions are works-in-progress and don't count as duplicates.
     */
    @Query("SELECT CASE WHEN COUNT(fs) > 0 THEN true ELSE false END FROM FormSubmission fs " +
           "WHERE fs.form.uuid = :formUuid " +
           "AND fs.hotel.uuid = :hotelUuid " +
           "AND fs.submittedBy = :submittedBy " +
           "AND fs.status = 'APPROVED' " +
           "AND fs.isDeleted = false")
    boolean existsApprovedSubmission(
        @Param("formUuid") UUID formUuid,
        @Param("hotelUuid") UUID hotelUuid,
        @Param("submittedBy") String submittedBy
    );

    /**
     * Get all submissions for a hotel filtered by status.
     * Used for getHotelAssessmentResults to return only APPROVED submissions.
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "WHERE fs.hotel.id = :hotelId " +
           "AND fs.status = :status " +
           "AND fs.isDeleted = false " +
           "ORDER BY fs.submittedAt DESC")
    List<FormSubmission> findByHotelIdAndStatusOrderBySubmittedAtDesc(
        @Param("hotelId") Long hotelId,
        @Param("status") SubmissionStatus status
    );

    /**
     * Get all submissions for a specific assessor ordered by submission date (most recent first).
     * Used for "My Assessments" page to show an assessor all their submissions.
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "WHERE fs.assessor.id = :assessorId " +
           "AND fs.isDeleted = false " +
           "ORDER BY fs.submittedAt DESC")
    Page<FormSubmission> findByAssessorId(@Param("assessorId") Long assessorId, Pageable pageable);

    /**
     * Get ALL submissions for a specific assessor as a list (unpaginated).
     * Used for filtering to highest percentage per hotel/form combination.
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "WHERE fs.assessor.id = :assessorId " +
           "AND fs.isDeleted = false " +
           "ORDER BY fs.submittedAt DESC")
    List<FormSubmission> findByAssessorIdOrderBySubmittedAtDesc(@Param("assessorId") Long assessorId);

    /**
     * Find an existing DRAFT submission for a specific assessor, hotel, and form.
     * Used to prevent duplicate DRAFT submissions - if a DRAFT exists, update it instead of creating new.
     * Returns the most recent DRAFT if multiple exist (ordered by ID descending).
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "WHERE fs.form.uuid = :formUuid " +
           "AND fs.hotel.uuid = :hotelUuid " +
           "AND fs.submittedBy = :submittedBy " +
           "AND fs.status = 'DRAFT' " +
           "AND fs.isDeleted = false " +
           "ORDER BY fs.id DESC")
    Optional<FormSubmission> findExistingDraft(
        @Param("formUuid") UUID formUuid,
        @Param("hotelUuid") UUID hotelUuid,
        @Param("submittedBy") String submittedBy
    );

    /**
     * Find the latest submission for a specific assessor, hotel, and form.
     * Used for variance resolution to load the assessor's most recent submission.
     * Returns the most recent submission ordered by submittedAt DESC.
     * Note: Responses and section scores will be loaded lazily or via separate query in service layer.
     */
    @Query("SELECT fs FROM FormSubmission fs " +
           "WHERE fs.form.uuid = :formUuid " +
           "AND fs.hotel.uuid = :hotelUuid " +
           "AND fs.assessor.id = :assessorId " +
           "AND fs.isDeleted = false " +
           "ORDER BY fs.submittedAt DESC")
    List<FormSubmission> findLatestByFormHotelAndAssessor(
        @Param("formUuid") UUID formUuid,
        @Param("hotelUuid") UUID hotelUuid,
        @Param("assessorId") Long assessorId
    );
}
