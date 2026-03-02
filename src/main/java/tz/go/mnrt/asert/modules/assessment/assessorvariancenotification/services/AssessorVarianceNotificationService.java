package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos.AssessorVarianceNotificationRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos.AssessorVarianceNotificationResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;

public interface AssessorVarianceNotificationService {

  AssessorVarianceNotificationRequestDto save(AssessorVarianceNotificationRequestDto assessorVarianceNotificationDto);

  Page<AssessorVarianceNotificationResponseDto> findAll(Pageable page, Map<String, String> search);

  AssessorVarianceNotificationResponseDto findByUuid(UUID id);

  void delete(UUID uuid);

  /**
   * Send multi-channel notifications to assessors about detected variances.
   * Sends both in-app notifications (stored in DB) and SMS alerts.
   *
   * @param varianceLog The variance log containing information about the detected variance
   */
  void sendVarianceNotifications(AssessmentVarianceLog varianceLog);

  /**
   * Get unread notifications for a specific assessor.
   *
   * @param assessorId The assessor ID
   * @return List of unread notifications
   */
  List<AssessorVarianceNotificationResponseDto> getUnreadNotifications(Long assessorId);

  /**
   * Mark a notification as read.
   *
   * @param notificationUuid The notification UUID
   */
  void markAsRead(UUID notificationUuid);

  /**
   * Mark all notifications as read for an assessor.
   *
   * @param assessorId The assessor ID
   */
  void markAllAsRead(Long assessorId);
}

