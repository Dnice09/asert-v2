package tz.go.mnrt.asert.modules.setup.attachmenttype.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.setup.attachmenttype.dtos.AttachmentTypeDto;

import java.util.Map;
import java.util.UUID;

public interface AttachmentTypeService {

  AttachmentTypeDto save(AttachmentTypeDto attachmentTypeDto);

  Page<AttachmentTypeDto> findAll(Pageable page, Map<String, String> search);

  AttachmentTypeDto findByUuid(UUID id);

  void delete(UUID uuid);
}
