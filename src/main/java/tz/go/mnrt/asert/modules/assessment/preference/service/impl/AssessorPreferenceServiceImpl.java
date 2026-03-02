package tz.go.mnrt.asert.modules.assessment.preference.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorDto;
import tz.go.mnrt.asert.modules.assessment.assessor.service.AssessorService;
import tz.go.mnrt.asert.modules.assessment.preference.dto.AssessorPreferenceDto;
import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;
import tz.go.mnrt.asert.modules.assessment.preference.repository.AssessorPreferenceRepository;
import tz.go.mnrt.asert.modules.assessment.preference.repository.AssessorPreferenceSpecification;
import tz.go.mnrt.asert.modules.assessment.preference.service.AssessorPreferenceService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessorPreferenceServiceImpl implements AssessorPreferenceService {
    private final AssessorPreferenceRepository assessorPreferenceRepository;
    private final AssessorService assessorService;

    @Override
    public AssessorPreferenceDto save(AssessorPreferenceDto dto) {
        AssessorPreference entity;
        if (dto.getUuid() != null) {
            entity = assessorPreferenceRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Preference not found."));
        } else {
            // New mode
            entity = new AssessorPreference();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
        }
        AssessorDto assessor = assessorService.getCurrentAssessor();
        entity.setPreference(dto.getPreference());
        entity.setAssessorId(assessor.getId());
        entity.setUpdatedAt(LocalDateTime.now());

        assessorPreferenceRepository.save(entity);

        AssessorPreferenceDto response = new AssessorPreferenceDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<AssessorPreferenceDto> findAll(Pageable page, Map<String, String> search) {
        AssessorDto assessor = assessorService.getCurrentAssessor();
        Specification<AssessorPreference> spec = Specification.where(AssessorPreferenceSpecification.byAssessor(assessor.getId()));
        Page<AssessorPreference> results = assessorPreferenceRepository.findAll(spec, page);
        return results.map(AssessorPreferenceDto::new);
    }

    @Override
    public List<AssessorPreferenceDto> findAll(Long assessorId) {
        Specification<AssessorPreference> spec = Specification.where(AssessorPreferenceSpecification.byAssessor(assessorId));
        return assessorPreferenceRepository.findAll(spec).stream().map(AssessorPreferenceDto::new).collect(Collectors.toList());
    }

    @Override
    public AssessorPreferenceDto findByUuid(UUID uuid) {
        AssessorPreference entity = assessorPreferenceRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Preference not found."));
        AssessorPreferenceDto dto = new AssessorPreferenceDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        AssessorPreference entity = assessorPreferenceRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Preference not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        assessorPreferenceRepository.save(entity);
    }
}

