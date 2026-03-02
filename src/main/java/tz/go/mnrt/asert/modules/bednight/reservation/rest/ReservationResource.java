package tz.go.mnrt.asert.modules.bednight.reservation.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationRequestDto;
import tz.go.mnrt.asert.modules.bednight.reservation.dtos.ReservationResponseDto;
import tz.go.mnrt.asert.modules.bednight.reservation.entity.Reservation;
import tz.go.mnrt.asert.modules.bednight.reservation.services.ReservationService;

@RestController
@RequestMapping(Constant.API_V1 + "/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationResource {

  final ReservationService reservationService;

  @GetMapping()
  public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
    log.info("Retrieving reservations with pagination: {} and search criteria: {}", pagination, search);
    return CustomApiResponse.ok(
            reservationService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("id").descending())),
                search));
  }

  @PostMapping
  @Transactional
  public CustomApiResponse create(
        @Valid @RequestBody ReservationRequestDto reservationRequestDto,
        BindingResult bindingResult) {

    log.info("Creating new Reservation: {}", reservationRequestDto);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate business rules
    if (reservationRequestDto.getId() != null || reservationRequestDto.getUuid() != null) {
        List<CustomApiResponse.ValidationError> errors = new ArrayList<>();
        if (reservationRequestDto.getId() != null) {
            errors.add(new CustomApiResponse.ValidationError("id", "Must be null for new Reservation",
                reservationRequestDto.getId().toString(), "reservationRequestDto"));
        }
        if (reservationRequestDto.getUuid() != null) {
            errors.add(new CustomApiResponse.ValidationError("uuid", "Must be null for new Reservation",
                reservationRequestDto.getUuid().toString(), "reservationRequestDto"));
        }
        return CustomApiResponse.validationErrors("New Reservation cannot contain id or uuid", errors);
    }

    try {
        return CustomApiResponse.created("Reservation created successfully",
                reservationService.save(reservationRequestDto));
    } catch (Exception e) {
        log.error("Error creating Reservation", e);
        return CustomApiResponse.badRequest("Failed to create Reservation", e.getMessage());
    }
  }

  @PutMapping("/{uuid}")
  @Transactional
  public CustomApiResponse update(
        @Valid @RequestBody ReservationRequestDto reservationDto,
        BindingResult bindingResult,
        @PathVariable UUID uuid) {

    log.info("Updating Reservation with UUID: {}", uuid);

    // Handle validation errors from BindingResult
    if (bindingResult.hasErrors()) {
        return CustomApiResponse.validationError("Validation failed", bindingResult);
    }

    // Validate UUID consistency
    if (reservationDto.getUuid() == null || !Objects.equals(reservationDto.getUuid(), uuid)) {
        return CustomApiResponse.validationError("uuid",
                "Reservation UUID must be present and equal to path UUID", uuid.toString());
    }

    try {
        return CustomApiResponse.accepted("Reservation updated successfully",
                reservationService.save(reservationDto));
    } catch (Exception e) {
        log.error("Error updating Reservation with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to update Reservation", e.getMessage());
    }
  }

  @GetMapping("/{uuid}")
  public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
    log.info("Fetching Reservation with UUID: {}", uuid);
    try {
        return CustomApiResponse.ok(
                reservationService.findByUuid(uuid));
    } catch (Exception e) {
        log.error("Error fetching Reservation with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("Reservation with UUID " + uuid + " not found");
    }
  }

  @DeleteMapping("/{uuid}")
  @Transactional
  public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
    log.info("Deleting Reservation with UUID: {}", uuid);
    try {
        reservationService.delete(uuid);
        return CustomApiResponse.noContent("Reservation deleted successfully");
    } catch (Exception e) {
        log.error("Error deleting Reservation with UUID: {}", uuid, e);
        return CustomApiResponse.badRequest("Failed to delete Reservation", e.getMessage());
    }
  }

  @GetMapping("/checkout/{uuid}")
  public CustomApiResponse checkout(@PathVariable("uuid") UUID uuid) {
    log.info("Checkout Reservation with UUID: {}", uuid);
    try {
        reservationService.checkout(uuid);
        return CustomApiResponse.noContent("Reservation checked Out successfully");
    } catch (Exception e) {
        log.error("Error checkout Reservation with UUID: {}", uuid, e);
        return CustomApiResponse.notFound("Reservation with UUID " + uuid + " not found");
    }
  }
}
