package tz.go.mnrt.asert.modules.assessment.document.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.assessment.document.dto.AssessorDocumentDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface AssessorDocumentService {

    AssessorDocumentDto save(AssessorDocumentDto assessorDocumentDto);

    Page<AssessorDocumentDto> findAll(Pageable page, Map<String, String> search);

    List<AssessorDocumentDto> findAll(Long assessorId);

    AssessorDocumentDto findByUuid(UUID id);

    void delete(UUID uuid);
}
