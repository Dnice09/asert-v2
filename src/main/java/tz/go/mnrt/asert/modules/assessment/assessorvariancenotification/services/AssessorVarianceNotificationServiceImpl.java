package tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos.AssessorVarianceNotificationRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.dtos.AssessorVarianceNotificationResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.entity.AssessorVarianceNotification;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.repository.AssessorVarianceNotificationRepository;
import tz.go.mnrt.asert.smshelper.SmsUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssessorVarianceNotificationServiceImpl extends SimpleSearchService<AssessorVarianceNotification> implements AssessorVarianceNotificationService {
  private final AssessorVarianceNotificationRepository assessorVarianceNotificationRepository;
  private final SmsUtil smsUtil = new SmsUtil();

  @Override
  public AssessorVarianceNotificationRequestDto save(AssessorVarianceNotificationRequestDto assessorVarianceNotificationRequestDto) {
    AssessorVarianceNotification assessorVarianceNotification = new AssessorVarianceNotification();
    if (assessorVarianceNotificationRequestDto.getUuid() != null) {
      assessorVarianceNotification =
          assessorVarianceNotificationRepository
              .findByUuid(assessorVarianceNotificationRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "AssessorVarianceNotification with uuid {" + assessorVarianceNotificationRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(assessorVarianceNotificationRequestDto, assessorVarianceNotification, "uuid");
    assert (assessorVarianceNotification.getUuid() != null);
    assessorVarianceNotification = assessorVarianceNotificationRepository.save(assessorVarianceNotification);
    assessorVarianceNotificationRequestDto.setId(assessorVarianceNotification.getId());
    return assessorVarianceNotificationRequestDto;
  }

  @Override
  public Page<AssessorVarianceNotificationResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated AssessorVarianceNotifications with page {} and search {} ", page, search);
    return assessorVarianceNotificationRepository
        .findAll(createSpecification(AssessorVarianceNotification.class, search), page)
        .map(AssessorVarianceNotificationResponseDto::new);
  }

  @Override
  public AssessorVarianceNotificationResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return assessorVarianceNotificationRepository
        .findByUuid(uuid)
        .map(AssessorVarianceNotificationResponseDto::new)
        .orElseThrow(() -> new ValidationException("AssessorVarianceNotification with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting AssessorVarianceNotification with uuid {} ", uuid);
    assessorVarianceNotificationRepository.softDelete(uuid);
  }

  @Override
  @Async
  @Transactional
  public void sendVarianceNotifications(AssessmentVarianceLog varianceLog) {
    log.info("Sending variance notifications for variance log: {}", varianceLog.getUuid());

    // Collect all involved assessors
    List<Assessor> assessors = new ArrayList<>();
    if (varianceLog.getAssessor1() != null) assessors.add(varianceLog.getAssessor1());
    if (varianceLog.getAssessor2() != null) assessors.add(varianceLog.getAssessor2());
    if (varianceLog.getAssessor3() != null) assessors.add(varianceLog.getAssessor3());

    // Send notifications to each assessor
    for (Assessor assessor : assessors) {
      try {
        // Create in-app notification
        String message = createVarianceMessage(varianceLog, assessor);

        AssessorVarianceNotification notification = AssessorVarianceNotification.builder()
            .assessmentVarianceLog(varianceLog)
            .assessor(assessor)
            .notificationType("VARIANCE_ALERT")
            .message(message)
            .isRead(false)
            .build();
        notification.setUuid(UUID.randomUUID());

        assessorVarianceNotificationRepository.save(notification);
        log.info("Created in-app notification for assessor: {}", assessor.getId());

        // Send SMS notification if phone number exists
        if (assessor.getUser() != null && assessor.getUser().getPhoneNumber() != null) {
          sendSmsNotification(assessor, message);
        }
      } catch (Exception e) {
        log.error("Error sending notification to assessor {}: {}",
            assessor.getId(), e.getMessage(), e);
        // Continue with other assessors even if one fails
      }
    }

    log.info("Completed sending variance notifications for variance log: {}", varianceLog.getUuid());
  }

  @Override
  public List<AssessorVarianceNotificationResponseDto> getUnreadNotifications(Long assessorId) {
    log.info("Fetching unread notifications for assessor: {}", assessorId);
    return assessorVarianceNotificationRepository.findUnreadByAssessorId(assessorId)
        .stream()
        .map(AssessorVarianceNotificationResponseDto::new)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void markAsRead(UUID notificationUuid) {
    log.info("Marking notification as read: {}", notificationUuid);
    AssessorVarianceNotification notification = assessorVarianceNotificationRepository
        .findByUuid(notificationUuid)
        .orElseThrow(() -> new ValidationException("Notification not found: " + notificationUuid));

    notification.setIsRead(true);
    notification.setReadAt(LocalDateTime.now());
    assessorVarianceNotificationRepository.save(notification);
  }

  @Override
  @Transactional
  public void markAllAsRead(Long assessorId) {
    log.info("Marking all notifications as read for assessor: {}", assessorId);
    List<AssessorVarianceNotification> unreadNotifications =
        assessorVarianceNotificationRepository.findUnreadByAssessorId(assessorId);

    LocalDateTime now = LocalDateTime.now();
    for (AssessorVarianceNotification notification : unreadNotifications) {
      notification.setIsRead(true);
      notification.setReadAt(now);
    }

    assessorVarianceNotificationRepository.saveAll(unreadNotifications);
    log.info("Marked {} notifications as read", unreadNotifications.size());
  }

  /**
   * Create a human-readable variance message for the assessor
   */
  private String createVarianceMessage(AssessmentVarianceLog varianceLog, Assessor recipient) {
    String hotelName = varianceLog.getHotel().getName();
    String formName = varianceLog.getForm().getName();
    String sectionName = varianceLog.getSection() != null ? varianceLog.getSection().getTitle() : "Unknown Section";

    // Determine if this assessor's score is higher or lower
    Double recipientScore = getAssessorScore(varianceLog, recipient);
    String comparisonText = recipientScore != null ?
        String.format("Your score: %.1f points", recipientScore) : "Check your submission";

    return String.format(
        "Variance Alert: Score difference of %.1f points detected in '%s' section for %s - %s. %s. " +
        "Please review and make corrections if needed.",
        varianceLog.getScoreDifference(),
        sectionName,
        hotelName,
        formName,
        comparisonText
    );
  }

  /**
   * Get the specific assessor's score from the variance log
   */
  private Double getAssessorScore(AssessmentVarianceLog varianceLog, Assessor assessor) {
    if (varianceLog.getAssessor1() != null && varianceLog.getAssessor1().getId().equals(assessor.getId())) {
      return varianceLog.getAssessor1Score();
    }
    if (varianceLog.getAssessor2() != null && varianceLog.getAssessor2().getId().equals(assessor.getId())) {
      return varianceLog.getAssessor2Score();
    }
    if (varianceLog.getAssessor3() != null && varianceLog.getAssessor3().getId().equals(assessor.getId())) {
      return varianceLog.getAssessor3Score();
    }
    return null;
  }

  /**
   * Send SMS notification asynchronously
   */
  @Async
  private void sendSmsNotification(Assessor assessor, String message) {
    try {
      ArrayList<String> phoneNumbers = new ArrayList<>();
      phoneNumbers.add(assessor.getUser().getPhoneNumber());

      // Truncate message to SMS length limit (160 characters)
      String smsMessage = message.length() > 160 ?
          message.substring(0, 157) + "..." : message;

      smsUtil.sendMessage(phoneNumbers, smsMessage);
      log.info("SMS sent to assessor {}: {}", assessor.getId(), assessor.getUser().getPhoneNumber());
    } catch (Exception e) {
      log.error("Failed to send SMS to assessor {}: {}",
          assessor.getId(), e.getMessage(), e);
      // Don't throw - SMS failures should not block the notification process
    }
  }
}
