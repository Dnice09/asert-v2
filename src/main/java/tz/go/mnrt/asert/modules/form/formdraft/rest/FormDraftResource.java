package tz.go.mnrt.asert.modules.form.formdraft.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.form.formdraft.dtos.FormDraftRequestDto;
import tz.go.mnrt.asert.modules.form.formdraft.services.FormDraftService;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/form-drafts")
@RequiredArgsConstructor
@Slf4j
public class FormDraftResource {

    private final FormDraftService formDraftService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving form drafts with pagination: {} and search criteria: {}", pagination, search);

        return CustomApiResponse.ok(
                formDraftService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("lastSavedAt").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CustomApiResponse create(@Valid @RequestBody FormDraftRequestDto requestDto, BindingResult bindingResult) {
        log.info("Creating/updating form draft: {}", requestDto);

        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        try {
            return CustomApiResponse.created("Form draft saved successfully", formDraftService.save(requestDto));
        } catch (Exception e) {
            log.error("Error saving form draft", e);
            return CustomApiResponse.badRequest("Failed to save form draft: " + e.getMessage(), requestDto);
        }
    }

    @PutMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomApiResponse update(@Valid @RequestBody FormDraftRequestDto requestDto, BindingResult bindingResult,
            @PathVariable UUID uuid) {
        log.info("Updating form draft with UUID: {}", uuid);

        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Set UUID for update
        requestDto.setUuid(uuid);

        try {
            return CustomApiResponse.accepted("Form draft updated successfully", formDraftService.save(requestDto));
        } catch (Exception e) {
            log.error("Error updating form draft with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to update form draft: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching form draft with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(formDraftService.findByUuid(uuid));
        } catch (Exception e) {
            log.error("Error fetching form draft with UUID: {}", uuid, e);
            return CustomApiResponse.notFound("Form draft with UUID " + uuid + " not found");
        }
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        log.info("Deleting form draft with UUID: {}", uuid);
        try {
            formDraftService.delete(uuid);
            return CustomApiResponse.noContent("Form draft deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting form draft with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to delete form draft: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-form-and-user")
    public CustomApiResponse findByFormAndUser(
            @RequestParam UUID formUuid,
            @RequestParam String submittedBy) {
        log.info("Finding form draft by form UUID: {} and user: {}", formUuid, submittedBy);
        try {
            var draft = formDraftService.findByFormAndUser(formUuid, submittedBy);
            if (draft != null) {
                return CustomApiResponse.ok(draft);
            } else {
                return CustomApiResponse.notFound("No draft found for this form and user");
            }
        } catch (Exception e) {
            log.error("Error finding form draft", e);
            return CustomApiResponse.badRequest("Failed to find form draft: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-form-hotel-and-user")
    public CustomApiResponse findByFormHotelAndUser(
            @RequestParam UUID formUuid,
            @RequestParam UUID hotelUuid,
            @RequestParam String submittedBy) {
        log.info("Finding form draft by form UUID: {}, hotel UUID: {} and user: {}", formUuid, hotelUuid, submittedBy);
        try {
            var draft = formDraftService.findByFormHotelAndUser(formUuid, hotelUuid, submittedBy);
            if (draft != null) {
                return CustomApiResponse.ok(draft);
            } else {
                return CustomApiResponse.notFound("No draft found for this form, hotel and user");
            }
        } catch (Exception e) {
            log.error("Error finding form draft", e);
            return CustomApiResponse.badRequest("Failed to find form draft: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-user/{submittedBy}")
    public CustomApiResponse findByUser(@PathVariable String submittedBy) {
        log.info("Finding form drafts by user: {}", submittedBy);
        try {
            return CustomApiResponse.ok(formDraftService.findByUser(submittedBy));
        } catch (Exception e) {
            log.error("Error finding form drafts by user: {}", submittedBy, e);
            return CustomApiResponse.badRequest("Failed to find form drafts: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-hotel/{hotelUuid}")
    public CustomApiResponse findByHotel(@PathVariable UUID hotelUuid) {
        log.info("Finding form drafts by hotel UUID: {}", hotelUuid);
        try {
            return CustomApiResponse.ok(formDraftService.findByHotel(hotelUuid));
        } catch (Exception e) {
            log.error("Error finding form drafts by hotel UUID: {}", hotelUuid, e);
            return CustomApiResponse.badRequest("Failed to find form drafts: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-form-and-current-user")
    public CustomApiResponse findByFormAndCurrentUser(@RequestParam UUID formUuid) {
        log.info("Finding form draft by form UUID: {} and current user", formUuid);
        try {
            var draft = formDraftService.findByFormAndCurrentUser(formUuid);
            if (draft != null) {
                return CustomApiResponse.ok(draft);
            } else {
                return CustomApiResponse.notFound("No draft found for this form and user");
            }
        } catch (Exception e) {
            log.error("Error finding form draft", e);
            return CustomApiResponse.badRequest("Failed to find form draft: " + e.getMessage(), null);
        }
    }

    @GetMapping("/by-form-hotel-and-current-user")
    public CustomApiResponse findByFormHotelAndCurrentUser(
            @RequestParam UUID formUuid,
            @RequestParam UUID hotelUuid) {
        log.info("Finding form draft by form UUID: {}, hotel UUID: {} and current user", formUuid, hotelUuid);
        var draft = formDraftService.findByFormHotelAndCurrentUser(formUuid, hotelUuid);
        if (draft != null) {
            return CustomApiResponse.ok(draft);
        } else {
            return CustomApiResponse.notFound("No draft found for this form, hotel and user");
        }
    }

    @GetMapping("/by-current-user")
    public CustomApiResponse findByCurrentUser() {
        log.info("Finding form drafts by current user");
        try {
            return CustomApiResponse.ok(formDraftService.findByCurrentUser());
        } catch (Exception e) {
            log.error("Error finding form drafts by current user", e);
            return CustomApiResponse.badRequest("Failed to find form drafts: " + e.getMessage(), null);
        }
    }
}
