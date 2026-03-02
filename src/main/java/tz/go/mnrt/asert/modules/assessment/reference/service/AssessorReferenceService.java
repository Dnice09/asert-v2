package tz.go.mnrt.asert.modules.assessment.reference.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.reference.dto.AssessorReferenceDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface AssessorReferenceService {

    AssessorReferenceDto save(AssessorReferenceDto assessorReferenceDto);

    Page<AssessorReferenceDto> findAll(Pageable page, Map<String, String> search);

    List<AssessorReferenceDto> findAll(Long assessorId);

    AssessorReferenceDto findByUuid(UUID id);

    void delete(UUID uuid);
}
