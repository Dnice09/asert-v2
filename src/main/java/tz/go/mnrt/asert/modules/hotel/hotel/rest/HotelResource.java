package tz.go.mnrt.asert.modules.hotel.hotel.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelAssessorsAssignmentRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelTypeDto;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for Hotel resource operations.
 *
 * Note: The CustomApiResponse.ok(Page<T>) method has been updated to
 * automatically
 * extract page.getContent() as the data property, so response.data directly
 * contains
 * the list of items without requiring .content access.
 */
@RestController
@RequestMapping(Constant.API_V1 + "/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelResource {

    final HotelService hotelService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving hotels with pagination: {} and search criteria: {}", pagination, search);

        return CustomApiResponse.ok(
                hotelService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CustomApiResponse create(@Valid @RequestBody HotelRequestDto hotelRequestDto, BindingResult bindingResult) {
        log.info("Creating new Hotel: {}", hotelRequestDto);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate business rules
        if (hotelRequestDto.getId() != null || hotelRequestDto.getUuid() != null) {
            List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
            if (hotelRequestDto.getId() != null) {
                errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new Hotel",
                        hotelRequestDto.getId().toString(), "hotelRequestDto"));
            }
            if (hotelRequestDto.getUuid() != null) {
                errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new Hotel",
                        hotelRequestDto.getUuid().toString(), "hotelRequestDto"));
            }
            return CustomApiResponse.validationErrors("New Hotel cannot contain id or uuid", errors);
        }

        try {
            return CustomApiResponse.created("Hotel created successfully", hotelService.save(hotelRequestDto));
        } catch (Exception e) {
            log.error("Error creating Hotel", e);
            return CustomApiResponse.badRequest("Failed to create Hotel: " + e.getMessage(), hotelRequestDto);
        }
    }

    @PutMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomApiResponse update(@Valid @RequestBody HotelRequestDto hotelDto, BindingResult bindingResult,
            @PathVariable UUID uuid) {
        log.info("Updating Hotel with UUID: {}", uuid);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate UUID consistency
        if (hotelDto.getUuid() == null || !Objects.equals(hotelDto.getUuid(), uuid)) {
            return CustomApiResponse.validationError("uuid",
                    "Hotel UUID must be present and equal to path UUID", uuid.toString());
        }

        try {
            return CustomApiResponse.accepted("Hotel updated successfully", hotelService.save(hotelDto));
        } catch (Exception e) {
            log.error("Error updating Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to update Hotel: " + e.getMessage(), null);
        }
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching Hotel with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(hotelService.findByUuid(uuid));
        } catch (Exception e) {
            log.error("Error fetching Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.notFound("Hotel with UUID " + uuid + " not found");
        }
    }

    @GetMapping("/{uuid}/request-assessment")
    public CustomApiResponse requestAssessment(@PathVariable("uuid") UUID uuid) {
        log.info("Requesting assessment for Hotel with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(hotelService.requestAssessment(uuid));
        } catch (Exception e) {
            log.error("Error requesting assessment for Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to request assessment: " + e.getMessage(), null);
        }
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        log.info("Deleting Hotel with UUID: {}", uuid);
        try {
            hotelService.delete(uuid);
            return CustomApiResponse.noContent("Hotel deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to delete Hotel: " + e.getMessage(), null);
        }
    }

    @GetMapping("/new-and-assigned")
    public CustomApiResponse newAndAssigned(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving new and assigned hotels with pagination: {} and search criteria: {}", pagination, search);

        Page<?> pageResult = hotelService.newAssignedHotels(
                PageRequest.of(
                        pagination.getPageNumber(),
                        pagination.getPageSize(),
                        pagination.getSortOr(Sort.by("id").descending())),
                search);

        return CustomApiResponse.ok(
                pageResult);
    }

    @GetMapping("/get-types")
    public CustomApiResponse getHotelTypes() {
        log.info("Retrieving hotel types");
        return CustomApiResponse.ok(hotelService.getAllTypes());
    }

    @PostMapping("/{uuid}/assessors")
    public CustomApiResponse assignAssessors(
            @PathVariable UUID uuid,
            @Valid @RequestBody HotelAssessorsAssignmentRequestDto requestDto, BindingResult bindingResult) {
        log.info("Assigning assessors to Hotel with UUID: {}", uuid);
        hotelService.assignAssessorsToHotel(uuid, requestDto);

        return CustomApiResponse.ok("successfully assigned assessors");
    }

    @DeleteMapping("/{uuid}/assessors")
    public CustomApiResponse removeAssessors(
            @PathVariable UUID uuid,
            @RequestBody List<UUID> assessorUuids) {
        log.info("Removing assessors from Hotel with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(
                    hotelService.removeAssessorsFromHotel(uuid, assessorUuids));
        } catch (Exception e) {
            log.error("Error removing assessors from Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to remove assessors: " + e.getMessage(), assessorUuids);
        }
    }

    @GetMapping("/{uuid}/assessors")
    public CustomApiResponse getHotelAssessors(@PathVariable UUID uuid) {
        log.info("Retrieving assessors for Hotel with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(hotelService.getHotelAssessors(uuid));
        } catch (Exception e) {
            log.error("Error retrieving assessors for Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to retrieve assessors: " + e.getMessage(), null);
        }
    }

    @GetMapping("/import-query")
    @NoAuthorization
    public CustomApiResponse importQuery(@RequestParam(value = "tin") String tin) {
        log.info("Importing hotel query with TIN: {}", tin);
        try {
            return CustomApiResponse.ok(hotelService.importQuery(tin));
        } catch (Exception e) {
            log.error("Error importing hotel query with TIN: {}", tin, e);
            return CustomApiResponse.badRequest("Failed to import query: " + e.getMessage(), tin);
        }
    }

    @GetMapping("/get-portal-types")
    @NoAuthorization
    public CustomApiResponse getPortalTypes() {
        log.info("Retrieving portal types");
        return CustomApiResponse.ok(mapFacilities());
    }

    @GetMapping("/listing")
    @NoAuthorization
    public CustomApiResponse getHotelListing(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving hotels with pagination: {} and search criteria: {}", pagination, search);

        return CustomApiResponse.ok(
                hotelService.findPublicListing(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    public static String toReadable(String enumName) {
        return Arrays.stream(enumName.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static List<HotelTypeDto> mapFacilities() {
        return Arrays.stream(PropertyType.values())
                .map(facility -> new HotelTypeDto(
                        facility.name(),
                        toReadable(facility.name())))
                .collect(Collectors.toList());
    }

    @GetMapping("/get-facility-types")
    @NoAuthorization
    public CustomApiResponse getFacilityTypes() {
        log.info("Retrieving hotel types");
        return CustomApiResponse.ok(hotelService.getAllTypes());
    }
}
