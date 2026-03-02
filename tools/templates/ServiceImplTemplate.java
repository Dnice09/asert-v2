package tz.go.mnrt.asert.modules.#package_name.services;

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
import tz.go.mnrt.asert.modules.#package_name.dtos.#module_nameRequestDto;
import tz.go.mnrt.asert.modules.#package_name.dtos.#module_nameResponseDto;
import tz.go.mnrt.asert.modules.#package_name.entity.#module_name;
import tz.go.mnrt.asert.modules.#package_name.repository.#module_nameRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class #module_nameServiceImpl extends SimpleSearchService<#module_name> implements #module_nameService {
  private final #module_nameRepository #module_varRepository;

  @Override
  public #module_nameRequestDto save(#module_nameRequestDto #module_varRequestDto) {
    #module_name #module_var = new #module_name();
    if (#module_varRequestDto.getUuid() != null) {
      #module_var =
          #module_varRepository
              .findByUuid(#module_varRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "#module_name with uuid {" + #module_varRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(#module_varRequestDto, #module_var, "uuid");
    assert (#module_var.getUuid() != null);
    #module_var = #module_varRepository.save(#module_var);
    #module_varRequestDto.setId(#module_var.getId());
    return #module_varRequestDto;
  }

  @Override
  public Page<#module_nameResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated #module_names with page {} and search {} ", page, search);
    return #module_varRepository
        .findAll(createSpecification(#module_name.class, search), page)
        .map(#module_nameResponseDto::new);
  }

  @Override
  public #module_nameResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return #module_varRepository
        .findByUuid(uuid)
        .map(#module_nameResponseDto::new)
        .orElseThrow(() -> new ValidationException("#module_name with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting #module_name with uuid {} ", uuid);
    #module_varRepository.softDelete(uuid);
  }
}
