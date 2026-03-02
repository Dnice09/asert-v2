package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.entity.AssessorVarianceNotification;

public interface AssessorVarianceNotificationRepository
    extends BaseRepository<AssessorVarianceNotification, Long> {

  Optional<AssessorVarianceNotification> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  /**
   * Find all unread notifications for a specific assessor.
   * Uses index on assessor_id and is_read for optimal performance.
   */
  @Query("SELECT n FROM AssessorVarianceNotification n " +
         "WHERE n.assessor.id = :assessorId " +
         "AND n.isRead = false " +
         "AND n.isDeleted = false " +
         "ORDER BY n.createdAt DESC")
  List<AssessorVarianceNotification> findUnreadByAssessorId(@Param("assessorId") Long assessorId);
}
