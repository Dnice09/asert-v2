package tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.assessment.hotelassessmentapproval.entity.HotelAssessmentApproval;

public interface HotelAssessmentApprovalRepository
    extends BaseRepository<HotelAssessmentApproval, Long> {

  Optional<HotelAssessmentApproval> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  /**
   * Find approval record by hotel and form.
   * Uses UNIQUE constraint index for optimal performance.
   */
  @Query("SELECT a FROM HotelAssessmentApproval a " +
         "WHERE a.hotel.id = :hotelId " +
         "AND a.form.id = :formId " +
         "AND a.isDeleted = false")
  Optional<HotelAssessmentApproval> findByHotelIdAndFormId(
      @Param("hotelId") Long hotelId,
      @Param("formId") Long formId
  );

  /**
   * Find all approvals pending DT review.
   * Uses index on status for optimal performance.
   */
  @Query("SELECT a FROM HotelAssessmentApproval a " +
         "WHERE a.status = 'PENDING_DT_APPROVAL' " +
         "AND a.isDeleted = false " +
         "ORDER BY a.submittedToDtAt DESC")
  Page<HotelAssessmentApproval> findPendingDtApprovals(Pageable pageable);
}
