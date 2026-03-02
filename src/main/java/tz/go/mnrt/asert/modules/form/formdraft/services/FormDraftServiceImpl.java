package tz.go.mnrt.asert.modules.form.formdraft.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.form.form.repository.FormRepository;
import tz.go.mnrt.asert.modules.form.formdraft.dtos.FormDraftRequestDto;
import tz.go.mnrt.asert.modules.form.formdraft.dtos.FormDraftResponseDto;
import tz.go.mnrt.asert.modules.form.formdraft.entity.FormDraft;
import tz.go.mnrt.asert.modules.form.formdraft.repository.FormDraftRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
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
public class FormDraftServiceImpl extends SimpleSearchService<FormDraft> implements FormDraftService {

    private final FormDraftRepository formDraftRepository;
    private final FormRepository formRepository;
    private final HotelRepository hotelRepository;
    private final UserService userService;

    @Override
    public FormDraftResponseDto save(FormDraftRequestDto requestDto) {
        // Get current logged-in user
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Saving form draft for form UUID: {} by current user: {}", requestDto.getFormUuid(), currentUser);

        FormDraft formDraft;

        // Check if draft already exists for this form/user combination
        if (requestDto.getHotelUuid() != null) {
            formDraft = formDraftRepository.findByFormUuidAndHotelUuidAndSubmittedBy(
                    requestDto.getFormUuid(), requestDto.getHotelUuid(), currentUser)
                    .orElse(new FormDraft());
        } else {
            formDraft = formDraftRepository.findByFormUuidAndSubmittedBy(
                    requestDto.getFormUuid(), currentUser)
                    .orElse(new FormDraft());
        }

        // Set form
        var form = formRepository.findByUuid(requestDto.getFormUuid())
                .orElseThrow(() -> new ValidationException("Form not found with UUID: " + requestDto.getFormUuid()));
        formDraft.setForm(form);

        // Set hotel if provided
        if (requestDto.getHotelUuid() != null) {
            var hotel = hotelRepository.findByUuid(requestDto.getHotelUuid())
                    .orElseThrow(
                            () -> new ValidationException("Hotel not found with UUID: " + requestDto.getHotelUuid()));
            formDraft.setHotel(hotel);
        }

        // Update draft data
        formDraft.setSubmittedBy(currentUser);
        formDraft.setCurrentSectionIndex(requestDto.getCurrentSectionIndex());
        formDraft.setFormData(requestDto.getFormData());
        formDraft.setCompletionPercentage(requestDto.getCompletionPercentage());
        formDraft.setTotalSections(requestDto.getTotalSections());
        formDraft.setLastSavedAt(LocalDateTime.now());

        // Debug logging to check what's being saved
        log.debug("Saving form data for draft UUID {}: {}", formDraft.getUuid(), requestDto.getFormData());

        // Set UUID if new draft
        if (formDraft.getUuid() == null) {
            formDraft.setUuid(UUID.randomUUID());
        }

        formDraft = formDraftRepository.save(formDraft);

        // Debug logging to check what was actually saved
        log.debug("Form data after saving to database: {}", formDraft.getFormData());

        log.info("Form draft saved successfully with UUID: {}", formDraft.getUuid());
        return new FormDraftResponseDto(formDraft);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDraftResponseDto findByUuid(UUID uuid) {
        log.info("Finding form draft by UUID: {}", uuid);
        FormDraft formDraft = formDraftRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Form draft not found with UUID: " + uuid));
        return new FormDraftResponseDto(formDraft);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FormDraftResponseDto> findAll(Pageable pageable, Map<String, String> search) {
        log.info("Finding all form drafts with search criteria: {}", search);

        Specification<FormDraft> spec = createSpecification(FormDraft.class, search);

        return formDraftRepository.findAll(spec, pageable)
                .map(FormDraftResponseDto::new);
    }

    @Override
    public void delete(UUID uuid) {
        log.info("Deleting form draft with UUID: {}", uuid);
        FormDraft formDraft = formDraftRepository.findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Form draft not found with UUID: " + uuid));

        formDraft.setDeleted(true);
        formDraftRepository.save(formDraft);
        log.info("Form draft deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public FormDraftResponseDto findByFormAndUser(UUID formUuid, String submittedBy) {
        log.info("Finding form draft by form UUID: {} and user: {}", formUuid, submittedBy);
        return formDraftRepository.findByFormUuidAndSubmittedBy(formUuid, submittedBy)
                .map(FormDraftResponseDto::new)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDraftResponseDto findByFormHotelAndUser(UUID formUuid, UUID hotelUuid, String submittedBy) {
        log.info("Finding form draft by form UUID: {}, hotel UUID: {} and user: {}", formUuid, hotelUuid, submittedBy);
        return formDraftRepository.findByFormUuidAndHotelUuidAndSubmittedBy(formUuid, hotelUuid, submittedBy)
                .map(FormDraftResponseDto::new)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDraftResponseDto> findByUser(String submittedBy) {
        log.info("Finding form drafts by user: {}", submittedBy);
        return formDraftRepository.findBySubmittedByOrderByLastSavedAtDesc(submittedBy)
                .stream()
                .map(FormDraftResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDraftResponseDto> findByHotel(UUID hotelUuid) {
        log.info("Finding form drafts by hotel UUID: {}", hotelUuid);
        return formDraftRepository.findByHotelUuidOrderByLastSavedAtDesc(hotelUuid)
                .stream()
                .map(FormDraftResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExpiredDrafts(int daysOld) {
        log.info("Deleting form drafts older than {} days", daysOld);
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);

        // This would require a custom query method in the repository
        // For now, we'll implement it as a soft delete
        log.warn("Delete expired drafts not yet implemented");
    }

    @Override
    @Transactional(readOnly = true)
    public FormDraftResponseDto findByFormAndCurrentUser(UUID formUuid) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding form draft by form UUID: {} and current user: {}", formUuid, currentUser);
        return findByFormAndUser(formUuid, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public FormDraftResponseDto findByFormHotelAndCurrentUser(UUID formUuid, UUID hotelUuid) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding form draft by form UUID: {}, hotel UUID: {} and current user: {}", formUuid, hotelUuid,
                currentUser);
        log.info("Current user email being used for draft lookup: '{}'", currentUser);

        FormDraftResponseDto result = findByFormHotelAndUser(formUuid, hotelUuid, currentUser);

        if (result == null) {
            log.warn("No draft found for user '{}' on form {} and hotel {}. This could be because:",
                    currentUser, formUuid, hotelUuid);
            log.warn("  1. User email doesn't match the submitted_by field in database");
            log.warn("  2. Draft was created by a different user");
            log.warn("  3. Draft is marked as deleted");
        } else {
            log.info("Found draft with UUID: {} for user '{}'", result.getUuid(), currentUser);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormDraftResponseDto> findByCurrentUser() {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged == null) {
            throw new ValidationException("User not authenticated");
        }

        String currentUser = logged.getEmail();
        log.info("Finding form drafts by current user: {}", currentUser);
        return findByUser(currentUser);
    }
}
