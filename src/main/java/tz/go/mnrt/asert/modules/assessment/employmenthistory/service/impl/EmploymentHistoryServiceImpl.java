package tz.go.mnrt.asert.modules.assessment.employmenthistory.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.dto.EmploymentHistoryDto;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.entity.EmploymentHistory;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.repository.EmploymentHistoryRepository;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.repository.EmploymentHistorySpecification;
import tz.go.mnrt.asert.modules.assessment.employmenthistory.service.EmploymentHistoryService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentHistoryServiceImpl implements EmploymentHistoryService {
    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final AssessorService assessorService;

    @Override
    public EmploymentHistoryDto save(EmploymentHistoryDto dto) {
        EmploymentHistory entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = employmentHistoryRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Employment History not found."));
        } else {
            // New mode
            entity = new EmploymentHistory();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setPositionHeld(dto.getPositionHeld());
        entity.setFromDate(dto.getFromDate());
        entity.setToDate(dto.getToDate());
        entity.setCompany(dto.getCompany());
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());

        employmentHistoryRepository.save(entity);

        EmploymentHistoryDto response = new EmploymentHistoryDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<EmploymentHistoryDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<EmploymentHistory> spec = Specification.where(EmploymentHistorySpecification.byAssessor(assessor.getId()).and(EmploymentHistorySpecification.notDeleted()));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(EmploymentHistorySpecification.search(filter));
        }

        Page<EmploymentHistory> results = employmentHistoryRepository.findAll(spec, page);

        return results.map(EmploymentHistoryDto::new);
    }

    @Override
    public List<EmploymentHistoryDto> findAll(Long assessorId) {
        Specification<EmploymentHistory> spec = Specification.where(EmploymentHistorySpecification.byAssessor(assessorId).and(EmploymentHistorySpecification.notDeleted()));
        return employmentHistoryRepository.findAll(spec).stream().map(EmploymentHistoryDto::new).collect(Collectors.toList());
    }

    @Override
    public EmploymentHistoryDto findByUuid(UUID uuid) {
        EmploymentHistory entity = employmentHistoryRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Employment History not found."));
        EmploymentHistoryDto dto = new EmploymentHistoryDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        EmploymentHistory entity = employmentHistoryRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Employment History not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        employmentHistoryRepository.save(entity);
    }
}

