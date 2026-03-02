package tz.go.mnrt.asert.modules.assessment.assessmentrequest.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestCreateDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestResponseDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.dto.AssessmentRequestUpdateDto;
import tz.go.mnrt.asert.modules.assessment.assessmentrequest.entity.AssessmentRequestStatus;

import java.util.Map;
import java.util.UUID;

public interface AssessmentRequestService {

    AssessmentRequestResponseDto create(AssessmentRequestCreateDto createDto);

    AssessmentRequestResponseDto findByUuid(UUID uuid);

    Page<AssessmentRequestResponseDto> findAll(Pageable page, Map<String, String> search);

    AssessmentRequestResponseDto approve(UUID uuid);

}
