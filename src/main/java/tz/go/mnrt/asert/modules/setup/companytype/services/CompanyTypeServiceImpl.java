package tz.go.mnrt.asert.modules.setup.companytype.services;

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
import tz.go.mnrt.asert.modules.setup.companytype.dtos.CompanyTypeDto;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;
import tz.go.mnrt.asert.modules.setup.companytype.repository.CompanyTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyTypeServiceImpl extends SimpleSearchService<CompanyType> implements CompanyTypeService {
  private final CompanyTypeRepository companyTypeRepository;

  @Override
  public CompanyTypeDto save(CompanyTypeDto companyTypeDto) {
    CompanyType companyType = new CompanyType();
    if (companyTypeDto.getUuid() != null) {
      companyType =
          companyTypeRepository
              .findByUuid(companyTypeDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "CompanyType with uuid {" + companyTypeDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(companyTypeDto, companyType, "uuid");
    assert (companyType.getUuid() != null);
    companyType = companyTypeRepository.save(companyType);
    companyTypeDto.setId(companyType.getId());
    return companyTypeDto;
  }

  @Override
  public Page<CompanyTypeDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated CompanyTypes with page {} and search {} ", page, search);
    return companyTypeRepository
        .findAll(createSpecification(CompanyType.class, search), page)
        .map(CompanyTypeDto::new);
  }

  @Override
  public CompanyTypeDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return companyTypeRepository
        .findByUuid(uuid)
        .map(CompanyTypeDto::new)
        .orElseThrow(() -> new ValidationException("CompanyType with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting CompanyType with uuid {} ", uuid);
    companyTypeRepository.softDelete(uuid);
  }
}
