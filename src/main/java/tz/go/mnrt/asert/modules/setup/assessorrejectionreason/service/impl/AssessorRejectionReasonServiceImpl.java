package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.dto.AssessorRejectionReasonDto;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity.AssessorRejectionReason;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.repository.AssessorRejectionReasonRepository;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.repository.AssessorRejectionReasonSpecification;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.service.AssessorRejectionReasonService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssessorRejectionReasonServiceImpl implements AssessorRejectionReasonService {
    private final AssessorRejectionReasonRepository assessorRejectionReasonRepository;

    @Override
    public AssessorRejectionReasonDto save(AssessorRejectionReasonDto dto) {
        AssessorRejectionReason entity;
        if (dto.getUuid() != null) {
            // Update mode
            entity = assessorRejectionReasonRepository.findByUuid(dto.getUuid())
                .orElseThrow(() -> new ValidationException("Rejection Reason not found."));
        } else {
            // New mode
            entity = new AssessorRejectionReason();
            entity.setUuid(UUID.randomUUID());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setDeleted(false);
            // Optionally: setCreatedBy from security context
        }

        entity.setReason(dto.getReason());
        entity.setCode(dto.getCode());
        entity.setUpdatedAt(LocalDateTime.now());
        // Optionally: setUpdatedBy

        assessorRejectionReasonRepository.save(entity);

        AssessorRejectionReasonDto response = new AssessorRejectionReasonDto();
        BeanUtils.copyProperties(entity, response);
        return response;
    }

    @Override
    public Page<AssessorRejectionReasonDto> findAll(Pageable page, Map<String, String> search) {
        String filter = "";
        if (search != null && !search.isEmpty()) {
            filter = search.get("search");
        }

        Specification<AssessorRejectionReason> spec = Specification.where(AssessorRejectionReasonSpecification.notDeleted());

        if (filter != null && !filter.isBlank()) {
            spec = spec.and(AssessorRejectionReasonSpecification.search(filter));
        }

        Page<AssessorRejectionReason> results = assessorRejectionReasonRepository.findAll(spec, page);

        return results.map(AssessorRejectionReasonDto::new);
    }


    @Override
    public AssessorRejectionReasonDto findByUuid(UUID uuid) {
        AssessorRejectionReason entity = assessorRejectionReasonRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Rejection Reason not found."));
        AssessorRejectionReasonDto dto = new AssessorRejectionReasonDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public void delete(UUID uuid) {
        AssessorRejectionReason entity = assessorRejectionReasonRepository.findByUuid(uuid)
            .orElseThrow(() -> new ValidationException("Rejection Reason not found."));
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        assessorRejectionReasonRepository.save(entity);
    }
}

