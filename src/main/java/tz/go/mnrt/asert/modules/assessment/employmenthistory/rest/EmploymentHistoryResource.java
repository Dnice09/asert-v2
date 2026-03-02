package tz.go.mnrt.asert.modules.assessment.employmenthistory.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.dto.EmploymentHistoryDto;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.service.EmploymentHistoryService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/assessor-employment-history")
@RequiredArgsConstructor
public class EmploymentHistoryResource {

    private final EmploymentHistoryService employmentHistoryService;

    @GetMapping
    public ResponseEntity<CustomApiResponse> get(
        Pageable pagination,
        @RequestParam Map<String, String> search) {

        CustomApiResponse response = CustomApiResponse.ok(
            employmentHistoryService.findAll(
                PageRequest.of(
                    pagination.getPageNumber(),
                    pagination.getPageSize(),
                    pagination.getSortOr(Sort.by("fromDate").descending())),
                search)
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<CustomApiResponse> create(@Valid @RequestBody EmploymentHistoryDto employmentHistoryDto) {
        if (employmentHistoryDto.getId() != null || employmentHistoryDto.getUuid() != null) {
            throw new ValidationException("New Employment History cannot contain id or uuid");
        }
        CustomApiResponse response = CustomApiResponse.created(
            "Employment History created successfully",
            employmentHistoryService.save(employmentHistoryDto)
        );
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> update(
        @Valid @RequestBody EmploymentHistoryDto employmentHistoryDto,
        @PathVariable UUID uuid) {

        if (employmentHistoryDto.getUuid() == null || !Objects.equals(employmentHistoryDto.getUuid(), uuid)) {
            throw new ValidationException(
                "Employment History id must be present and equals to path id {" + uuid + "}");
        }

        CustomApiResponse response = CustomApiResponse.accepted(
            "Employment History updated successfully",
            employmentHistoryService.save(employmentHistoryDto)
        );
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CustomApiResponse> findById(@PathVariable UUID uuid) {
        CustomApiResponse response = CustomApiResponse.ok(
            employmentHistoryService.findByUuid(uuid)
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public ResponseEntity<CustomApiResponse> delete(@PathVariable UUID uuid) {
        employmentHistoryService.delete(uuid);
        CustomApiResponse response = CustomApiResponse.noContent("Employment History deleted successfully");
        return ResponseEntity.status(204).body(response);
    }
}

