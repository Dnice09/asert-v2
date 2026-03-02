package tz.go.mnrt.asert.modules.setup.attachmenttype.services;

import java.util.Map;
import java.util.UUID;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.attachmenttype.dtos.AttachmentTypeDto;
import tz.go.mnrt.asert.modules.setup.attachmenttype.entity.AttachmentType;
import tz.go.mnrt.asert.modules.setup.attachmenttype.repository.AttachmentTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentTypeServiceImpl extends SimpleSearchService<AttachmentType> implements AttachmentTypeService {
  private final AttachmentTypeRepository attachmentTypeRepository;

  @Override
  public AttachmentTypeDto save(AttachmentTypeDto attachmentTypeDto) {
    AttachmentType attachmentType = new AttachmentType();
    if (attachmentTypeDto.getUuid() != null) {
      attachmentType =
          attachmentTypeRepository
              .findByUuid(attachmentTypeDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "AttachmentType with uuid {" + attachmentTypeDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(attachmentTypeDto, attachmentType, "uuid");
    assert (attachmentType.getUuid() != null);
    attachmentType = attachmentTypeRepository.save(attachmentType);
    attachmentTypeDto.setId(attachmentType.getId());
    return attachmentTypeDto;
  }

  @Override
  public Page<AttachmentTypeDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated AttachmentTypes with page {} and search {} ", page, search);
    return attachmentTypeRepository
        .findAll(createSpecification(AttachmentType.class, search), page)
        .map(AttachmentTypeDto::new);
  }

  @Override
  public AttachmentTypeDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return attachmentTypeRepository
        .findByUuid(uuid)
        .map(AttachmentTypeDto::new)
        .orElseThrow(() -> new ValidationException("AttachmentType with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting AttachmentType with uuid {} ", uuid);
    attachmentTypeRepository.softDelete(uuid);
  }
}
