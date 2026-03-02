package tz.go.mnrt.asert.modules.selfassessment.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentDraftRequestDto;
import tz.go.mnrt.asert.modules.selfassessment.dtos.SelfAssessmentDraftResponseDto;
import tz.go.mnrt.asert.modules.selfassessment.entity.SelfAssessmentDraft;
import tz.go.mnrt.asert.modules.selfassessment.repository.SelfAssessmentDraftRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;
import javax.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SelfAssessmentDraftServiceImpl extends SimpleSearchService implements SelfAssessmentDraftService {

    private final SelfAssessmentDraftRepository draftRepository;
    private final FormRepository formRepository;
    private final HotelRepository hotelRepository;
    private final UserService userService;

    @Override
    public SelfAssessmentDraftResponseDto save(SelfAssessmentDraftRequestDto requestDto) {
        // Get current logged-in user
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Saving self-assessment draft for form UUID: {} by current user: {}", requestDto.getFormUuid(), currentUser);

        SelfAssessmentDraft draft;

        // Check if draft already exists for this form/user combination
        if (requestDto.getHotelUuid() != null) {
            draft = draftRepository.findByFormUuidAndHotelUuidAndSubmittedBy(
                    requestDto.getFormUuid(), requestDto.getHotelUuid(), currentUser)
                    .orElse(new SelfAssessmentDraft());
        } else {
            draft = draftRepository.findByFormUuidAndSubmittedBy(
                    requestDto.getFormUuid(), currentUser)
                    .orElse(new SelfAssessmentDraft());
        }

        // Set form
        var form = formRepository.findByUuid(requestDto.getFormUuid())
                .orElseThrow(() -> new ValidationException("Form not found with UUID: " + requestDto.getFormUuid()));
        draft.setForm(form);

        // Set hotel if provided
        if (requestDto.getHotelUuid() != null) {
            var hotel = hotelRepository.findByUuid(requestDto.getHotelUuid())
                    .orElseThrow(
                            () -> new ValidationException("Hotel not found with UUID: " + requestDto.getHotelUuid()));
            draft.setHotel(hotel);
        }

        // Update draft data
        draft.setSubmittedBy(currentUser);
        draft.setCurrentSectionIndex(requestDto.getCurrentSectionIndex());
        draft.setFormData(requestDto.getFormData());
        draft.setCompletionPercentage(requestDto.getCompletionPercentage());
        draft.setTotalSections(requestDto.getTotalSections());
        draft.setLastSavedAt(LocalDateTime.now());

        // Debug logging to check what's being saved
        log.debug("Saving form data for draft UUID {}: {}", draft.getUuid(), requestDto.getFormData());

        // Set UUID if new draft
        if (draft.getUuid() == null) {
            draft.setUuid(UUID.randomUUID());
        }

        draft = draftRepository.save(draft);

        // Debug logging to check what was actually saved
        log.debug("Form data after saving to database: {}", draft.getFormData());

        log.info("Self-assessment draft saved successfully with UUID: {}", draft.getUuid());
        return new SelfAssessmentDraftResponseDto(draft);
    }

    @Override
    @Transactional(readOnly = true)
    public SelfAssessmentDraftResponseDto findByUuid(UUID uuid) {
        log.info("Finding self-assessment draft by UUID: {}", uuid);
        SelfAssessmentDraft draft = draftRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment draft not found with UUID: " + uuid));
        return new SelfAssessmentDraftResponseDto(draft);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SelfAssessmentDraftResponseDto> findAll(Pageable pageable, Map<String, String> search) {
        log.info("Finding all self-assessment drafts with search criteria: {}", search);

        Specification<SelfAssessmentDraft> spec = createSpecification(SelfAssessmentDraft.class, search);

        return draftRepository.findAll(spec, pageable)
                .map(SelfAssessmentDraftResponseDto::new);
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting self-assessment draft with UUID: {}", uuid);
        SelfAssessmentDraft draft = draftRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Self-assessment draft not found with UUID: " + uuid));

        draft.setDeleted(true);
        draftRepository.save(draft);
        log.info("Self-assessment draft deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public SelfAssessmentDraftResponseDto findByFormAndUser(UUID formUuid, String submittedBy) {
        log.info("Finding self-assessment draft by form UUID: {} and user: {}", formUuid, submittedBy);
        return draftRepository.findByFormUuidAndSubmittedBy(formUuid, submittedBy)
                .map(SelfAssessmentDraftResponseDto::new)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public SelfAssessmentDraftResponseDto findByFormHotelAndUser(UUID formUuid, UUID hotelUuid, String submittedBy) {
        log.info("Finding self-assessment draft by form UUID: {}, hotel UUID: {} and user: {}", formUuid, hotelUuid, submittedBy);
        return draftRepository.findByFormUuidAndHotelUuidAndSubmittedBy(formUuid, hotelUuid, submittedBy)
                .map(SelfAssessmentDraftResponseDto::new)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SelfAssessmentDraftResponseDto> findByUser(String submittedBy) {
        log.info("Finding self-assessment drafts by user: {}", submittedBy);
        return draftRepository.findBySubmittedByOrderByLastSavedAtDesc(submittedBy)
                .stream()
                .map(SelfAssessmentDraftResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SelfAssessmentDraftResponseDto> findByHotel(UUID hotelUuid) {
        log.info("Finding self-assessment drafts by hotel UUID: {}", hotelUuid);
        return draftRepository.findByHotelUuidOrderByLastSavedAtDesc(hotelUuid)
                .stream()
                .map(SelfAssessmentDraftResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExpiredDrafts(int daysOld) {
        log.info("Deleting self-assessment drafts older than {} days", daysOld);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);

        // This would require a custom query method in the repository
        // For now, we'll implement it as a soft delete
        log.warn("Delete expired drafts not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public SelfAssessmentDraftResponseDto findByFormAndCurrentUser(UUID formUuid) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding self-assessment draft by form UUID: {} and current user: {}", formUuid, currentUser);
        return findByFormAndUser(formUuid, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public SelfAssessmentDraftResponseDto findByFormHotelAndCurrentUser(UUID formUuid, UUID hotelUuid) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding self-assessment draft by form UUID: {}, hotel UUID: {} and current user: {}", formUuid, hotelUuid, currentUser);
        return findByFormHotelAndUser(formUuid, hotelUuid, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SelfAssessmentDraftResponseDto> findByCurrentUser() {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding self-assessment drafts by current user: {}", currentUser);
        return findByUser(currentUser);
    }
}
