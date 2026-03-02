package tz.go.mnrt.asert.modules.hotel.hotel.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;

import java.util.*;

@RestController
@RequestMapping(Constant.API_V1 + "/listings")
@RequiredArgsConstructor
@Slf4j
public class ListingResource {

    final HotelService hotelService;

    @GetMapping
    @NoAuthorization
    public CustomApiResponse get(Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("Retrieving hotels with pagination: {} and search criteria: {}", pagination, search);

        return CustomApiResponse.ok(
                hotelService.findPublicListing(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @GetMapping("/{uuid}")
    @NoAuthorization
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        log.info("Fetching Hotel with UUID: {}", uuid);
        try {
            return CustomApiResponse.ok(hotelService.findPublicFacilityByUuid(uuid));
        } catch (Exception e) {
            log.error("Error fetching Hotel with UUID: {}", uuid, e);
            return CustomApiResponse.notFound("Hotel with UUID " + uuid + " not found");
        }
    }
}
