package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.dto.AssessorRejectionReasonDto;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.service.AssessorRejectionReasonService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-rejection-reasons")
@RequiredArgsConstructor
public class AssessorRejectionReasonResource {

    private final AssessorRejectionReasonService assessorRejectionReasonService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            assessorRejectionReasonService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("code").ascending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody AssessorRejectionReasonDto assessorRejectionReasonDto) {
        if (assessorRejectionReasonDto.getId() != null || assessorRejectionReasonDto.getUuid() != null) {
            throw new ValidationException("New Rejection Reason cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Rejection Reason created successfully",
            assessorRejectionReasonService.save(assessorRejectionReasonDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody AssessorRejectionReasonDto assessorRejectionReasonDto,
        @PathVariable UUID uuid) {

        if (assessorRejectionReasonDto.getUuid() == null || !Objects.equals(assessorRejectionReasonDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Rejection Reason id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Rejection Reason updated successfully",
            assessorRejectionReasonService.save(assessorRejectionReasonDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            assessorRejectionReasonService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        assessorRejectionReasonService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Rejection Reason deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

