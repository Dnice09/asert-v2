package tz.go.mnrt.asert.modules.assessment.assessmentrequest.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestCreateDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestUpdateDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.service.AssessmentRequestService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.constants.Constant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(Constant.API_V1 + "/assessment-requests")
@RequiredArgsConstructor
public class AssessmentRequestResource {

    final AssessmentRequestService assessmentRequestService;

    @PostMapping
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    public CustomApiResponse create(
            @Valid @RequestBody AssessmentRequestCreateDto createDto, BindingResult bindingResult) {

        // Handle validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            return CustomApiResponse.validationError("Validation failed", bindingResult);
        }

        return CustomApiResponse.created("Request created successfully", assessmentRequestService.create(createDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findByUuid(@PathVariable UUID uuid) {
        log.info("GET /api/v1/assessment-requests/{} - Retrieving assessment request", uuid);

        AssessmentRequestResponseDto response = assessmentRequestService.findByUuid(uuid);
        return CustomApiResponse.ok(response);
    }

    @GetMapping("/{uuid}/approve")
    public CustomApiResponse approve(@PathVariable UUID uuid) {
        log.info("GET /api/v1/assessment-requests/{} - Approving assessment request", uuid);

        AssessmentRequestResponseDto response = assessmentRequestService.approve(uuid);
        return CustomApiResponse.ok(response);
    }

    @GetMapping
    public CustomApiResponse get(
            Pageable pagination, @RequestParam Map<String, String> search) {
        log.info("GET /api/v1/assessment-requests - Retrieving assessment requests with filters: {}", search);

        return CustomApiResponse.ok(
                assessmentRequestService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

}
