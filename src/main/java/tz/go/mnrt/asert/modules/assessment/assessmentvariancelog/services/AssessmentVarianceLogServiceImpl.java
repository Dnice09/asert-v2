package tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import javax.validation.ValidationException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.AssessmentVarianceLogRequestDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.AssessmentVarianceLogResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.HotelVarianceGroupDto;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.dtos.HotelVarianceGroupPageResponse;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.entity.AssessmentVarianceLog;
import tz.go.mnrt.asert.modules.assessment.assessmentvariancelog.repository.AssessmentVarianceLogRepository;
import tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor;
import tz.go.mnrt.asert.modules.assessment.assessor.repository.AssessorRepository;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.entity.AssessorVarianceNotification;
import tz.go.mnrt.asert.modules.assessment.assessorvariancenotification.repository.AssessorVarianceNotificationRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.formsubmission.entity.FormSubmission;
import tz.go.mnrt.asert.modules.form.formsubmission.repository.FormSubmissionRepository;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.repository.FormSubmissionScoreRepository;
import tz.go.mnrt.asert.modules.form.formsubmissionscore.entity.FormSubmissionScore;
import tz.go.mnrt.asert.modules.systemconfiguration.repository.SystemConfigurationRepository;
import tz.go.mnrt.asert.modules.user.service.CurrentUserService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssessmentVarianceLogServiceImpl extends SimpleSearchService<AssessmentVarianceLog>
        implements AssessmentVarianceLogService {
    private final AssessmentVarianceLogRepository assessmentVarianceLogRepository;
    private final FormSubmissionRepository formSubmissionRepository;
    private final FormSubmissionScoreRepository formSubmissionScoreRepository;
    private final SystemConfigurationRepository systemConfigurationRepository;
    private final AssessorVarianceNotificationRepository assessorVarianceNotificationRepository;
    private final AssessorRepository assessorRepository;
    private final tz.go.mnrt.asert.modules.user.service.UserService userService;
    private final CurrentUserService currentUserService;
    private final HotelRepository hotelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AssessmentVarianceLogRequestDto save(AssessmentVarianceLogRequestDto assessmentVarianceLogRequestDto) {
        AssessmentVarianceLog assessmentVarianceLog = new AssessmentVarianceLog();
        if (assessmentVarianceLogRequestDto.getUuid() != null) {
            assessmentVarianceLog = assessmentVarianceLogRepository
                    .findByUuid(assessmentVarianceLogRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "AssessmentVarianceLog with uuid {" + assessmentVarianceLogRequestDto.getUuid()
                                            + "} not found"));
        }
        BeanUtils.copyProperties(assessmentVarianceLogRequestDto, assessmentVarianceLog, "uuid");
        assert (assessmentVarianceLog.getUuid() != null);
        assessmentVarianceLog = assessmentVarianceLogRepository.save(assessmentVarianceLog);
        assessmentVarianceLogRequestDto.setId(assessmentVarianceLog.getId());
        return assessmentVarianceLogRequestDto;
    }

    @Override
    public Page<AssessmentVarianceLogResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated AssessmentVarianceLogs with page {} and search {} ", page, search);

        Page<AssessmentVarianceLog> allLogs;

        // If search criteria exist, use the specification query (slower but supports filtering)
        if (search != null && !search.isEmpty()) {
            log.info("Using specification query with search criteria");
            allLogs = assessmentVarianceLogRepository
                    .findAll(createSpecification(AssessmentVarianceLog.class, search), page);
        } else {
            // For unfiltered requests, use the optimized JOIN FETCH query
            log.info("Using optimized JOIN FETCH query (no search criteria)");
            allLogs = assessmentVarianceLogRepository.findAllWithRelations(page);
        }

        // STEP 1: Get current logged-in user
        LoggedInUserDto currentUser = currentUserService.getLoggedInUser().orElse(null);

        // STEP 2: Filter variances by hotels assigned to current assessor
        List<AssessmentVarianceLog> filteredLogs = allLogs.getContent();

        if (currentUser != null) {
            log.info("Filtering variances for user: {}", currentUser.getEmail());

            // Find assessor for current user
            Assessor currentAssessor = assessorRepository.findByUserEmail(currentUser.getEmail()).orElse(null);

            if (currentAssessor != null) {
                log.info("Found assessor {} for user {}", currentAssessor.getId(), currentUser.getEmail());

                // Get all hotels assigned to this assessor
                List<Hotel> assignedHotels = hotelRepository.findByAssessorId(currentAssessor.getId());
                List<Long> assignedHotelIds = assignedHotels.stream()
                        .map(Hotel::getId)
                        .collect(Collectors.toList());

                log.info("Assessor {} is assigned to {} hotels: {}",
                        currentAssessor.getId(), assignedHotels.size(), assignedHotelIds);

                // Filter variances to only include those for assigned hotels
                filteredLogs = allLogs.getContent().stream()
                        .filter(variance -> variance.getHotel() != null &&
                                           assignedHotelIds.contains(variance.getHotel().getId()))
                        .collect(Collectors.toList());

                log.info("Filtered {} variances down to {} for assessor's assigned hotels",
                        allLogs.getContent().size(), filteredLogs.size());
            } else {
                log.warn("No assessor found for user {}, returning empty list", currentUser.getEmail());
                filteredLogs = Collections.emptyList();
            }
        } else {
            log.warn("No logged-in user found, returning all variances (fallback behavior)");
        }

        // Deduplicate by section UUID - keep only the LATEST entry for each section
        // This prevents showing multiple rows for the same section when status changes (OPEN -> PARTIALLY_RESOLVED -> RESOLVED)
        List<AssessmentVarianceLog> deduplicatedLogs = deduplicateVarianceLogs(filteredLogs);

        // Batch-load all assessors to avoid N+1 query problem
        // Collect all unique createdBy emails (lowercase for case-insensitive matching)
        List<String> createdByEmails = deduplicatedLogs.stream()
                .map(AssessmentVarianceLog::getCreatedBy)
                .filter(java.util.Objects::nonNull)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());

        // Fetch all assessors in a single query
        Map<String, Assessor> assessorsByEmail = new HashMap<>();
        if (!createdByEmails.isEmpty()) {
            log.info("Batch loading {} assessors", createdByEmails.size());
            List<Assessor> assessors = assessorRepository.findByUserEmailIn(createdByEmails);
            assessorsByEmail = assessors.stream()
                    .collect(Collectors.toMap(
                            a -> a.getUser() != null ? a.getUser().getEmail().toLowerCase() : "",
                            a -> a,
                            (a1, a2) -> a1)); // In case of duplicates, keep first
        }

        // Convert to DTOs using the pre-loaded assessors map
        final Map<String, Assessor> assessorsMap = assessorsByEmail;
        List<AssessmentVarianceLogResponseDto> dtos = deduplicatedLogs.stream()
                .map(log -> mapToDtoOptimized(log, assessorsMap))
                .collect(Collectors.toList());

        // Return as a new Page with the same pagination metadata
        return new PageImpl<>(dtos, page, allLogs.getTotalElements());
    }

    @Override
    public HotelVarianceGroupPageResponse findAllGroupedByHotel(Pageable pageable, Map<String, String> search) {
        log.info("Loading variance logs grouped by hotel with page {} and search {}", pageable, search);

        // STEP 1: Fetch ALL variances using the existing findAll logic (handles filtering, deduplication, etc.)
        // We use a large page size to get all variances, then group and paginate by hotel
        Pageable allVariancesPage = PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort());
        Page<AssessmentVarianceLogResponseDto> allVariances = findAll(allVariancesPage, search);

        log.info("Fetched {} total variances to group by hotel", allVariances.getTotalElements());

        // STEP 2: Group variances by hotel
        Map<Long, List<AssessmentVarianceLogResponseDto>> variancesByHotel = allVariances.getContent().stream()
                .collect(Collectors.groupingBy(
                        variance -> variance.getHotel().getId(),
                        LinkedHashMap::new,  // Preserve insertion order
                        Collectors.toList()
                ));

        log.info("Grouped variances into {} hotels", variancesByHotel.size());

        // STEP 3: Create HotelVarianceGroupDto for each hotel
        List<HotelVarianceGroupDto> allHotelGroups = variancesByHotel.entrySet().stream()
                .map(entry -> {
                    List<AssessmentVarianceLogResponseDto> hotelVariances = entry.getValue();
                    HotelResponseDto hotel = hotelVariances.get(0).getHotel(); // All have same hotel

                    // Count unresolved variances (OPEN or PARTIALLY_RESOLVED)
                    long unresolvedCount = hotelVariances.stream()
                            .filter(v -> "OPEN".equals(v.getStatus()) ||
                                       "PARTIALLY_RESOLVED".equals(v.getStatus()))
                            .count();

                    return HotelVarianceGroupDto.builder()
                            .hotel(hotel)
                            .variances(hotelVariances)
                            .varianceCount(hotelVariances.size())
                            .unresolvedCount((int) unresolvedCount)
                            .build();
                })
                .collect(Collectors.toList());

        // STEP 4: Sort hotel groups (by hotel name for consistency)
        allHotelGroups.sort(Comparator.comparing(group -> group.getHotel().getName()));

        // STEP 5: Paginate the hotel groups
        int totalHotels = allHotelGroups.size();
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalHotels);

        // Handle invalid page numbers
        List<HotelVarianceGroupDto> pageContent;
        if (startIndex >= totalHotels) {
            pageContent = Collections.emptyList();
        } else {
            pageContent = allHotelGroups.subList(startIndex, endIndex);
        }

        // STEP 6: Calculate pagination metadata
        int totalPages = (int) Math.ceil((double) totalHotels / size);
        long totalVariances = allVariances.getTotalElements();

        log.info("Returning page {} of {} with {} hotel groups (total {} hotels, {} variances)",
                page, totalPages, pageContent.size(), totalHotels, totalVariances);

        return HotelVarianceGroupPageResponse.builder()
                .content(pageContent)
                .page(page)
                .size(size)
                .totalElements(totalHotels)
                .totalPages(totalPages)
                .first(page == 0)
                .last(page >= totalPages - 1)
                .totalVariances(totalVariances)
                .build();
    }

    /**
     * Deduplicate variance logs by field UUID, keeping only the most important entry for each section/field.
     * This prevents UI clutter when the same section has multiple variance log entries due to status changes.
     *
     * Priority order (highest to lowest):
     * 1. OPEN or PARTIALLY_RESOLVED - These need user attention, always shown first
     * 2. RESOLVED - Already handled, shown only if no unresolved variance exists for that field
     * 3. Within same status, keep the entry with highest ID (most recent)
     *
     * @param logs List of variance logs to deduplicate
     * @return Deduplicated list with only the most important entry per section
     */
    private List<AssessmentVarianceLog> deduplicateVarianceLogs(List<AssessmentVarianceLog> logs) {
        // Group by field UUID and keep the most important entry based on status priority
        Map<UUID, AssessmentVarianceLog> latestBySection = new LinkedHashMap<>();

        for (AssessmentVarianceLog varianceLog : logs) {
            UUID sectionKey = varianceLog.getFieldUuid();

            // Skip if no field UUID (shouldn't happen, but defensive)
            if (sectionKey == null) {
                log.warn("Variance log {} has no field_uuid, skipping deduplication", varianceLog.getId());
                continue;
            }

            AssessmentVarianceLog existing = latestBySection.get(sectionKey);

            if (existing == null) {
                // No existing entry for this field, add this one
                latestBySection.put(sectionKey, varianceLog);
            } else {
                // Determine priority: OPEN/PARTIALLY_RESOLVED > RESOLVED
                boolean currentIsUnresolved = "OPEN".equals(varianceLog.getStatus()) ||
                                             "PARTIALLY_RESOLVED".equals(varianceLog.getStatus());
                boolean existingIsUnresolved = "OPEN".equals(existing.getStatus()) ||
                                              "PARTIALLY_RESOLVED".equals(existing.getStatus());

                if (currentIsUnresolved && !existingIsUnresolved) {
                    // Current is unresolved, existing is resolved → Replace with current (higher priority)
                    log.info("Replacing RESOLVED variance {} with UNRESOLVED variance {} for field {}",
                            existing.getId(), varianceLog.getId(), sectionKey);
                    latestBySection.put(sectionKey, varianceLog);
                } else if (currentIsUnresolved == existingIsUnresolved && varianceLog.getId() > existing.getId()) {
                    // Both have same resolution status, keep newer one (higher ID)
                    log.info("Replacing older variance {} with newer variance {} for field {} (same status)",
                            existing.getId(), varianceLog.getId(), sectionKey);
                    latestBySection.put(sectionKey, varianceLog);
                }
                // Otherwise keep existing (it's either unresolved or newer)
            }
        }

        log.info("Deduplicated {} variance logs down to {} unique fields",
                logs.size(), latestBySection.size());

        return new ArrayList<>(latestBySection.values());
    }

    @Override
    public AssessmentVarianceLogResponseDto findByUuid(UUID uuid) {
        log.info("Finding AssessmentVarianceLog with uuid {} using optimized query", uuid);

        // Use optimized query with JOIN FETCH to load all relationships in a single query
        // This prevents lazy loading and N+1 query problem (reduces 6+ queries to 1 query)
        return assessmentVarianceLogRepository
                .findByUuidWithRelations(uuid)
                .map(AssessmentVarianceLogResponseDto::new)
                .orElseThrow(() -> new ValidationException("AssessmentVarianceLog with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting AssessmentVarianceLog with uuid {} ", uuid);
        assessmentVarianceLogRepository.softDelete(uuid);
    }

    @Override
    public List<AssessmentVarianceLogResponseDto> detectVariances(FormSubmission newSubmission) {
        log.info("Detecting variances for submission {} (hotel={}, form={})",
                newSubmission.getId(), newSubmission.getHotel().getId(), newSubmission.getForm().getId());

        // Get configurable variance threshold (default 5.0)
        Double threshold = getVarianceThreshold();

        // Fetch all submissions for this hotel/form
        // Note: We intentionally trigger lazy loading of section scores to avoid
        // Hibernate JOIN FETCH issues
        List<FormSubmission> allSubmissions = formSubmissionRepository
                .findByHotelIdAndFormIdWithScores(
                        newSubmission.getHotel().getId(),
                        newSubmission.getForm().getId())
                .stream()
                .distinct() // Deduplicate (JOIN FETCH without DISTINCT can return duplicates)
                .collect(Collectors.toList());

        // CRITICAL: Add the new submission to the list since it may not be committed to
        // DB yet
        // This ensures we compare using the NEW scores, not old ones from a previous
        // submission
        if (!allSubmissions.contains(newSubmission)) {
            log.info("Adding new submission {} to comparison list (not yet committed to DB)",
                    newSubmission.getId());
            allSubmissions.add(newSubmission);
        }

        log.info("Loaded {} submissions", allSubmissions.size());

        // Early exit if less than 2 submissions (need at least 2 for comparison)
        if (allSubmissions.size() < 2) {
            log.info("Only {} submission(s) found, no variance detection needed", allSubmissions.size());
            return Collections.emptyList();
        }

        // Force initialization of lazy-loaded section scores for all submissions
        // This is a workaround for Hibernate JOIN FETCH issues where some collections
        // aren't initialized
        for (FormSubmission submission : allSubmissions) {
            if (submission.getSectionScores() != null) {
                submission.getSectionScores().size(); // Force Hibernate to load the collection
            }
        }

        // Filter to keep only the latest submission per assessor
        // This ensures we're only comparing current scores, not historical ones
        // and prevents duplicate variance logs when assessors make corrections
        Map<Long, FormSubmission> latestSubmissionPerAssessor = new HashMap<>();
        for (FormSubmission submission : allSubmissions) {
            if (submission.getAssessor() != null) {
                Long assessorId = submission.getAssessor().getId();
                FormSubmission existing = latestSubmissionPerAssessor.get(assessorId);

                // Keep the submission with the higher ID (most recent)
                if (existing == null || submission.getId() > existing.getId()) {
                    latestSubmissionPerAssessor.put(assessorId, submission);
                }
            }
        }

        List<FormSubmission> latestSubmissions = new ArrayList<>(latestSubmissionPerAssessor.values());
        log.info("Filtered to {} latest submissions (one per assessor) from {} total submissions",
                latestSubmissions.size(), allSubmissions.size());

        // Early exit if less than 2 unique assessors
        if (latestSubmissions.size() < 2) {
            log.info("Only {} unique assessor(s) found, no variance detection needed", latestSubmissions.size());
            return Collections.emptyList();
        }

        // Build indexed map for O(1) section score lookups
        // Structure: Map<SectionId, Map<SubmissionId, SubmissionScoreData>>
        Map<Long, Map<Long, SubmissionScoreData>> sectionScoreIndex = buildSectionScoreIndex(latestSubmissions);

        log.info("Built section score index with {} sections from {} latest submissions",
                sectionScoreIndex.size(), latestSubmissions.size());

        // Get all sections that have scores across all submissions
        // Don't rely on newSubmission.getSectionScores() because it might not be loaded
        // yet (lazy loading issue)
        List<Long> sectionsToCheck = sectionScoreIndex.keySet().stream()
                .collect(Collectors.toList());

        if (sectionsToCheck.isEmpty()) {
            log.info("No section scores found in any submission, no variance detection needed");
            return Collections.emptyList();
        }

        log.info("Checking {} sections for variances across {} submissions with threshold {}",
                sectionsToCheck.size(), allSubmissions.size(), threshold);

        // Detect variances using pairwise comparison
        List<AssessmentVarianceLog> varianceLogs = new ArrayList<>();
        List<AssessorVarianceNotification> notifications = new ArrayList<>();

        for (Long sectionId : sectionsToCheck) {
            Map<Long, SubmissionScoreData> submissionScores = sectionScoreIndex.get(sectionId);

            if (submissionScores == null || submissionScores.size() < 2) {
                log.info("Skipping section {} - only {} submission(s)", sectionId,
                        submissionScores == null ? 0 : submissionScores.size());
                continue; // Skip if this section doesn't have at least 2 submissions
            }

            // Pairwise comparison of all submissions for this section
            List<Map.Entry<Long, SubmissionScoreData>> entries = new ArrayList<>(submissionScores.entrySet());

            log.info("Comparing section {} with {} submissions", sectionId, entries.size());

            for (int i = 0; i < entries.size(); i++) {
                for (int j = i + 1; j < entries.size(); j++) {
                    Map.Entry<Long, SubmissionScoreData> entry1 = entries.get(i);
                    Map.Entry<Long, SubmissionScoreData> entry2 = entries.get(j);

                    SubmissionScoreData data1 = entry1.getValue();
                    SubmissionScoreData data2 = entry2.getValue();

                    // Calculate score difference
                    double scoreDiff = Math.abs(data1.score - data2.score);

                    log.info("Section {}: Comparing score1={} vs score2={}, diff={}, threshold={}",
                            sectionId, data1.score, data2.score, scoreDiff, threshold);

                    // Check if variance exceeds threshold
                    if (scoreDiff >= threshold) {
                        log.info("Variance detected! Section {}: score1={}, score2={}, diff={} (threshold={})",
                                sectionId, data1.score, data2.score, scoreDiff, threshold);

                        // Check if variance already exists for this field/section
                        AssessmentVarianceLog existingVariance = assessmentVarianceLogRepository
                                .findUnresolvedVarianceForField(
                                        data1.sectionUuid,
                                        newSubmission.getHotel().getId(),
                                        newSubmission.getForm().getId())
                                .orElse(null);

                        if (existingVariance == null) {
                            // Create new variance log with ALL assessors' scores for this section
                            AssessmentVarianceLog varianceLog = createVarianceLog(
                                    newSubmission, data1, data2, scoreDiff, sectionId, submissionScores);
                            varianceLogs.add(varianceLog);

                            // Create notifications for both assessors
                            notifications
                                    .add(createNotification(varianceLog, data1.assessor, data1.score, data2.score));
                            notifications
                                    .add(createNotification(varianceLog, data2.assessor, data2.score, data1.score));

                            log.info("Created variance log for section {} with difference {}", sectionId, scoreDiff);
                        } else {
                            // Update existing variance log with 3rd assessor's score if not already set
                            boolean updated = updateVarianceWithThirdAssessor(existingVariance, submissionScores, sectionId);

                            if (updated) {
                                varianceLogs.add(existingVariance); // Add to save list
                                log.info("Updated existing variance log for section {} with 3rd assessor's score", sectionId);
                            } else {
                                log.info("Variance for section {} already has 3 assessors or couldn't be updated", sectionId);
                            }
                        }
                    }
                }
            }
        }

        // Batch insert variance logs and notifications for performance
        if (!varianceLogs.isEmpty()) {
            List<AssessmentVarianceLog> savedLogs = assessmentVarianceLogRepository.saveAll(varianceLogs);

            // Link notifications to saved variance logs
            for (int i = 0; i < savedLogs.size(); i++) {
                notifications.get(i * 2).setAssessmentVarianceLog(savedLogs.get(i));
                notifications.get(i * 2 + 1).setAssessmentVarianceLog(savedLogs.get(i));
            }

            assessorVarianceNotificationRepository.saveAll(notifications);

            log.info("Created {} variance logs and {} notifications", savedLogs.size(), notifications.size());

            return savedLogs.stream()
                    .map(AssessmentVarianceLogResponseDto::new)
                    .collect(Collectors.toList());
        }

        log.info("No variances detected");
        return Collections.emptyList();
    }

    @Override
    public List<AssessmentVarianceLogResponseDto> getUnresolvedVariances(Long hotelId, Long formId) {
        log.info("Fetching unresolved variances for hotel={}, form={}", hotelId, formId);
        return assessmentVarianceLogRepository
                .findUnresolvedVariances(hotelId, formId)
                .stream()
                .map(AssessmentVarianceLogResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void resolveVariance(UUID varianceUuid) {
        log.info("Attempting to resolve variance {}", varianceUuid);
        AssessmentVarianceLog variance = assessmentVarianceLogRepository
                .findByUuid(varianceUuid)
                .orElseThrow(() -> new ValidationException("Variance not found: " + varianceUuid));

        // Get current threshold
        Double threshold = getVarianceThreshold();

        // Validate that the variance is actually resolved (score difference < threshold)
        // This prevents users from manually marking a variance as resolved when it still exceeds the threshold
        if (variance.getScoreDifference() != null && variance.getScoreDifference() >= threshold) {
            String errorMsg = String.format(
                "Cannot resolve variance %s manually. Score difference (%.1f) is still >= threshold (%.1f). " +
                "Assessors must submit corrections to bring the difference below %.1f first.",
                varianceUuid, variance.getScoreDifference(), threshold, threshold
            );
            log.warn(errorMsg);
            throw new ValidationException(errorMsg);
        }

        log.info("Variance {} is within threshold (diff: {}, threshold: {}), marking as resolved",
                varianceUuid, variance.getScoreDifference(), threshold);

        variance.setStatus("RESOLVED");
        variance.setResolvedAt(LocalDateTime.now());
        assessmentVarianceLogRepository.save(variance);

        log.info("Variance {} successfully resolved", varianceUuid);

        // CRITICAL: Update ALL submissions for this hotel/form with the correct variance flags
        // This ensures the submission list shows the correct variance status after manual resolution
        if (variance.getHotel() != null && variance.getForm() != null) {
            updateAllSubmissionsVarianceFlags(variance.getHotel().getId(), variance.getForm().getId());
        }
    }

    /**
     * Update the has_unresolved_variances and varianceCheckStatus flags for ALL submissions of a hotel/form.
     * This ensures consistency when variances are manually resolved.
     *
     * This is called after manual variance resolution to update submission flags immediately.
     *
     * @param hotelId The hotel ID
     * @param formId The form ID
     */
    private void updateAllSubmissionsVarianceFlags(Long hotelId, Long formId) {
        try {
            log.info("Updating variance flags for all submissions after manual resolution: hotel={}, form={}",
                hotelId, formId);

            // Get all submissions for this hotel/form
            List<FormSubmission> allSubmissions = formSubmissionRepository
                .findByHotelIdAndFormIdWithScores(hotelId, formId);

            if (allSubmissions.isEmpty()) {
                log.warn("No submissions found for hotel={}, form={}", hotelId, formId);
                return;
            }

            // Check if there are any remaining unresolved variances for this hotel/form
            var unresolvedVariances = getUnresolvedVariances(hotelId, formId);
            boolean hasVariances = unresolvedVariances != null && !unresolvedVariances.isEmpty();

            log.info("Found {} unresolved variances for hotel={}, form={}",
                hasVariances ? unresolvedVariances.size() : 0, hotelId, formId);

            // Update ALL submissions with the correct flags
            for (FormSubmission submission : allSubmissions) {
                submission.setHasUnresolvedVariances(hasVariances);
                submission.setVarianceCheckStatus(hasVariances ? "HAS_VARIANCE" : "NO_VARIANCE");
            }

            // Batch save all updated submissions
            formSubmissionRepository.saveAll(allSubmissions);

            log.info("Updated {} submissions with has_unresolved_variances={}, varianceCheckStatus={}",
                allSubmissions.size(), hasVariances, hasVariances ? "HAS_VARIANCE" : "NO_VARIANCE");

        } catch (Exception e) {
            log.error("Error updating variance flags for hotel={}, form={}: {}",
                hotelId, formId, e.getMessage(), e);
            // Don't fail the main transaction - variance flag updates are non-critical
        }
    }

    @Override
    @Transactional
    public void checkAndResolveVariances(FormSubmission resolutionSubmission) {
        log.info("Checking and resolving variances for submission {} (hotel={}, form={})",
                resolutionSubmission.getId(),
                resolutionSubmission.getHotel().getId(),
                resolutionSubmission.getForm().getId());

        // Get variance threshold
        Double threshold = getVarianceThreshold();

        // Fetch all open/pending variance logs for this hotel/form
        List<AssessmentVarianceLog> openVariances = assessmentVarianceLogRepository
                .findUnresolvedVariances(
                        resolutionSubmission.getHotel().getId(),
                        resolutionSubmission.getForm().getId());

        if (openVariances.isEmpty()) {
            log.info("No open variances found for this hotel/form combination");
            return;
        }

        log.info("Found {} open variances to check", openVariances.size());

        // Fetch all submissions for this hotel/form to re-check scores
        List<FormSubmission> allSubmissions = formSubmissionRepository
                .findByHotelIdAndFormIdWithScores(
                        resolutionSubmission.getHotel().getId(),
                        resolutionSubmission.getForm().getId())
                .stream()
                .distinct()
                .collect(Collectors.toList());

        // CRITICAL: Add the resolution submission to the list since it may not be
        // committed to DB yet
        // This ensures we compare using the NEW scores, not the old ones from a
        // previous submission
        if (!allSubmissions.contains(resolutionSubmission)) {
            log.info("Adding resolution submission {} to comparison list (not yet committed to DB)",
                    resolutionSubmission.getId());
            allSubmissions.add(resolutionSubmission);
        }

        // Force initialization of section scores
        for (FormSubmission submission : allSubmissions) {
            if (submission.getSectionScores() != null) {
                submission.getSectionScores().size();
            }
        }

        // Filter to keep only the latest submission per assessor
        // This ensures we're comparing current/corrected scores, not historical ones
        Map<Long, FormSubmission> latestSubmissionPerAssessor = new HashMap<>();
        for (FormSubmission submission : allSubmissions) {
            if (submission.getAssessor() != null) {
                Long assessorId = submission.getAssessor().getId();
                FormSubmission existing = latestSubmissionPerAssessor.get(assessorId);

                // Keep the submission with the higher ID (most recent)
                if (existing == null || submission.getId() > existing.getId()) {
                    latestSubmissionPerAssessor.put(assessorId, submission);
                }
            }
        }

        List<FormSubmission> latestSubmissions = new ArrayList<>(latestSubmissionPerAssessor.values());
        log.info("Filtered to {} latest submissions (one per assessor) from {} total submissions",
                latestSubmissions.size(), allSubmissions.size());

        // Build section score index using only latest submissions
        Map<Long, Map<Long, SubmissionScoreData>> sectionScoreIndex = buildSectionScoreIndex(latestSubmissions);

        // Check each open variance to see if it's still valid
        for (AssessmentVarianceLog variance : openVariances) {
            if (variance.getSection() == null) {
                log.warn("Variance {} has no section, skipping", variance.getId());
                continue;
            }

            Long sectionId = variance.getSection().getId();
            Map<Long, SubmissionScoreData> submissionScores = sectionScoreIndex.get(sectionId);

            if (submissionScores == null || submissionScores.size() < 2) {
                log.info("Section {} no longer has enough submissions, marking variance {} as resolved",
                        sectionId, variance.getId());
                variance.setStatus("RESOLVED");
                variance.setResolvedAt(LocalDateTime.now());
                continue;
            }

            // Re-check all pairwise comparisons for this section
            boolean varianceStillExists = false;
            List<Map.Entry<Long, SubmissionScoreData>> entries = new ArrayList<>(submissionScores.entrySet());
            double maxScoreDiff = 0.0;
            double originalScoreDiff = variance.getScoreDifference();

            for (int i = 0; i < entries.size(); i++) {
                for (int j = i + 1; j < entries.size(); j++) {
                    SubmissionScoreData data1 = entries.get(i).getValue();
                    SubmissionScoreData data2 = entries.get(j).getValue();

                    double scoreDiff = Math.abs(data1.score - data2.score);
                    maxScoreDiff = Math.max(maxScoreDiff, scoreDiff);

                    if (scoreDiff >= threshold) {
                        varianceStillExists = true;
                        log.info("Section {} still has variance: score1={}, score2={}, diff={}",
                                sectionId, data1.score, data2.score, scoreDiff);
                        break;
                    }
                }
                if (varianceStillExists) {
                    break;
                }
            }

            // Update the variance log with the latest scores and score difference
            updateVarianceWithLatestScores(variance, submissionScores, maxScoreDiff);

            // If variance no longer exists, mark as resolved
            if (!varianceStillExists) {
                log.info("Variance {} for section {} is now resolved (all scores within threshold {})",
                        variance.getId(), sectionId, threshold);
                variance.setStatus("RESOLVED");
                variance.setResolvedAt(LocalDateTime.now());
            } else {
                // Check if the variance has improved (score difference decreased)
                boolean hasImproved = maxScoreDiff < originalScoreDiff;

                if (hasImproved && "OPEN".equals(variance.getStatus())) {
                    log.info("Variance {} for section {} is partially resolved (diff decreased from {} to {})",
                            variance.getId(), sectionId, originalScoreDiff, maxScoreDiff);
                    variance.setStatus("PARTIALLY_RESOLVED");
                } else {
                    log.info("Variance {} for section {} still exists (max diff: {}, status: {})",
                            variance.getId(), sectionId, maxScoreDiff, variance.getStatus());
                }
            }
        }

        // Save all updated variances
        assessmentVarianceLogRepository.saveAll(openVariances);

        // CRITICAL: After resolving subsection variances, check if parent variances should also be auto-resolved
        // This ensures that when all subsections are resolved, the parent variance is also marked as resolved
        checkAndResolveParentVariances(
                resolutionSubmission.getHotel().getId(),
                resolutionSubmission.getForm().getId());

        long resolvedCount = openVariances.stream()
                .filter(v -> "RESOLVED".equals(v.getStatus()))
                .count();

        log.info("Resolved {} out of {} open variances", resolvedCount, openVariances.size());
    }

    /**
     * Check and auto-resolve parent section variances when all subsection variances are resolved.
     *
     * Parent sections are containers with no direct fields - their scores are aggregates of subsections.
     * When all subsection variances are resolved, the parent variance should also be marked as resolved.
     *
     * This method:
     * 1. Finds all parent section variances (sections with no parent_section_id)
     * 2. For each parent, checks if ALL subsection variances are RESOLVED
     * 3. If all subsections are resolved, auto-resolves the parent variance
     *
     * @param hotelId The hotel ID
     * @param formId The form ID
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void checkAndResolveParentVariances(Long hotelId, Long formId) {
        log.info("Checking parent variances for auto-resolution: hotel={}, form={}", hotelId, formId);

        try {
            // Get all unresolved variances for this hotel/form
            List<AssessmentVarianceLog> allVariances = assessmentVarianceLogRepository
                    .findUnresolvedVariances(hotelId, formId);

            if (allVariances.isEmpty()) {
                log.info("No unresolved variances found, skipping parent variance check");
                return;
            }

            // Group variances by parent section
            // We need to identify which variances are for parent sections (sectionLevel = 0, no parent_section_id)
            // and which are for subsections (have a parent_section_id)
            Map<Long, AssessmentVarianceLog> parentVariances = new HashMap<>();
            Map<Long, List<AssessmentVarianceLog>> subsectionVariancesByParent = new HashMap<>();

            for (AssessmentVarianceLog variance : allVariances) {
                if (variance.getSection() == null) {
                    log.warn("Variance {} has no section, skipping", variance.getId());
                    continue;
                }

                var section = variance.getSection();

                // Check if this is a parent section (no parent)
                if (section.getParentSection() == null) {
                    // This is a parent section variance
                    parentVariances.put(section.getId(), variance);
                    log.info("Found parent variance: id={}, section={}, sectionId={}",
                            variance.getId(), section.getTitle(), section.getId());
                } else {
                    // This is a subsection variance - group by parent section
                    Long parentSectionId = section.getParentSection().getId();
                    subsectionVariancesByParent
                            .computeIfAbsent(parentSectionId, k -> new ArrayList<>())
                            .add(variance);
                    log.info("Found subsection variance: id={}, section={}, parentSectionId={}",
                            variance.getId(), section.getTitle(), parentSectionId);
                }
            }

            log.info("Found {} parent variances and {} parent sections with subsection variances",
                    parentVariances.size(), subsectionVariancesByParent.size());

            // For each parent variance, check if all its subsections are resolved
            for (Map.Entry<Long, AssessmentVarianceLog> entry : parentVariances.entrySet()) {
                Long parentSectionId = entry.getKey();
                AssessmentVarianceLog parentVariance = entry.getValue();

                log.info("Checking parent variance {} for section {} (sectionId={})",
                        parentVariance.getId(), parentVariance.getSection().getTitle(), parentSectionId);

                // Check if there are any unresolved subsection variances for this parent
                List<AssessmentVarianceLog> subsectionVariances = subsectionVariancesByParent.get(parentSectionId);

                if (subsectionVariances == null || subsectionVariances.isEmpty()) {
                    // No unresolved subsection variances found - parent can be auto-resolved
                    log.info("✅ No unresolved subsection variances found for parent section {} - AUTO-RESOLVING parent variance {}",
                            parentVariance.getSection().getTitle(), parentVariance.getId());

                    parentVariance.setStatus("RESOLVED");
                    parentVariance.setResolvedAt(LocalDateTime.now());

                    assessmentVarianceLogRepository.save(parentVariance);

                    log.info("✅ Parent variance {} for section {} has been auto-resolved",
                            parentVariance.getId(), parentVariance.getSection().getTitle());
                } else {
                    // There are still unresolved subsection variances
                    log.info("⚠️ Parent section {} still has {} unresolved subsection variances - NOT auto-resolving",
                            parentVariance.getSection().getTitle(), subsectionVariances.size());

                    // Log which subsections are still unresolved
                    for (AssessmentVarianceLog subsectionVariance : subsectionVariances) {
                        log.info("   - Unresolved: section={}, status={}, scoreDiff={}",
                                subsectionVariance.getSection().getTitle(),
                                subsectionVariance.getStatus(),
                                subsectionVariance.getScoreDifference());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error checking parent variances for hotel={}, form={}: {}",
                    hotelId, formId, e.getMessage(), e);
            // Don't fail the main transaction - parent variance resolution is supplementary
        }
    }

    /**
     * Update the variance log with the latest assessor scores.
     * This ensures the variance log reflects current scores after partial resolution.
     *
     * @param variance The variance log to update
     * @param submissionScores Map of submission ID to score data for this section
     * @param maxScoreDiff The maximum score difference found
     */
    private void updateVarianceWithLatestScores(
            AssessmentVarianceLog variance,
            Map<Long, SubmissionScoreData> submissionScores,
            double maxScoreDiff) {

        // Update the score difference to reflect the current maximum difference
        variance.setScoreDifference(maxScoreDiff);

        // Extract the assessor scores and update the variance log
        List<SubmissionScoreData> scoreDataList = new ArrayList<>(submissionScores.values());

        // Sort by assessor ID to maintain consistent ordering
        scoreDataList.sort((a, b) -> a.assessor.getId().compareTo(b.assessor.getId()));

        // Update assessor 1
        if (scoreDataList.size() > 0) {
            SubmissionScoreData data = scoreDataList.get(0);
            variance.setAssessor1(data.assessor);
            variance.setAssessor1Score(data.score);
        }

        // Update assessor 2
        if (scoreDataList.size() > 1) {
            SubmissionScoreData data = scoreDataList.get(1);
            variance.setAssessor2(data.assessor);
            variance.setAssessor2Score(data.score);
        }

        // Update assessor 3
        if (scoreDataList.size() > 2) {
            SubmissionScoreData data = scoreDataList.get(2);
            variance.setAssessor3(data.assessor);
            variance.setAssessor3Score(data.score);
        }

        log.info("Updated variance {} with latest scores: assessor1={}, assessor2={}, assessor3={}, maxDiff={}",
                variance.getId(),
                variance.getAssessor1Score(),
                variance.getAssessor2Score(),
                variance.getAssessor3Score(),
                maxScoreDiff);
    }

    /**
     * Build indexed map of section scores for fast O(1) lookups.
     * Avoids nested loops and reduces time complexity.
     */
    private Map<Long, Map<Long, SubmissionScoreData>> buildSectionScoreIndex(List<FormSubmission> submissions) {
        Map<Long, Map<Long, SubmissionScoreData>> index = new HashMap<>();

        log.info("Building section score index from {} submissions", submissions.size());

        for (FormSubmission submission : submissions) {
            log.info("Processing submission ID={}, sectionScores={}",
                    submission.getId(),
                    submission.getSectionScores() == null ? "null" : submission.getSectionScores().size() + " scores");

            // If section scores are not loaded, load them manually
            if (submission.getSectionScores() == null) {
                log.info("Section scores not loaded for submission {}, loading manually", submission.getId());
                List<FormSubmissionScore> scores = formSubmissionScoreRepository.findBySubmissionId(submission.getId());
                submission.setSectionScores(scores);
                log.info("Loaded {} section scores for submission {}", scores.size(), submission.getId());
            }

            if (submission.getSectionScores() == null || submission.getSectionScores().isEmpty()) {
                log.warn("Skipping submission {} - no section scores found!", submission.getId());
                continue; // Skip submissions without section scores
            }

            for (FormSubmissionScore score : submission.getSectionScores()) {
                // Filter out soft-deleted scores (can't do this in query without breaking
                // Hibernate)
                if (score.isDeleted()) {
                    log.info("Skipping soft-deleted score: submission={}, section={}",
                            submission.getId(), score.getSection().getId());
                    continue;
                }

                Long sectionId = score.getSection().getId();

                index.computeIfAbsent(sectionId, k -> new HashMap<>())
                        .put(submission.getId(), new SubmissionScoreData(
                                submission.getAssessor(),
                                score.getSection().getUuid(),
                                score.getScore(),
                                score.getSection()));

                log.info("Added to index: submission={}, section={}, score={}",
                        submission.getId(), sectionId, score.getScore());
            }
        }

        log.info("Index built with {} sections", index.size());
        return index;
    }

    /**
     * Create a variance log entity with ALL assessors' scores for this section.
     */
    private AssessmentVarianceLog createVarianceLog(
            FormSubmission submission,
            SubmissionScoreData data1,
            SubmissionScoreData data2,
            double scoreDiff,
            Long sectionId,
            Map<Long, SubmissionScoreData> allSubmissionScoresForSection) {

        log.info("Creating variance log for section {} with data1.section={} (ID={})",
                sectionId, data1.section, data1.section != null ? data1.section.getId() : "NULL");

        // Get a managed reference to the section entity to avoid detached entity issues
        tz.go.mnrt.asert.modules.form.formsection.entity.FormSection managedSection = entityManager
                .getReference(tz.go.mnrt.asert.modules.form.formsection.entity.FormSection.class, sectionId);

        // Collect all assessor scores for this section
        // We'll store up to 3 assessors' scores in the variance log
        // Create a list with submission IDs for proper sorting
        List<Map.Entry<Long, SubmissionScoreData>> allScoresWithIds =
            new ArrayList<>(allSubmissionScoresForSection.entrySet());

        // Sort by submission ID to maintain consistent ordering
        allScoresWithIds.sort(Comparator.comparing(Map.Entry::getKey));

        log.info("Section {} has {} assessor scores to store", sectionId, allScoresWithIds.size());

        // Build the variance log with all available assessor scores
        AssessmentVarianceLog.AssessmentVarianceLogBuilder builder = AssessmentVarianceLog.builder()
                .hotel(submission.getHotel())
                .form(submission.getForm())
                .formField(null) // Section-level variance, not field-level
                .fieldUuid(data1.sectionUuid)
                .section(managedSection) // Use managed reference instead of detached entity
                .varianceType("SECTION_SCORE_DIFFERENCE")
                .status("OPEN")
                .scoreDifference(scoreDiff)
                .detectedAt(LocalDateTime.now());

        // Assign assessors and scores (up to 3)
        if (allScoresWithIds.size() >= 1) {
            SubmissionScoreData score1 = allScoresWithIds.get(0).getValue();
            builder.assessor1(score1.assessor);
            builder.assessor1Score(score1.score);
            log.info("Assessor 1: {} with score {}", score1.assessor.getId(), score1.score);
        }
        if (allScoresWithIds.size() >= 2) {
            SubmissionScoreData score2 = allScoresWithIds.get(1).getValue();
            builder.assessor2(score2.assessor);
            builder.assessor2Score(score2.score);
            log.info("Assessor 2: {} with score {}", score2.assessor.getId(), score2.score);
        }
        if (allScoresWithIds.size() >= 3) {
            SubmissionScoreData score3 = allScoresWithIds.get(2).getValue();
            builder.assessor3(score3.assessor);
            builder.assessor3Score(score3.score);
            log.info("Assessor 3: {} with score {}", score3.assessor.getId(), score3.score);
        }

        return builder.build();
    }

    /**
     * Update an existing variance log with the 3rd assessor's score.
     * Returns true if the variance was updated, false if it already has 3 assessors.
     */
    private boolean updateVarianceWithThirdAssessor(
            AssessmentVarianceLog existingVariance,
            Map<Long, SubmissionScoreData> allSubmissionScoresForSection,
            Long sectionId) {

        // Check if variance already has 3 assessors
        if (existingVariance.getAssessor3() != null) {
            log.info("Variance already has 3 assessors, no update needed");
            return false;
        }

        // Get all scores sorted by submission ID
        List<Map.Entry<Long, SubmissionScoreData>> allScoresWithIds =
            new ArrayList<>(allSubmissionScoresForSection.entrySet());
        allScoresWithIds.sort(Comparator.comparing(Map.Entry::getKey));

        log.info("Section {} has {} assessor scores for update", sectionId, allScoresWithIds.size());

        // We need at least 3 assessors to update with 3rd score
        if (allScoresWithIds.size() < 3) {
            log.info("Not enough assessors ({}) to update variance with 3rd score", allScoresWithIds.size());
            return false;
        }

        // Get the 3rd assessor's data (index 2, sorted by submission ID)
        SubmissionScoreData score3 = allScoresWithIds.get(2).getValue();

        // Update the variance log
        existingVariance.setAssessor3(score3.assessor);
        existingVariance.setAssessor3Score(score3.score);

        log.info("Updated variance with Assessor 3: {} with score {}",
                score3.assessor.getId(), score3.score);

        return true;
    }

    /**
     * Create a notification for an assessor about a variance.
     */
    private AssessorVarianceNotification createNotification(
            AssessmentVarianceLog varianceLog,
            tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor assessor,
            Double assessorScore,
            Double otherScore) {

        String comparisonType = assessorScore > otherScore ? "higher" : "lower";
        String message = String.format(
                "Your assessment score (%.2f) differs from another assessor's score (%.2f) by %.2f points, which exceeds the threshold. Please review and adjust if necessary.",
                assessorScore, otherScore, Math.abs(assessorScore - otherScore));

        return AssessorVarianceNotification.builder()
                .assessmentVarianceLog(varianceLog)
                .assessor(assessor)
                .notificationType("VARIANCE_DETECTED")
                .message(message)
                .isRead(false)
                .build();
    }

    /**
     * Get configurable variance threshold from system configuration.
     */
    private Double getVarianceThreshold() {
        return systemConfigurationRepository
                .findByConfigKey("variance.threshold")
                .map(config -> Double.parseDouble(config.getConfigValue()))
                .orElse(5.0); // Default to 5.0 if not configured
    }

    /**
     * Internal DTO for efficient score lookups.
     */
    private static class SubmissionScoreData {
        final tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor assessor;
        final UUID sectionUuid;
        final Double score;
        final tz.go.mnrt.asert.modules.form.formsection.entity.FormSection section;

        SubmissionScoreData(tz.go.mnrt.asert.modules.assessment.assessor.entity.Assessor assessor,
                UUID sectionUuid, Double score, tz.go.mnrt.asert.modules.form.formsection.entity.FormSection section) {
            this.assessor = assessor;
            this.sectionUuid = sectionUuid;
            this.score = score;
            this.section = section;
        }
    }

    /**
     * Optimized DTO mapping that uses a pre-loaded map of assessors.
     * Avoids N+1 query problem by not querying the database for each entity.
     */
    private AssessmentVarianceLogResponseDto mapToDtoOptimized(
            AssessmentVarianceLog entity,
            Map<String, Assessor> assessorsMap) {

        // Get assessor from pre-loaded map instead of querying database
        // Use lowercase email for case-insensitive lookup
        Assessor assessor = entity.getCreatedBy() != null
                ? assessorsMap.get(entity.getCreatedBy().toLowerCase())
                : null;

        return new AssessmentVarianceLogResponseDto(entity, assessor);
    }

    /**
     * Legacy DTO mapping method - still used by other methods.
     * WARNING: This method has N+1 query problem - use mapToDtoOptimized when possible.
     */
    private AssessmentVarianceLogResponseDto mapToDto(AssessmentVarianceLog entity) {
        // find the assessor by createBy field
        Assessor assessor = assessorRepository
                .findByUserEmail(entity.getCreatedBy())
                .orElse(null);

        AssessmentVarianceLogResponseDto dto = new AssessmentVarianceLogResponseDto(entity, assessor);
        return dto;
    }
}
