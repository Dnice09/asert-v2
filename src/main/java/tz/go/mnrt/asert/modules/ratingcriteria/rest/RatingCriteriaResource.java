package tz.go.mnrt.asert.modules.ratingcriteria.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.ratingcriteria.dtos.RatingCriteriaRequestDto;
import tz.go.mnrt.asert.modules.ratingcriteria.services.RatingCriteriaService;

@RestController
@RequestMapping(Constant.API_V1 + "/rating-criteria")
@RequiredArgsConstructor
@Slf4j
public class RatingCriteriaResource {

    final RatingCriteriaService ratingCriteriaService;

    @GetMapping
    public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving rating-criterias with pagination: {} and search criteria: {}", pagination, search);
        return CustomApiResponse.ok(
                ratingCriteriaService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CustomApiResponse create(
            @Valid @RequestBody RatingCriteriaRequestDto ratingCriteriaRequestDto,
            BindingResult bindingResult) {
        log.info("Creating new RatingCriteria: {}", ratingCriteriaRequestDto);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate business rules
        if (ratingCriteriaRequestDto.getId() != null || ratingCriteriaRequestDto.getUuid() != null) {
            List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
            if (ratingCriteriaRequestDto.getId() != null) {
                errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new RatingCriteria",
                        ratingCriteriaRequestDto.getId().toString(), "ratingCriteriaRequestDto"));
            }
            if (ratingCriteriaRequestDto.getUuid() != null) {
                errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new RatingCriteria",
                        ratingCriteriaRequestDto.getUuid().toString(), "ratingCriteriaRequestDto"));
            }
            return CustomApiResponse.validationErrors("New RatingCriteria cannot contain id or uuid", errors);
        }

        try {
            return CustomApiResponse.created("RatingCriteria created successfully",
                    ratingCriteriaService.save(ratingCriteriaRequestDto));
        } catch (Exception e) {
            log.error("Error creating RatingCriteria", e);
            return CustomApiResponse.badRequest("Failed to create RatingCriteria", e.getMessage());
        }
    }

    @PutMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CustomApiResponse update(
            @Valid @RequestBody RatingCriteriaRequestDto ratingCriteriaDto,
            BindingResult bindingResult,
            @PathVariable UUID uuid) {
        log.info("Updating RatingCriteria with UUID: {}", uuid);

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        // Validate UUID consistency
        if (ratingCriteriaDto.getUuid() == null || !Objects.equals(ratingCriteriaDto.getUuid(), uuid)) {
            return CustomApiResponse.validationError("uuid",
                    "RatingCriteria UUID must be present and equal to path UUID", uuid.toString());
        }

        try {
            return CustomApiResponse.accepted("RatingCriteria updated successfully",
                    ratingCriteriaService.save(ratingCriteriaDto));
        } catch (Exception e) {
            log.error("Error updating RatingCriteria with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to update RatingCriteria", e.getMessage());
        }
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching RatingCriteria with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(
                    ratingCriteriaService.findByUuid(uuid));
        } catch (Exception e) {
            log.error("Error fetching RatingCriteria with UUID: {}", uuid, e);
            return CustomApiResponse.notFound("RatingCriteria with UUID " + uuid + " not found");
        }
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        log.info("Deleting RatingCriteria with UUID: {}", uuid);
        try {
            ratingCriteriaService.delete(uuid);
            return CustomApiResponse.noContent("RatingCriteria deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting RatingCriteria with UUID: {}", uuid, e);
            return CustomApiResponse.badRequest("Failed to delete RatingCriteria", e.getMessage());
        }
    }
}
