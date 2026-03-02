package tz.go.mnrt.asert.modules.hotel.hotel.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.form.formsubmission.dtos.FormSubmissionSummaryDto;
import tz.go.mnrt.asert.modules.form.formsubmission.services.FormSubmissionService;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;
import tz.go.mnrt.asert.constants.Constant;

@RestController
@RequestMapping(Constant.API_V1 + "/hotel-assessments")
@RequiredArgsConstructor
@Slf4j
public class HotelAssessmentResource {
    private final FormSubmissionService formSubmissionService;
    private final HotelService hotelService;

    @GetMapping("/{hotelUuid}")
    public CustomApiResponse getHotelAssessmentResult(
            @PathVariable UUID hotelUuid) {
        return CustomApiResponse.ok(formSubmissionService.getHotelAssessmentResult(hotelUuid));
    }

    @GetMapping("/{hotelUuid}/submissions")
    public CustomApiResponse getHotelSubmissions(
            @PathVariable UUID hotelUuid,
            Pageable pageable,
            @RequestParam(required = false) String assessor) {

        Page<FormSubmissionSummaryDto> submissions = formSubmissionService.getHotelSubmissions(hotelUuid, pageable,
                assessor);
        return CustomApiResponse.ok(submissions);
    }

    @GetMapping("/{hotelUuid}/assessors")
    public CustomApiResponse getHotelAssessors(
            @PathVariable UUID hotelUuid) {

        List<AssessorResponseDto> assessors = hotelService.getHotelAssessors(hotelUuid);
        return CustomApiResponse.ok(assessors);
    }
}
