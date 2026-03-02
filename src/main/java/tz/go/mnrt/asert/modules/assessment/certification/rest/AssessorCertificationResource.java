package tz.go.mnrt.asert.modules.assessment.certification.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.certification.dto.AssessorCertificationDto;
import tz.go.mnrt.asert.modules.assessment.certification.service.AssessorCertificationService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-certifications")
@RequiredArgsConstructor
public class AssessorCertificationResource {

    private final AssessorCertificationService assessorCertificationService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            assessorCertificationService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("issueDate").descending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorCertificationDto assessorCertificationDto) {
        if (assessorCertificationDto.getId() != null || assessorCertificationDto.getUuid() != null) {
            throw new ValidationException("New Certificate cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Certificate created successfully",
            assessorCertificationService.save(assessorCertificationDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody AssessorCertificationDto assessorCertificationDto,
        @PathVariable UUID uuid) {

        if (assessorCertificationDto.getUuid() == null || !Objects.equals(assessorCertificationDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Certificate id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Certificate updated successfully",
            assessorCertificationService.save(assessorCertificationDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            assessorCertificationService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorCertificationService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Certificate deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

