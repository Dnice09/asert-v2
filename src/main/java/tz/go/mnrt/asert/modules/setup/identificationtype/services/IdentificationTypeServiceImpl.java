package tz.go.mnrt.asert.modules.setup.identificationtype.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeRequestDto;
import tz.go.mnrt.asert.modules.setup.identificationtype.dtos.IdentificationTypeResponseDto;
import tz.go.mnrt.asert.modules.setup.identificationtype.entity.IdentificationType;
import tz.go.mnrt.asert.modules.setup.identificationtype.repository.IdentificationTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentificationTypeServiceImpl extends SimpleSearchService<IdentificationType> implements IdentificationTypeService {
  private final IdentificationTypeRepository identificationTypeRepository;

  @Override
  public IdentificationTypeRequestDto save(IdentificationTypeRequestDto identificationTypeRequestDto) {
    IdentificationType identificationType = new IdentificationType();
    if (identificationTypeRequestDto.getUuid() != null) {
      identificationType =
          identificationTypeRepository
              .findByUuid(identificationTypeRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "IdentificationType with uuid {" + identificationTypeRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(identificationTypeRequestDto, identificationType, "uuid");
    assert (identificationType.getUuid() != null);
    identificationType = identificationTypeRepository.save(identificationType);
    identificationTypeRequestDto.setId(identificationType.getId());
    return identificationTypeRequestDto;
  }

  @Override
  public Page<IdentificationTypeResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated IdentificationTypes with page {} and search {} ", page, search);
    return identificationTypeRepository
        .findAll(createSpecification(IdentificationType.class, search), page)
        .map(IdentificationTypeResponseDto::new);
  }

  @Override
  public IdentificationTypeResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return identificationTypeRepository
        .findByUuid(uuid)
        .map(IdentificationTypeResponseDto::new)
        .orElseThrow(() -> new ValidationException("IdentificationType with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting IdentificationType with uuid {} ", uuid);
    identificationTypeRepository.softDelete(uuid);
  }
}
