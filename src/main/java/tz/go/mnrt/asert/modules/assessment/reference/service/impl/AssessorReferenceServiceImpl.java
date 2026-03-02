package tz.go.mnrt.asert.modules.assessment.reference.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;
import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;
import tz.go.mnrt.asert.modules.assessment.reference.repository.AssessorReferenceRepository;
import tz.go.mnrt.asert.modules.assessment.reference.repository.AssessorReferenceSpecification;
import tz.go.mnrt.asert.modules.assessment.reference.service.AssessorReferenceService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessorReferenceServiceImpl implements AssessorReferenceService {
    private final AssessorReferenceRepository assessorReferenceRepository;
    private final AssessorService assessorService;

    @Override
    public AssessorReferenceDto save(AssessorReferenceDto dto) {
        AssessorReference entity;
        if (dto.getUuid() != null) {
            entity = assessorReferenceRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Reference not found."));
        } else {
            // New mode
            entity = new AssessorReference();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setRelationship(dto.getRelationship());
        entity.setTitle(dto.getTitle());
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());

        assessorReferenceRepository.save(entity);

        AssessorReferenceDto response = new AssessorReferenceDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<AssessorReferenceDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<AssessorReference> spec = Specification.where(AssessorReferenceSpecification.byAssessor(assessor.getId()).and(AssessorReferenceSpecification.notDeleted()));

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorReferenceSpecification.search(filter));
        }

        Page<AssessorReference> results = assessorReferenceRepository.findAll(spec, page);

        return results.map(AssessorReferenceDto::new);
    }

    @Override
    public List<AssessorReferenceDto> findAll(Long assessorId) {
        Specification<AssessorReference> spec = Specification.where(AssessorReferenceSpecification.byAssessor(assessorId).and(AssessorReferenceSpecification.notDeleted()));
        return assessorReferenceRepository.findAll(spec).stream().map(AssessorReferenceDto::new).collect(Collectors.toList());
    }

    @Override
    public AssessorReferenceDto findByUuid(UUID uuid) {
        AssessorReference entity = assessorReferenceRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Reference not found."));
        AssessorReferenceDto dto = new AssessorReferenceDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        AssessorReference entity = assessorReferenceRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Reference not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        assessorReferenceRepository.save(entity);
    }
}

