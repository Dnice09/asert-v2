package tz.go.mnrt.asert.modules.setup.documenttype.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.setup.documenttype.dto.DocumentDto;

import java.util.Map;
import java.util.UUID;

@Service
public interface DocumentTypeService {

    DocumentDto save(DocumentDto equipmentCategoryDto);

    Page<DocumentDto> findAll(Pageable page, Map<String, String> search);

    DocumentDto findByUuid(UUID id);

    void delete(UUID uuid);
}
